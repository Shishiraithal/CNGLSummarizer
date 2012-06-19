package ie.dcu.cngl.tokeniser;

import java.util.Vector;

public interface ISentenizer {
    public Vector<Vector<TokenInfo>> sentenizeTokens(String sentence);
}
