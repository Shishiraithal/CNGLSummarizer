package ie.dcu.cngl.summarizer;

import org.apache.commons.lang.StringUtils;

public class Affix {
	
	public enum AffixType {
		PREFIX, SUFFIX, INFIX;
	}
	
	private AffixType type;
	private String affix;
	
	public Affix(String affix) throws Exception {
		boolean hyphenAtStart = affix.startsWith("-");
		boolean hyphenAtEnd = affix.endsWith("-");
		if(hyphenAtStart && hyphenAtEnd) {
			this.type = AffixType.INFIX;
		} else if(hyphenAtStart) {
			this.type = AffixType.SUFFIX;
		} else if(hyphenAtEnd) {
			this.type = AffixType.PREFIX;
		} else {
			throw new Exception("Incorrect affix format");
		}
		this.affix = affix.replace("-", StringUtils.EMPTY);
	}

	public AffixType getType() {
		return this.type;
	}
	
	public String getAffix() {
		return this.affix;
	}
	
}
