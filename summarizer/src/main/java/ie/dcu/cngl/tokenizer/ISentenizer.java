package ie.dcu.cngl.tokenizer;

import java.util.Vector;

public interface ISentenizer {
    public Vector<Vector<TokenInfo>> sentenizeTokens(String sentence);
}
