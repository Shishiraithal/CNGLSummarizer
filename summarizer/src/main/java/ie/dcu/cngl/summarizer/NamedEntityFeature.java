package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.SectionInfo;
import ie.dcu.cngl.tokenizer.Sentenizer;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.Vector;

public class NamedEntityFeature extends LuceneFeature {

	public NamedEntityFeature(Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) throws IOException {
		super(tokens, sentences, paragraphs);
	}

	@Override
	protected float computeDeboost(int paragraphNumber, int sentenceNumber) {
		Sentenizer sentenizer = Sentenizer.getInstance();
		String paragraph = paragraphs.get(paragraphNumber).getValue();
		Vector<TokenInfo> sentence = sentenizer.sentenizeTokens(paragraph).get(sentenceNumber);
		int numNamedEntities = calculateNumNamedEntities(sentence);
		float boost = (float) (Math.pow(numNamedEntities, 2)/numberOfTerms(sentence));
		return boost;
	}

	private int calculateNumNamedEntities(Vector<TokenInfo> sentence) {
		int numNamedEntities = 0;
		for(int i = 1; i < sentence.size(); i++) {	//Ignore first token
			String token = sentence.get(i).getValue();
			if(Character.isUpperCase(token.charAt(0))) {
				numNamedEntities++;
			}
		}
		return numNamedEntities;
	}

}