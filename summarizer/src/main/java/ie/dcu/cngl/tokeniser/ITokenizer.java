package ie.dcu.cngl.tokeniser;

import java.util.Vector;

public interface ITokenizer {
	public Vector<TokenInfo> tokenize(String sentence);
}
