package ie.dcu.cngl.tokenizer;

import java.util.*;

import org.apache.commons.lang.StringUtils;


public class Paragrapher {
	
	private static Paragrapher instance;
    private Tokenizer tokenizer;
    
    private Paragrapher(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
    }

    public static Paragrapher getInstance() {
    	if(instance == null) {
    		synchronized(Paragrapher.class) {
	    		Tokenizer tokenizer = Tokenizer.getInstance();
	    		instance = new Paragrapher(tokenizer);
    		}
    	}
    	return instance;
    }

    public synchronized ArrayList<ArrayList<TokenInfo>> paragraphTokens(String s) {
		ArrayList<TokenInfo> tok_vec = tokenizer.tokenize(s);
		if (tok_vec == null)
		    return null;
		
		ArrayList<TokenInfo> sentence = new ArrayList<TokenInfo>();
		ArrayList<ArrayList<TokenInfo>> sentences = new ArrayList<ArrayList<TokenInfo>>();
		
		TokenInfo currentTokInfo = null;
		TokenInfo nextTokInfo = null;
		String currentToken = StringUtils.EMPTY, nextToken = StringUtils.EMPTY;
		
		int tok_pos = 0;
		int tok_len = tok_vec.size();
	    while (tok_pos < tok_len) {
	    	//Update all token info
		    currentTokInfo = nextTokInfo;
		    nextTokInfo = (TokenInfo)tok_vec.get(tok_pos);
		    
		    //Update token str values
	        currentToken = nextToken;
	        nextToken = nextTokInfo.getValue();

		    if (currentTokInfo != null)
				sentence.add(currentTokInfo);

		    if (currentTokInfo != null && nextTokInfo != null && (currentTokInfo.getStart() + currentTokInfo.getLength() > nextTokInfo.getStart()-1)) {
		    	; // only break after whitespace
		    } else if (sentence.size() > 0 && currentToken != null
		    		&& (currentTokInfo.getLineNum() < nextTokInfo.getLineNum() 	//New line
		    				&& nextTokInfo.getStart()-(currentTokInfo.getStart()+currentTokInfo.getLength()) > 1)) {
				sentences.add(sentence);
				sentence = new ArrayList<TokenInfo>();
		    }
		    tok_pos++;
		}
		
	    if (sentence.size() > 0 && currentToken != null) { 
		    sentence.add(nextTokInfo);
		    sentences.add(sentence);
		}
	    
	    return sentences;
    }

}

