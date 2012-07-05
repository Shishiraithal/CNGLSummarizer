package ie.dcu.cngl.tokenizer;

import java.util.ArrayList;

public interface ITokenizer {
	public ArrayList<TokenInfo> tokenize(String sentence);
}
