package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.SectionInfo;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.Vector;

public class QueryBiasFeature extends Feature {
	
	private Vector<TokenInfo> query;

	public QueryBiasFeature(Vector<TokenInfo> query, Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) throws IOException {
		super(tokens, sentences, paragraphs);
		this.query = query;
	}

	@Override
	public Double[] getWeights() {
		Double [] weights = new Double[sentences.size()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 0.0;
		}

		final double numQueryTerms = numberOfTerms(query);
		for(int i = 0; i < sentences.size(); i++) {
			double numOccurences = 0;
			for(int j = 0; j < query.size(); j++) {
				numOccurences+=getNumOccurences(query.get(j).getValue(), sentences.get(i).getValue());
			}
			weights[i] = Math.pow(numOccurences, 2)/numQueryTerms;
		}

		normalise(weights);
		return weights;
	}

}
