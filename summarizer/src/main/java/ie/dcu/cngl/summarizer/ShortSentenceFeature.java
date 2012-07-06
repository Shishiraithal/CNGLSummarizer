package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

/**
 * Filters sentences with too few terms
 * @author Shane
 *
 */
public class ShortSentenceFeature extends Feature {
	
	private int minimumSentenceTerms;

	public ShortSentenceFeature(PageStructure structure) throws IOException {
		super(structure);
		this.minimumSentenceTerms = 5;
	}
	
	public void setMinimumSentenceTerms(int minimum) {
		this.minimumSentenceTerms = minimum;
	}

	@Override
	public Double[] getWeights() {
		final int numSentences = structure.getNumSentences();
		Double[] weights = new Double[numSentences];
		
		ArrayList<TokenInfo> tokens;
		for(int i = 0; i < numSentences; i++) {
			tokens = structure.getSentenceTokens(i);
			tokens = filterPunctuation(tokens);
			weights[i] = tokens.size() < this.minimumSentenceTerms ? -1.0 : 0.0;
		}
		
		return weights;
	}

	private ArrayList<TokenInfo> filterPunctuation(ArrayList<TokenInfo> tokens) {
		ArrayList<TokenInfo> alphaNumericTokens = new ArrayList<TokenInfo>();
		
		for(TokenInfo token : tokens) {
			if(StringUtils.isAlphanumeric(token.getValue())) {
				alphaNumericTokens.add(token);
			}
		}
		
		return alphaNumericTokens;
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.shortSentenceMultiplier;
	}

	@Override
	public Double[] calculateRawWeights(Double[] weights) {
		return weights;
	}

}
