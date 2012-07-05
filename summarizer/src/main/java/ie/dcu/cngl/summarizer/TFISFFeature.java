package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import java.io.IOException;

public class TFISFFeature extends LuceneFeature {

	public TFISFFeature(PageStructure structure) throws IOException {
		super(structure);
	}

	@Override
	protected float computeDeboost(int paragraphNumber, int sentenceNumber) {
		return 1;	//All will be treated the same
	}

}
