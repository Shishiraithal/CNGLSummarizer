package ie.dcu.cngl.summarizer;

import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

import ie.dcu.cngl.tokenizer.IStructurer;
import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.Tokenizer;

public class Summarizer {
	
	private Tokenizer tokenizer;
	private IStructurer structurer;	
	private IWeighter weighter;
	private IAggregator aggregator;
	
	private int numSentences;
	private String title;
	private String query;
	
	public Summarizer(IStructurer structurer, IWeighter weighter, IAggregator aggregator) {
		this.tokenizer = Tokenizer.getInstance();
		this.weighter = weighter;
		this.aggregator = aggregator;
		this.structurer = structurer;
		this.numSentences = 2;	//Default number of sentences
	}
	
	public void setNumSentences(int numSentences) {
		this.numSentences = numSentences;
	}
	
	public String summarize(String content) {
		if(StringUtils.isEmpty(content)) {
			return StringUtils.EMPTY;
		}
		
		PageStructure structure = structurer.getStructure(content);	
		weighter.setStructure(structure);
		weighter.setTitle(StringUtils.isNotEmpty(title) ? tokenizer.tokenize(title) : null);
		weighter.setQuery(StringUtils.isNotEmpty(query) ? tokenizer.tokenize(query) : null);
		aggregator.setSentences(structure.getSentences());
		
		ArrayList<Double[]> weights = weighter.calculateWeights();
		SentenceScore [] scores = aggregator.aggregate(weights);
		
		String summary = StringUtils.EMPTY;
		for(int i = 0; i < scores.length; i++) {
			if(scores[i].getScore() < numSentences)
				summary+=(scores[i].getSentence() + "\n");
		}
		
		return summary;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}

	public void setQuery(String query) {
		this.query = query;
	}
	
}