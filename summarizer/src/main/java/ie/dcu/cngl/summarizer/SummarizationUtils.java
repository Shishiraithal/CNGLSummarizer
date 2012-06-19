package ie.dcu.cngl.summarizer;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class SummarizationUtils {
	public static String stopwords;
	public static String cuePhrasesFile;
	
	static {
		try {
			XMLConfiguration config = new XMLConfiguration("src/main/resources/summarizer.xml");
			stopwords = config.getString("stopwords");
			cuePhrasesFile = config.getString("cuephrases");
		} catch (ConfigurationException e) {}
	}	
}
