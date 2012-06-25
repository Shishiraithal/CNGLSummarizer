package ie.dcu.cngl.summarizer;

import java.io.File;

import org.apache.commons.io.FileUtils;


public class Test {
	public static void main(String [] args) throws Exception {
		String text = FileUtils.readFileToString(
									new File("C:\\Users\\Shane\\Downloads\\summaries\\JAIRtxt\\agre97a.txt"), "UTF-8");
		Summarizer summarizer = new Summarizer();
		summarizer.setNumSentences(3);
		String summary = summarizer.summarize(null, text);
//		summarizer.setQuery("what is cloud dial");
//		String summary = summarizer.summarize(
//				"Cloud Dial - A Cloud Based Visual Bookmarking Service",
//				"Cloud Dial Cloud Dial. "
//				+ "Cloud Dial is a web application that allows users to store, manage, retrieve and share their bookmarks anywhere, any time, straight from their browser. This system aims to be central to a users' web browsing. It should be the first thing they see when they open their browser, a new tab, or new window. Doing so they can seamlessly access their favourite sites and collected bookmarks, greatly improving productivity. In Cloud Dial each bookmark will be identifiable by an image. This will make it visually appealing and extremely practical to use in this way, especially on mobile devices. "
//				+ "Identifying bookmarks further will be made possible by means of textual tags, content summarisation, and categorisation into groups and sub-groups; all of which will be suggested by Cloud Dial. This rich variety of information will ensure efficient browsing, and effective bookmark retrieval. "
//				+ "It is hoped this system will be used by all web browser users, but more specifically, by those who bookmark regularly. An ideal user bookmarks on several different devices, bookmarking pages of various topics. For people who bookmark a lot, and need to retrieve specific bookmarks on many devices, Cloud Dial is the perfect solution. "
//				+ "In conclusion, Cloud Dial is Delicious with pictures"
//			);
		System.out.println(summary);
	}
}
