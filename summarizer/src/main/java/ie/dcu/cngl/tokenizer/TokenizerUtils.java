package ie.dcu.cngl.tokenizer;

import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

public class TokenizerUtils {
	public static final String COMMENT = "#";
	public static final String WHITE_SPACE = " ";
	public static String abbreviations;
	public static String badSentenceStart;
	public static String possibleSentenceEnd;
	public static String badSentenceEnd;
	
	static {
		try {
			XMLConfiguration config = new XMLConfiguration("src/main/resources/tokeniser.xml");
			abbreviations = config.getString("word.abbreviations");
			badSentenceStart = config.getString("sentence.badStart");
			possibleSentenceEnd = config.getString("sentence.possibleEnd");
			badSentenceEnd = config.getString("sentence.badEnd");
		} catch (ConfigurationException e) {}
	}
	
	public static Vector<String> recombineTokens(Vector<Vector<TokenInfo>> sections) {
		Vector<String> strSections = new Vector<String>();
		String combined;
		TokenInfo current, next;
		
		for(Vector<TokenInfo> tokens : sections) {
			current = tokens.get(0);
			combined = current.getValue();	//Presumes the first character has no spaces at the beginning
			for(int i = 1; i < tokens.size(); i++) {
				next = tokens.get(i);
				for(int j = 0; j < next.getStart()-(current.getStart()+current.getLength()); j++) {	//Add white spaces
					combined+=WHITE_SPACE;
				}
				combined+=next.getValue();
				current = next;
			}
			strSections.add(combined);
		}
		
		return strSections;
	}
}
