package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.ArrayList;

public class QueryBiasFeature extends Feature {
	
	private ArrayList<TokenInfo> query;

	public QueryBiasFeature(ArrayList<TokenInfo> query, PageStructure structure) throws IOException {
		super(structure);
		this.query = query;
	}
	
	@Override
	public Double[] calculateRawWeights(Double[] weights) {
		final double numQueryTerms = numberOfTerms(query);
		for(int i = 0; i < structure.getNumSentences(); i++) {
			double numOccurences = 0;
			for(int j = 0; j < query.size(); j++) {
				numOccurences+=getNumOccurences(query.get(j).getValue(), structure.getSentences().get(i).getValue());
			}
			weights[i] = Math.pow(numOccurences, 2)/numQueryTerms;
		}
		return weights;
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.queryBiasMultiplier;
	}

}
