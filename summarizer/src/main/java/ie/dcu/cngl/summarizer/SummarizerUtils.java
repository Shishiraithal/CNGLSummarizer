package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.TokenInfo;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class SummarizerUtils {
	
	public static String stopwords;
	public static String cuePhrasesFile;
	
	static {
		try {
			XMLConfiguration config = new XMLConfiguration("src/main/resources/summarizer.xml");
			stopwords = config.getString("stopwords");
			cuePhrasesFile = config.getString("cuephrases");
		} catch (ConfigurationException e) {}
	}
	
	public static int numberOfSentences(ArrayList<ArrayList<ArrayList<TokenInfo>>> structure) {
		int numSentences = 0;
		for(ArrayList<ArrayList<TokenInfo>> paragraph : structure) {
			numSentences+=paragraph.size();
		}
		return numSentences;
	}

}
