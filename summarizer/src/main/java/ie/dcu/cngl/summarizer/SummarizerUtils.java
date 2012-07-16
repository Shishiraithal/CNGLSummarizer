package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.TokenInfo;
import java.util.ArrayList;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class SummarizerUtils {
	
	public static String stopwords;
	public static String cuePhrasesFile;
	public static String affixesFile;
	
	//Feature multipliers
	public static double skimmingMultiplier;
	public static double namedEntityMultiplier;
	public static double TFISFMultiplier;
	public static double titleTermMultiplier;
	public static double cuePhraseMultiplier;
	public static double shortSentenceMultiplier;
	public static double queryBiasMultiplier;
	public static double globalBushyMultiplier;
	public static double punctuationMultiplier;
	public static double affixPresenceMultiplier;
	
	static {
		try {
			XMLConfiguration config = new XMLConfiguration("src/main/resources/summarizer.xml");
			stopwords = config.getString("stopwords");
			cuePhrasesFile = config.getString("cuephrases");
			affixesFile = config.getString("affixes");
			skimmingMultiplier = config.getDouble("multipliers.skimming");
			namedEntityMultiplier = config.getDouble("multipliers.namedEntity");
			TFISFMultiplier = config.getDouble("multipliers.TSISF");
			titleTermMultiplier = config.getDouble("multipliers.titleTerm");
			cuePhraseMultiplier = config.getDouble("multipliers.cuePhrase");
			shortSentenceMultiplier = config.getDouble("multipliers.shortSentence");
			queryBiasMultiplier = config.getDouble("multipliers.queryBias");
			globalBushyMultiplier = config.getDouble("multipliers.globalBushy");
			punctuationMultiplier = config.getDouble("multipliers.punctuation");
			affixPresenceMultiplier = config.getDouble("multipliers.affixPresence");
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
