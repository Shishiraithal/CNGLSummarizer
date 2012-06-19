package ie.dcu.cngl.tokeniser;

import java.util.*;

import org.apache.commons.lang.StringUtils;


public class Paragrapher {
    private Tokenizer tokenizer;
    
    public Paragrapher(Tokenizer tokenizer) {
		this.tokenizer = tokenizer;
    }

    public Vector<Vector<TokenInfo>> paragraphTokens(String s) {
		Vector<TokenInfo> tok_vec = tokenizer.tokenize(s);
		if (tok_vec == null)
		    return null;
		
		Vector<TokenInfo> sentence = new Vector<TokenInfo>();
		Vector<Vector<TokenInfo>> sentences = new Vector<Vector<TokenInfo>>();
		
		TokenInfo currentTokInfo = null;
		TokenInfo nextTokInfo = null;
		String currentToken = StringUtils.EMPTY, nextToken = StringUtils.EMPTY;
		
		int tok_pos = 0;
		int tok_len = tok_vec.size();
	    while (tok_pos < tok_len) {
	    	//Update all token info
		    currentTokInfo = nextTokInfo;
		    nextTokInfo = (TokenInfo)tok_vec.elementAt(tok_pos);
		    
		    //Update token str values
	        currentToken = nextToken;
	        nextToken = nextTokInfo.getValue();

		    if (currentTokInfo != null)
				sentence.add(currentTokInfo);

		    if (currentTokInfo != null && nextTokInfo != null && (currentTokInfo.getStart() + currentTokInfo.getLength() > nextTokInfo.getStart()-1)) {
		    	; // only break after whitespace
		    } else if (sentence.size() > 0 && currentToken != null
		    		&& (currentTokInfo.getLineNum() < nextTokInfo.getLineNum() 
		    				&& nextTokInfo.getStart()-(currentTokInfo.getStart()+currentTokInfo.getLength()) > 1)) {
				sentences.add(sentence);
				sentence = new Vector<TokenInfo>();
		    }
		    tok_pos++;
		}
		
	    if (sentence.size() > 0 && currentToken != null) { 
		    sentence.add(nextTokInfo);
		    sentences.add(sentence);
		}
	    
//	    //Creates string vector
//	    Vector<Vector<String>> strSentences = new Vector<Vector<String>>();
//	    Vector<String> currentSentence;
//	    for(Vector<TokenInfo> vectSentence : sentences) {
//	    	currentSentence = new Vector<String>();
//	    	for(TokenInfo token : vectSentence) {
//	    		currentSentence.add(token.getToken());
//	    	}
//	    	strSentences.add(currentSentence);
//	    }
//	    
//		return strSentences;
	    
	    return sentences;
    }
    
    public Vector<SectionInfo> paragraph(String text) {
    	Vector<SectionInfo> paragraphs = new Vector<SectionInfo>();
    	
    	Vector<String> strParagraphs = TokenizerUtils.recombineTokens(paragraphTokens(text));
    	for(int i = 0; i < strParagraphs.size(); i++) {
    		paragraphs.add(new SectionInfo(strParagraphs.get(i), i));
    	}
    	
    	return paragraphs;
    }

}

