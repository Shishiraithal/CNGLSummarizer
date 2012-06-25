package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokeniser.SectionInfo;
import ie.dcu.cngl.tokeniser.TokenInfo;
import ie.dcu.cngl.tokeniser.Tokenizer;

import java.io.IOException;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

/**
 * Filters sentences with too few terms
 * @author Shane
 *
 */
public class ShortSentenceFeature extends Feature {
	
	private Tokenizer tokenizer;
	private int minimumSentenceTerms;

	public ShortSentenceFeature(Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) throws IOException {
		super(tokens, sentences, paragraphs);
		this.tokenizer = Tokenizer.getInstance();
		this.minimumSentenceTerms = 5;
	}
	
	public void setMinimumSentenceTerms(int minimum) {
		this.minimumSentenceTerms = minimum;
	}
	
	public int getMinimumSentenceTerms() {
		return this.minimumSentenceTerms;
	}

	@Override
	public Double[] getWeights() {
		final int numSentences = sentences.size();
		Double[] weights = new Double[numSentences];
		
		Vector<TokenInfo> tokens;
		for(int i = 0; i < numSentences; i++) {
			tokens = tokenizer.tokenize(sentences.get(i).getValue());
			tokens = filterPunctuation(tokens);
			weights[i] = tokens.size() < this.minimumSentenceTerms ? -1.0 : 0.0;
		}
		
		return weights;
	}

	private Vector<TokenInfo> filterPunctuation(Vector<TokenInfo> tokens) {
		Vector<TokenInfo> alphaNumericTokens = new Vector<TokenInfo>();
		
		for(TokenInfo token : tokens) {
			if(StringUtils.isAlphanumeric(token.getValue())) {
				alphaNumericTokens.add(token);
			}
		}
		
		return alphaNumericTokens;
	}

}
