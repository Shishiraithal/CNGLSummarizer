package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.Structurer;
import junit.framework.TestCase;

public class SummarizerTest extends TestCase {

	public void testSummarizer() {
		Structurer structurer = new Structurer();
		Weighter weighter = new Weighter();
		Aggregator aggregator = new Aggregator();		
		Summarizer summarizer = new Summarizer(structurer, weighter, aggregator);
		summarizer.setNumSentences(1);
		String summary = summarizer.summarize("This is a test");
		assertEquals(summary, "This is a test\n");
	}
}
