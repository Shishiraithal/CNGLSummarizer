package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.ArrayList;

public class TitleTermFeature extends Feature {

	private ArrayList<TokenInfo> titleTokens;

	public TitleTermFeature(ArrayList<TokenInfo> titleTokens) throws IOException {
		this.titleTokens = titleTokens;
	}

	@Override
	public Double[] calculateRawWeights(Double[] weights) {
		final double numTitleTerms = numberOfTerms(titleTokens);
		for(int i = 0; i < structure.getNumSentences(); i++) {
			double numOccurences = 0;
			for(int j = 0; j < titleTokens.size(); j++) {
				numOccurences+=getNumOccurences(titleTokens.get(j).getValue(), structure.getSentences().get(i).getValue());
			}
			weights[i] = numOccurences/numTitleTerms;
		}
		return weights;
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.titleTermMultiplier;
	}

}
