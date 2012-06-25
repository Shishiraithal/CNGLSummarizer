package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokeniser.SectionInfo;
import ie.dcu.cngl.tokeniser.TokenInfo;
import java.io.IOException;
import java.util.Vector;

public class TFISFFeature extends LuceneFeature {

	public TFISFFeature(Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) throws IOException {
		super(tokens, sentences, paragraphs);
	}

	@Override
	protected float computeDeboost(int paragraphNumber, int sentenceNumber) {
		return 1;
	}

}
