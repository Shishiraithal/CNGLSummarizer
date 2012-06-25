package ie.dcu.cngl.tokeniser;

import java.util.*;
import java.io.*;

import org.apache.commons.lang.StringUtils;

// 's s'
// contractions 'll etc.
// + abbrevs 
// ...

// dehyphenate
// SGML escpae/unescpae

public class Tokenizer implements ITokenizer {
	
	private static Tokenizer instance;

    private char[] mChars;
    private int mLastPosition;
    private int mStartPosition;

    private int mPosition;
    private int mTokenStart;
    private int mLastTokenIndex;
    private int mLine;
    
    private int mLastTokenStartPosition = -1;
    private int mLastTokenEndPosition = -1;

    private static HashMap<String, Vector<Vector<String>>> abbrevs;

    private Tokenizer() {
		abbrevs = new HashMap<String, Vector<Vector<String>>>();
		loadAbbreviations(TokenizerUtils.abbreviations);
    }
    
    public static Tokenizer getInstance() {
    	if(instance == null) {
    		instance = new Tokenizer();
    	}
    	return instance;
    }

    public void loadAbbreviations(String filename) {
		try {
            File file = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line = StringUtils.EMPTY;
            int cnt = 0;
            while((line = reader.readLine()) != null) {
                if (line == null || line.equals(StringUtils.EMPTY) || line.startsWith(TokenizerUtils.COMMENT)) {
                    ; // ignore comment
                } else {
				    line = line.toLowerCase();
				    if (line != null && !line.equals(StringUtils.EMPTY)) {
						// get rid of quotes
						line = line.substring(1, line.length()-1);
				    }
				    Vector<String> repl = new Vector<String>();
				    Vector<TokenInfo> term_elts = tokenize(line, false);
				    TokenInfo t0 = (TokenInfo)term_elts.elementAt(0);
				    String term_one = t0.getValue();
				    repl.add(line);
				    for (int i = 1; i < term_elts.size(); i++) {
						TokenInfo ti = (TokenInfo)term_elts.elementAt(i);
						String oelt = ti.getValue();
						repl.add(oelt);
				    }
				    Vector<Vector<String>> entry = (Vector<Vector<String>>)abbrevs.get(term_one);
				    if (entry == null) {
				    	entry = new Vector<Vector<String>>();
				    }
				    entry.add(repl);
				    abbrevs.put(term_one, entry);
				    cnt++;
                }
            }
            
            // should sort by length of key here
            reader.close();
        } catch (IOException o) {
            System.out.println("ERROR: exception " + o);
            return;
        }
    }

    public int lastTokenStartPosition() {
        return mLastTokenStartPosition;
    }

    public int lastTokenEndPosition() {
        return mLastTokenEndPosition;
    }

    public String getValue()  {
        skipWhitespace();
        if (EOS()) {
        	return null;
        }
        mTokenStart = mPosition;
        mLastTokenIndex++;
        char startChar = mChars[mPosition++];
        // update to deal with initial period digits properly
        if (startChar == '.') return specialToken('.');
        if (startChar == '-') return specialToken('-');
        if (startChar == '_') return specialToken('_');
        if (startChar == '=') return specialToken('=');
        if (startChar == '*') return specialToken('*');
        if (startChar == '\'') return specialToken('\'');
        if (startChar == '`') return specialToken('`');
        if (Character.isLetter(startChar)) return alphaNumericToken();
        if (Character.isDigit(startChar)) return numericToken();
        return curToken(); // other single character symbol
    }

    private boolean EOS() {
        return mPosition >= mLastPosition;
    }

    private char curChar() {
        return mChars[mPosition];
    }

    private boolean curCharEquals(char c) {
	if (EOS())
	    return false;
        return curChar() == c;
    }

    private void skipWhitespace()  {
        while (!EOS()) {
        	if(curChar() == '\r' || curChar() == '\n') {
        		mLine++;
        		mPosition++;
        	} else if(Character.isWhitespace(curChar())) {
        		mPosition++;
        	} else {
        		break;
        	}
        }
    }

    private String curToken() {
        mLastTokenStartPosition = mTokenStart - mStartPosition;
        mLastTokenEndPosition = mPosition - mStartPosition;
        return new String(mChars, mTokenStart, mPosition-mTokenStart);
    }

    private String specialToken(char ch) {
		while (curCharEquals(ch)) 
		    mPosition++;
		return curToken();
    }

    private String alphaNumericToken() {
        while (!EOS() && (Character.isLetter(curChar()) || Character.isDigit(curChar()) 
        		|| curChar() =='_' || curChar() == '\'' )) {
        	mPosition++;
        }
        return curToken();
    }

    private String numericToken() {
        while (!EOS()) {
            if (Character.isLetter(curChar())) {
                mPosition++;
                return alphaNumericToken();
            }
            if (Character.isDigit(curChar())) {
                mPosition++;
                continue;
            }
            if (curChar() == '.' || curChar() == ',') {
                return numericPunctuationToken();
            }
            return curToken();
        }
        return curToken();
    }

    private String numericPunctuationToken() {
        while (!EOS()) {
            if (Character.isDigit(curChar())) {
                mPosition++;
            } else if (curChar() == '.' || curChar() == ',') {
                mPosition++;
                if (EOS() || !Character.isDigit(curChar())) {
                    mPosition--;
                    return curToken();
                }
            } else {
                return curToken();
            }
        }
        return curToken();
    }

    public Vector<TokenInfo> tokenize(String s, boolean postprocess) {
        mChars = s.toCharArray();
        mPosition = 0;
        mLastPosition = s.length();
        mTokenStart = -1;
        mLastTokenIndex = -1;
        mStartPosition = 0;
        Vector<TokenInfo> tokens = new Vector<TokenInfo>();
        String token;
        while ((token = getValue()) != null) {
		    TokenInfo ti = new TokenInfo(token);
		    ti.setStart(mTokenStart);
		    ti.setLength(token.length());
		    ti.setLineNum(mLine);
	        tokens.add(ti);
        }
		if (postprocess) {
		    tokens=postTokenize(tokens);
		    tokens=deHyphenate(tokens, s);
		}
        return tokens;
    }

    public Vector<TokenInfo> tokenize(String s) {
    	return tokenize(s, true);
    }

    public Vector<String> tokenizeString(String s) {
		Vector<TokenInfo> tis = tokenize(s);
		Vector<String> ts = new Vector<String>();
		for (int i = 0; i < tis.size(); i++) {
		    TokenInfo ti = (TokenInfo)tis.elementAt(i);
		    ts.add(ti.getValue());
		}
		return ts;
    }

    public static Vector<TokenInfo> postTokenize(Vector<TokenInfo> tok_vec) {
		int tok_cnt = tok_vec.size();
	    int tok_pos = 0;
		int nskip = 1;
		while (tok_pos < tok_cnt) {
		    TokenInfo ti = tok_vec.elementAt(tok_pos);
            String token = ti.getValue();
            String ltoken = token.toLowerCase();
		    Vector<Vector<String>> repl_entries = null;
            Vector<String> repl_entry;
            int max_repl_len = 0;
		    
            // acronym heuristics
		    int mcount = 0;
		    nskip = ACROmatch(tok_vec, tok_pos);
		    if (nskip == 1) {
				if (abbrevs != null)
				    repl_entries = abbrevs.get(ltoken);
				if (repl_entries != null) {
				    for (int j = 0; j < repl_entries.size(); j++) { // find longest (#terms)
					repl_entry = repl_entries.elementAt(j);
					int i = 0;
					while (repl_entry != null && i < repl_entry.size()) {
					    mcount = MWEmatch(tok_vec, tok_pos+1, repl_entry, 1, 1);
					    if (mcount > max_repl_len) {
						String repl = repl_entry.elementAt(0);
						nskip = mcount;
						max_repl_len = mcount;
						token = repl;
					    }
					    i++;
					}
				    }
				}
		    }
		    if (nskip > 0) {
				// get last token 
				TokenInfo lti = tok_vec.elementAt(tok_pos + nskip-1);
				int lti_end = lti.getStart() + lti.getLength();
				String ls = StringUtils.EMPTY;
				for (int k = 1; k < nskip && tok_pos+1 < tok_cnt; k++) {
				    // remove next token
				    TokenInfo kti = tok_vec.elementAt(tok_pos+1);
				    // assume no whitespace between constituent tokens
				    ls+=kti.getValue();
				    tok_vec.removeElementAt(tok_pos+1);
				    tok_cnt--;
				}
				// modify current token
				ti.setValue(ti.getValue() + ls);
				ti.setLength(lti_end - ti.getStart());
				tok_pos++; // advance only one token
		    } else {
		    	tok_pos++;
		    }
		}
	
		return tok_vec;
    }

    // match token list and phrase pattern
    public static int MWEmatch(Vector<TokenInfo> tok_vec, int tok_pos, Vector<String> pat, int pat_pos, int mcount) {
        if (pat_pos >= pat.size()) { // reached end of pattern
            return mcount;
        } else if (tok_pos >= tok_vec.size()) { // reached end of token list
            return -1;
        } else {
            TokenInfo ti = tok_vec.elementAt(tok_pos);
            String tokelt = ti.getValue();
            String patelt = pat.elementAt(pat_pos);
            tokelt = tokelt.toLowerCase(); // !
            //
            if (tokelt == null  || tokelt.equals(StringUtils.EMPTY)) {
                return MWEmatch(tok_vec, tok_pos+1, pat, pat_pos, mcount+1);
            } else if (!(tokelt.equals(patelt))) { // mismatch
                return -1;
            } else { // match + continue match
                return MWEmatch(tok_vec, tok_pos+1, pat, pat_pos+1, mcount+1);
            }
        }
    }

    public static int ACROmatch(Vector<TokenInfo> tok_vec, int tok_pos) {
		int tok_len = tok_vec.size();
		int mcount = 1;
		if (tok_pos+1 >= tok_len)
		    return 0;
		
		TokenInfo cur = tok_vec.elementAt(tok_pos);
		String curS = cur.getValue();
		TokenInfo nxt = tok_vec.elementAt(tok_pos+1);
		String nxtS = nxt.getValue();
		if (nxtS.equals(".") && (Character.isLetter(curS.charAt(0)) && curS.length()==1)) { // Initial
		    // return or scan ahead
		    mcount = 2; 
		    int a_pos =tok_pos+2;
		    while (a_pos+1 < tok_len) {
				cur = (TokenInfo)tok_vec.elementAt(a_pos);
				curS = cur.getValue();
				nxt = (TokenInfo)tok_vec.elementAt(a_pos+1);
				nxtS = nxt.getValue();
				if (nxtS.equals(".") && (Character.isLetter(curS.charAt(0)) ||
						Character.isDigit(curS.charAt(0))) && curS.length() == 1) { // Initial
				    // return or scan ahead
				    mcount+=2; 
				    a_pos+=2;
				} else {
				    break;
				}
		    }
		    return mcount;
		}
		if (nxtS.equals(".") && Character.isLetter(curS.charAt(0)) && Character.isUpperCase(curS.charAt(0))) {
		    // scan first string: consonants only
		    boolean hasVowel = false;
		    for (int j = 1; !hasVowel && j < curS.length(); j++) {
				char ch = curS.charAt(j);
				if (isVowel(ch)) { // add diacritics
				    hasVowel = true;
				}
		    }
		    if (!hasVowel) {
				mcount = 2;
				return mcount;
		    }
		}
		return mcount;
    }

    public static final boolean isVowel(char ch) {
		if (ch=='a' ||
		    ch=='e' ||
		    ch=='i' ||
		    ch=='o' ||
		    ch=='u') { // add diacritics; special case: y
		    return true;
		}
		return false;
    }

    public static Vector<TokenInfo> deHyphenate(Vector<TokenInfo> tok_vec, String s) {
		int tok_cnt = tok_vec.size();
	    int tok_pos = 0;
		TokenInfo ti1 = null;
		TokenInfo ti2 = null;
		TokenInfo ti3 = null;
		String tok1 = StringUtils.EMPTY, tok2 = StringUtils.EMPTY, tok3 = StringUtils.EMPTY;
	    while (tok_pos < tok_cnt) {
		    ti1 = ti2;
		    ti2 = ti3;
		    ti3 = tok_vec.elementAt(tok_pos);
            tok1 = tok2;
            tok2 = tok3;
            tok3 = ti3.getValue();
	
		    if(ti1 != null && !tok1.equals(StringUtils.EMPTY) && ti2!=null && !tok2.equals(StringUtils.EMPTY) 
		    		&& Character.isLetter(tok1.charAt(0)) && tok2.equals("-") && Character.isLetter(tok3.charAt(0))) {
				int sr = ti1.getStart() + ti1.getLength();
				String ws = s.substring(ti2.getStart() + ti2.getLength(), ti3.getStart());
				if (sr == ti2.getStart() &&  ws.indexOf('\n') >= 0) {	// no space between 1st and 2nd token
				    // modify token
				    ti1.setValue(ti1.getValue() + ti3.getValue());
				    ti1.setLength(ti3.getStart() + ti3.getLength() - ti1.getStart());
				    // remove matching tokens
				    tok_vec.removeElementAt(tok_pos - 1);
				    tok_vec.removeElementAt(tok_pos - 1);
				    tok_cnt -= 2;
				    // re-initialize
				    ti1 = (TokenInfo)tok_vec.elementAt(tok_pos - 3);
				    ti2 = (TokenInfo)tok_vec.elementAt(tok_pos - 2);
				    ti3 = (TokenInfo)tok_vec.elementAt(tok_pos - 1);
				    tok1 = ti1.getValue();
				    tok2 = ti2.getValue();
				    tok3 = ti3.getValue();
				} else {
				    tok_pos++;
				}
		    } else {
		    	tok_pos++;
		    }
		}
		return tok_vec;
    }

}
