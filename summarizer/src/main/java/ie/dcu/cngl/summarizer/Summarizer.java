package ie.dcu.cngl.summarizer;

import java.util.Vector;

import org.apache.commons.lang.StringUtils;

import ie.dcu.cngl.tokeniser.Paragrapher;
import ie.dcu.cngl.tokeniser.SectionInfo;
import ie.dcu.cngl.tokeniser.Sentenizer;
import ie.dcu.cngl.tokeniser.TokenInfo;
import ie.dcu.cngl.tokeniser.Tokenizer;

public class Summarizer {
	
	private Tokenizer tokenizer;
	private Sentenizer sentinzer;
	private Paragrapher paragrapher;
	
	private int numSentences;
	
	public Summarizer() {
		tokenizer = new Tokenizer();
		sentinzer = new Sentenizer(tokenizer);
		paragrapher = new Paragrapher(tokenizer);
		this.numSentences = 2;	//Default number of sentences
	}
	
	public void setNumSentences(int numSentences) {
		this.numSentences = numSentences;
	}
	
	public String summarize(String title, String content) {
		Vector<TokenInfo> titleTokens = tokenizer.tokenize(title);
		Vector<TokenInfo> tokens = tokenizer.tokenize(content);
		Vector<SectionInfo> sentences = sentinzer.sentenize(content);
		Vector<SectionInfo> paragraphs = paragrapher.paragraph(content);
		Weighter weighter = new Weighter(titleTokens, tokens, sentences, paragraphs);
		Aggregator aggregator = new Aggregator(sentences);
		
		Vector<Double[]> weights = weighter.calculateWeights();
		SentenceScore [] scores = aggregator.aggregate(weights);
		
		String summary = StringUtils.EMPTY;
		for(int i = 0; i < scores.length; i++) {
			if(scores[i].getScore() < numSentences)
				summary+=(scores[i].getSentence() + " ... ");
		}
		
		return summary;
	}
	
}