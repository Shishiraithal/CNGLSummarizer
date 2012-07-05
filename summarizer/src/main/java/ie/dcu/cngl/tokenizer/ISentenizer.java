package ie.dcu.cngl.tokenizer;

import java.util.ArrayList;

public interface ISentenizer {
    public ArrayList<ArrayList<TokenInfo>> sentenizeTokens(String sentence);
}
