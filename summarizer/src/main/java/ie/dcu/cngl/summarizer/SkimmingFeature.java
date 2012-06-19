package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokeniser.SectionInfo;
import ie.dcu.cngl.tokeniser.Sentenizer;
import ie.dcu.cngl.tokeniser.TokenInfo;
import ie.dcu.cngl.tokeniser.Tokenizer;
import ie.dcu.cngl.tokeniser.TokenizerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class SkimmingFeature extends Feature {
	
	private Analyzer analyzer;
	private HashMap<Integer, SectionInfo> sentenceMap;
	private float topTermCutoff;
	// these two values are used to implement a simple linear deboost. If 
	// a different algorithm is desired, these variables are likely to be
	// no longer required.
	private float sentenceDeboost;
	private float sentenceDeboostBase = 0.5F;

	public SkimmingFeature(Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) {
		super(tokens, sentences, paragraphs);
		try {
			this.analyzer = new SummaryAnalyzer();
		} catch (IOException e) {
			
		}
		sentenceMap = new HashMap<Integer, SectionInfo>();
		for(SectionInfo sentence : sentences) {
			sentenceMap.put(sentence.hashCode(), sentence);
		}
	}

	public void setTopTermCutoff(float topTermCutoff) {
		if (topTermCutoff < 0.0F || topTermCutoff > 1.0F) {
			throw new IllegalArgumentException(
			"Invalid value: 0.0F <= topTermCutoff <= 1.0F");
		}
		this.topTermCutoff = topTermCutoff;
	}

	public void setSentenceDeboost(float sentenceDeboost) {
		if (sentenceDeboost < 0.0F || sentenceDeboost > 1.0F) {
			throw new IllegalArgumentException(
			"Invalid value: 0.0F <= sentenceDeboost <= 1.0F");
		}
		this.sentenceDeboost = sentenceDeboost;
	}

	public void setSentenceDeboostBase(float sentenceDeboostBase) {
		if (sentenceDeboostBase < 0.0F || sentenceDeboostBase > 1.0F) {
			throw new IllegalArgumentException(
			"Invalid value: 0.0F <= sentenceDeboostBase <= 1.0F");
		}
		this.sentenceDeboostBase = sentenceDeboostBase;
	}


	@Override
	public Double [] getWeights() {
		Double[] weights = null;
		try {
			RAMDirectory ramdir = new RAMDirectory();
			buildIndex(ramdir);
			Query topTermQuery = computeTopTermQuery(ramdir);
			weights = searchIndex(ramdir, topTermQuery);
			normalise(weights);
		} catch (Exception e) {}
		
		return weights;
	}

	private void buildIndex(Directory ramdir) throws Exception {
		Tokenizer tokenizer = new Tokenizer();
		Sentenizer sentenceTokenizer = new Sentenizer(tokenizer);
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_36, analyzer);
		IndexWriter writer = new IndexWriter(ramdir, config);
		int pno = 0;
		for(SectionInfo paragraph : paragraphs) {
			Vector<Vector<TokenInfo>> sentences = sentenceTokenizer.sentenizeTokens(paragraph.getValue());
			Vector<String> strSentences = TokenizerUtils.recombineTokens(sentences);
			int sno = 0;
			for(String sentence : strSentences) {
				Document doc = new Document();
				doc.add(new Field("text", sentence, Store.YES, Index.ANALYZED));
				doc.setBoost(computeDeboost(pno, sno));
				writer.addDocument(doc);
				sno++;
			}
			pno++;
		}
		writer.commit();
		writer.close();
	}

	private float computeDeboost(int paragraphNumber, int sentenceNumber) {
		if (paragraphNumber > 0) {
			if (sentenceNumber > 0) {
				float deboost = 1.0F - (sentenceNumber * sentenceDeboost);
				deboost = (deboost < sentenceDeboostBase) ? sentenceDeboostBase : deboost; 
				return deboost;
			}
		}
		return 1.0F;
	}

	private Query computeTopTermQuery(Directory ramdir) throws Exception {
		final Map<String,Integer> frequencyMap = new HashMap<String,Integer>();
		List<String> termlist = new ArrayList<String>();
		IndexReader reader = IndexReader.open(ramdir);
		TermEnum terms = reader.terms();
		while (terms.next()) {
			Term term = terms.term();
			String termText = term.text();
			int frequency = reader.docFreq(term);
			frequencyMap.put(termText, frequency);
			termlist.add(termText);
		}
		reader.close();

		// sort the term map by frequency descending
		Collections.sort(termlist, new Comparator<String>() {
			@Override
			public int compare(String term1, String term2) {
				int term1Freq = frequencyMap.get(term1);
				int term2Freq = frequencyMap.get(term2);

				if(term1Freq < term2Freq) return 1;
				if(term1Freq > term2Freq) return -1;
				return 0;
			}
		});

		// retrieve the top terms based on topTermCutoff
		List<String> topTerms = new ArrayList<String>();
		float topFreq = -1.0F;
		for (String term : termlist) {
			if (topFreq < 0.0F) {
				// first term, capture the value
				topFreq = (float) frequencyMap.get(term);
				topTerms.add(term);
			} else {
				// not the first term, compute the ratio and discard if below
				// topTermCutoff score
				float ratio = (float) ((float) frequencyMap.get(term) / topFreq);
				if (ratio >= topTermCutoff) {
					topTerms.add(term);
				} else {
					break;
				}
			}
		}
		
		BooleanQuery query = new BooleanQuery();
		for (String topTerm : topTerms) {
			query.add(new TermQuery(new Term("text", topTerm)), Occur.SHOULD);
		}

		return query;
	}

	private Double[] searchIndex(Directory ramdir, Query query) throws Exception {
		Double [] weights = new Double[sentences.size()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 0.0;
		}
		IndexReader reader = IndexReader.open(ramdir);
		IndexSearcher searcher = new IndexSearcher(reader);
		
		TopDocs topDocs = searcher.search(query, sentences.size());
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			int docId = scoreDoc.doc;
			Document doc = searcher.doc(docId);
			String sentence = StringUtils.chomp(doc.get("text"));
			SectionInfo sentenceInfo = sentenceMap.get(sentence.hashCode());
			weights[sentenceInfo.getLocation()] = (double)scoreDoc.score;
		}
		searcher.close();
		
		return weights;
	}

}
