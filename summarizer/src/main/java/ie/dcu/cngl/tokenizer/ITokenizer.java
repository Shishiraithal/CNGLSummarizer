package ie.dcu.cngl.tokenizer;

import java.util.Vector;

public interface ITokenizer {
	public Vector<TokenInfo> tokenize(String sentence);
}
