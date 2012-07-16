package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.TokenInfo;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class PunctuationFeature extends Feature {
	
	private double maxPunctuationRatio;

	public PunctuationFeature() throws IOException {
		//Default
		this.maxPunctuationRatio = 0.3;
	}
	
	@Override
	public Double[] getWeights() {
		final int numSentences = structure.getNumSentences();
		Double[] weights = new Double[numSentences];
		
		ArrayList<TokenInfo> tokens;
		for(int i = 0; i < numSentences; i++) {
			tokens = structure.getSentenceTokens(i);
			double punctuationRatio = numPunctuationTokens(tokens)/tokens.size();
			weights[i] = punctuationRatio > maxPunctuationRatio ? -1.0 : 0.0;
		}
		
		return weights;
	}

	private double numPunctuationTokens(ArrayList<TokenInfo> tokens) {
		double numPunctuationTokens = 0;
		
		for(TokenInfo token : tokens) {
			if(!StringUtils.isAlphanumeric(token.getValue())) {
				numPunctuationTokens++;
			}
		}
		
		return numPunctuationTokens;
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.punctuationMultiplier != 0 ? 1 : 0;
	}

	@Override
	public Double[] calculateRawWeights(Double[] weights) {
		return weights;
	}

	public void setMaxPunctuationRatio(double maxPunctuationRatio) {
		this.maxPunctuationRatio = maxPunctuationRatio;
	}

}
