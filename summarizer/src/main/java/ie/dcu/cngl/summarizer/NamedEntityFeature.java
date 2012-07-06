package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenInfo;
import java.io.IOException;
import java.util.ArrayList;

public class NamedEntityFeature extends LuceneFeature {

	public NamedEntityFeature(PageStructure structure) throws IOException {
		super(structure);
	}

	@Override
	protected float computeDeboost(int paragraphNumber, int sentenceNumber) {
		ArrayList<TokenInfo> sentence = structure.getSentenceFromParagraphTokens(sentenceNumber, paragraphNumber);
		int numNamedEntities = calculateNumNamedEntities(sentence);
		float boost = (float) (Math.pow(numNamedEntities, 2)/numberOfTerms(sentence));
		return boost;
	}

	private int calculateNumNamedEntities(ArrayList<TokenInfo> sentence) {
		int numNamedEntities = 0;
		for(int i = 1; i < sentence.size(); i++) {	//Ignore first token
			String token = sentence.get(i).getValue();
			if(Character.isUpperCase(token.charAt(0))) {
				numNamedEntities++;
			}
		}
		return numNamedEntities;
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.namedEntityMultiplier;
	}

}