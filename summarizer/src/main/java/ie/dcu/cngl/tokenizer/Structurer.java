package ie.dcu.cngl.tokenizer;

import java.util.ArrayList;

public class Structurer implements IStructurer {

	private Sentenizer sentenizer;
	private Paragrapher paragrapher;

	public Structurer() {
		this.sentenizer = Sentenizer.getInstance();
		this.paragrapher = Paragrapher.getInstance();
	}
	
	/**
	 * Gets content structure. A vector of paragraphs, containing a vector
	 * of sentences, containing a vector of tokens.
	 * @param content
	 * @return
	 */
	public PageStructure getStructure(String content) {
		ArrayList<ArrayList<ArrayList<TokenInfo>>> structure = new ArrayList<ArrayList<ArrayList<TokenInfo>>>();
		ArrayList<String> paragraphs = TokenizerUtils.recombineTokens2d(paragrapher.paragraphTokens(content));
		for(String paragraph : paragraphs) {
			ArrayList<ArrayList<TokenInfo>> sentences = sentenizer.sentenizeTokens(paragraph);
			structure.add(sentences);
		}
		return new PageStructure(structure);
	}
}
