package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.SectionInfo;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.Vector;

public class TitleTermFeature extends Feature {

	private Vector<TokenInfo> titleTokens;

	public TitleTermFeature(Vector<TokenInfo> titleTokens, Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) throws IOException {
		super(tokens, sentences, paragraphs);
		this.titleTokens = titleTokens;
	}

	@Override
	public Double [] getWeights() {
		Double [] weights = new Double[sentences.size()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 0.0;
		}

		for(int i = 0; i < sentences.size(); i++) {
			double numOccurences = 0;
			for(int j = 0; j < titleTokens.size(); j++) {
				numOccurences+=getNumOccurences(titleTokens.get(j).getValue(), sentences.get(i).getValue());
			}
			weights[i] = numOccurences/(double)titleTokens.size();
		}

		normalise(weights);
		return weights;
	}

}
