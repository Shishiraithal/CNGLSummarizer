package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.ArrayList;

public class TitleTermFeature extends Feature {

	private ArrayList<TokenInfo> titleTokens;

	public TitleTermFeature(ArrayList<TokenInfo> titleTokens, PageStructure structure) throws IOException {
		super(structure);
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
