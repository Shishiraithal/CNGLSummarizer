package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokeniser.SectionInfo;
import ie.dcu.cngl.tokeniser.TokenInfo;
import ie.dcu.cngl.tokeniser.TokenizerUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.Vector;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;

public abstract class Feature {
	
	protected Analyzer analyzer;
	protected Vector<TokenInfo> tokens;
	protected Vector<SectionInfo> sentences; 
	protected Vector<SectionInfo> paragraphs;
	
	public Feature(Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) throws IOException {
		this.analyzer = new SummaryAnalyzer();
		this.setTokens(tokens);
		this.setSentences(sentences);
		this.setParagraphs(paragraphs);
	}
	
	public abstract Double[] getWeights();
	
	protected int getNumOccurences(String str, String longerStr) {
		int len = str.length();
		int result = 0;
		if (len > 0) {  
			int start = longerStr.indexOf(str);
			while (start != -1) {
				result++;
				start = longerStr.indexOf(str, start+len);
			}
		}
		return result;
	}
	
	protected double numberOfTerms(Vector<TokenInfo> sentence) {
		double numTerms = 0;

		Vector<Vector<TokenInfo>> sentenceHolder = new Vector<Vector<TokenInfo>>();
		sentenceHolder.add(sentence);
		StringReader reader = new StringReader(TokenizerUtils.recombineTokens(sentenceHolder).get(0));
		TokenStream tokenStream = analyzer.tokenStream(null, reader);

		try {
			while (tokenStream.incrementToken()) {
				numTerms++;
			}
		} catch (IOException e) {}
		
		return numTerms;
	}
	
	protected void normalise(Double[] weights) {
		double max = getMax(weights);
		if(max != 0) {
			for(int i = 0; i < weights.length; i++) {
				weights[i]/=max;
			}
		}
	}

	private double getMax(Double[] weights) {
		double max = weights[0];
		for(int i = 1; i < weights.length; i++) {
			if(weights[i] > max) {
				max = weights[i];
			}
		}
		return max;
	}

	public void setParagraphs(Vector<SectionInfo> paragraphs) {
		this.paragraphs = paragraphs;
	}

	public Vector<SectionInfo> getParagraphs() {
		return paragraphs;
	}

	public void setSentences(Vector<SectionInfo> sentences) {
		this.sentences = sentences;
	}

	public Vector<SectionInfo> getSentences() {
		return sentences;
	}

	public void setTokens(Vector<TokenInfo> tokens) {
		this.tokens = tokens;
	}

	public Vector<TokenInfo> getTokens() {
		return tokens;
	}
	
}
