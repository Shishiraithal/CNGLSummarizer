package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.Structurer;

import java.io.File;

import org.apache.commons.io.FileUtils;


public class Test {
	public static void main(String [] args) throws Exception {
		String text = FileUtils.readFileToString(new File("C:\\Users\\Shane\\Desktop\\long.txt"), "UTF-8");
		Structurer structurer = new Structurer();
		Weighter weighter = new Weighter();
		Aggregator aggregator = new Aggregator();
		Summarizer summarizer = new Summarizer(structurer, weighter, aggregator);
		summarizer.setNumSentences(3);
		String summary = summarizer.summarize(text);
		System.out.println(summary);
	}
}
