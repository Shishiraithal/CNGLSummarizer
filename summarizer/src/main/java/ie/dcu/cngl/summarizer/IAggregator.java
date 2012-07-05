package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.SectionInfo;

import java.util.ArrayList;

public interface IAggregator {
	public SentenceScore[] aggregate(ArrayList<Double[]> allWeights);

	public void setSentences(ArrayList<SectionInfo> sentences);
}
