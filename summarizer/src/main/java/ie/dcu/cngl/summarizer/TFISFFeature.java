package ie.dcu.cngl.summarizer;

import java.io.IOException;

public class TFISFFeature extends LuceneFeature {

	public TFISFFeature() throws IOException {
		super();
	}

	@Override
	protected float computeDeboost(int paragraphNumber, int sentenceNumber) {
		return 1;	//All will be treated the same
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.TFISFMultiplier;
	}

}
