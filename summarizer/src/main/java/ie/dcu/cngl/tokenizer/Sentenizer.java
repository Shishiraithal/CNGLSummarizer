package ie.dcu.cngl.tokenizer;

import java.io.*;
import java.util.*;

import org.apache.commons.lang.StringUtils;

public class Sentenizer implements ISentenizer {
	
	private static Sentenizer instance;
	
    private HashSet<String> bss, pse, bse;
    private Tokenizer tokenizer = null;
    
    private Sentenizer(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
		String line;
		bss = new HashSet<String>();
	    try {
	        BufferedReader reader = new BufferedReader(new FileReader(TokenizerUtils.badSentenceStart));
	        while ((line = reader.readLine()) != null) {
				if (line.equals(StringUtils.EMPTY) || line.startsWith(TokenizerUtils.COMMENT)) {
				    ;
				} else if (line.length() < 3) {
				    System.out.println("ERR: invalid line " + line);
				} else {
				    // remove quotes
				    line = line.substring(1, line.length()-1);
				    bss.add(line.toLowerCase());
				}
	        }
	        reader.close();
	    } catch (IOException e) {
	        System.out.println("ERROR: " + e);
	    }
		
		pse = new HashSet<String>();
	    try {
	        BufferedReader reader = new BufferedReader(new FileReader(TokenizerUtils.possibleSentenceEnd));
	        while (null != (line = reader.readLine())) {
				if (line.equals(StringUtils.EMPTY) || line.startsWith(TokenizerUtils.COMMENT)) {
					;
				} else if (line.length() < 3) {
				    System.out.println("ERR: invalid line " + line);
				} else {
				    // remove quotes
				    line = line.substring(1, line.length()-1);
				    pse.add(line.toLowerCase());
				}
	        }
	        reader.close();
	    } catch (IOException e) {
	        System.out.println("ERROR: " + e);
	    }
		
		bse = new HashSet<String>();
	    try {
	        BufferedReader reader = new BufferedReader(new FileReader(TokenizerUtils.badSentenceEnd));
	        while (null != (line = reader.readLine())) {
				if (line.equals(StringUtils.EMPTY) || line.startsWith(TokenizerUtils.COMMENT)) {
					;
				} else if (line.length() < 3) {
				    System.out.println("ERR: invalid line " + line);
				    ;
				} else {
				    // remove quotes
				    line = line.substring(1, line.length()-1);
				    bse.add(line.toLowerCase());
				}
	        }
	        reader.close();
	    } catch (IOException e) {
	        System.out.println("ERROR: " + e);
	    }
    }
    
    public static Sentenizer getInstance() {
    	if(instance == null) {
    		synchronized(Sentenizer.class) {
	    		Tokenizer tokenizer = Tokenizer.getInstance();
	    		instance = new Sentenizer(tokenizer);
    		}
    	}
    	return instance;
    }

    public boolean isBadSentenceStart(String s) {
    	return bss.contains(s);
    }
    
    public boolean isPossibleSentenceEnd(String s) {
    	return pse.contains(s);
    }
    
    public boolean isBadSentenceEnd(String s) {
    	return bse.contains(s);
    }

    public synchronized Vector<Vector<TokenInfo>> sentenizeTokens(String s) {
		Vector<TokenInfo> tok_vec = tokenizer.tokenize(s);
		if (tok_vec == null)
		    return null;
		
		int tok_len = tok_vec.size();
		int tok_pos = 0;
		Vector<TokenInfo> sentence = new Vector<TokenInfo>();
		Vector<Vector<TokenInfo>> sentences = new Vector<Vector<TokenInfo>>();
		TokenInfo prevTokInfo = null;
		TokenInfo currentTokInfo = null;
		TokenInfo nextTokInfo = null;
		String previousToken = StringUtils.EMPTY, currentToken = StringUtils.EMPTY, nextToken = StringUtils.EMPTY;
	    boolean inQuotes = false, isSentence = false;
		while (tok_pos < tok_len) {
	    	//Update all token info
		    prevTokInfo = currentTokInfo;
		    currentTokInfo = nextTokInfo;
		    nextTokInfo = tok_vec.elementAt(tok_pos);
		    
		    //Update token str values
	        previousToken = currentToken;
	        currentToken = nextToken;
	        nextToken = nextTokInfo.getValue();

		    if (currentTokInfo != null)
				sentence.add(currentTokInfo);

		    if(currentTokInfo != null && currentTokInfo.getLineNum() < nextTokInfo.getLineNum() && nextTokInfo.getStart()-(currentTokInfo.getStart()+currentTokInfo.getLength()) > 1) {
		    	isSentence = true;		//New line
			} else if(currentToken.equals("\"")) {	//Opening quotes
				//Starting quotes must follow a space. ([he said "hi there"] is allowed, [5'6"] is not) 
				if(inQuotes || prevTokInfo == null || (currentTokInfo != null && currentTokInfo.getStart() - (prevTokInfo.getStart() + prevTokInfo.getLength()) > 1)) {
					inQuotes = !inQuotes;	//Switches between true and false
				}
		    	isSentence = false; 
	    	} else if(currentTokInfo != null && !isPossibleSentenceEnd(currentToken)) {
		    	isSentence = false; 	// do not break if end token is not recognized
		    } else if(prevTokInfo != null && isBadSentenceEnd(previousToken)) {
		    	isSentence = false; 	// do not break if last token would be bad sentence end
		    } else if(nextTokInfo != null && isBadSentenceStart(nextToken)) {
		    	isSentence = false; 	// do not break if next token is bad sentence start
		    } else if(currentTokInfo != null && nextTokInfo != null && (currentTokInfo.getStart() + currentTokInfo.getLength() > nextTokInfo.getStart()-1)) {
		    	isSentence = false; 	// only break after whitespace
		    } else if(currentTokInfo != null && Character.isLowerCase(nextToken.charAt(0))) {
		    	isSentence = false; 	// don't break before lower cased next token
		    } else {
		    	isSentence = true;
		    }
		    
		    if(sentence.size() > 0 && currentToken != null && isSentence) { 
		    	if(!inQuotes) {
					sentences.add(sentence);
					sentence = new Vector<TokenInfo>();
		    	}
		    }
		    tok_pos++;
		}
		
	    if (sentence.size() > 0 && currentToken != null) { 
		    sentence.add(nextTokInfo);
		    sentences.add(sentence);
		}
	    
	    return sentences;
    }
    
    public synchronized Vector<SectionInfo> sentenize(String text) {
    	Vector<SectionInfo> sentences = new Vector<SectionInfo>();
    	
    	Vector<String> strSentences = TokenizerUtils.recombineTokens(sentenizeTokens(text));
    	for(int i = 0; i < strSentences.size(); i++) {
    		sentences.add(new SectionInfo(strSentences.get(i), i));
    	}
    	
    	return sentences;
    }

}

