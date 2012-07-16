package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.summarizer.Affix.AffixType;
import ie.dcu.cngl.tokenizer.TokenInfo;
import ie.dcu.cngl.tokenizer.TokenizerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class AffixPresenceFeature extends Feature {

	private ArrayList<Affix> affixes;
	private int extraLettersForMatch;		//Extra letters at either end of affix necessary for match

	public AffixPresenceFeature() throws IOException {
		this.extraLettersForMatch = 3;		//Default
		this.affixes = new ArrayList<Affix>();
		String line = null;
		try {
			File file = new File(SummarizerUtils.affixesFile);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			while((line = reader.readLine()) != null) {
				if (!(line.equals(StringUtils.EMPTY) || line.startsWith(TokenizerUtils.COMMENT))) {
					line = line.toLowerCase();
					Affix affix = new Affix(line);
					affixes.add(affix);
				}
			}
			reader.close();
		} catch (Exception e) {
			System.out.println("ERROR: exception " + e + "\n" + line);
			return;
		}
	}

	@Override
	public double getMultiplier() {
		return SummarizerUtils.affixPresenceMultiplier;
	}
	
	public void setExtraLettersForMatch(int extraLettersForMatch) {
		this.extraLettersForMatch = extraLettersForMatch;
	}

	public int getExtraLettersForMatch() {
		return extraLettersForMatch;
	}

	@Override
	public Double[] calculateRawWeights(Double[] weights) {
		int sentenceNum = 0;
		for(ArrayList<ArrayList<TokenInfo>> paragraph : structure.getStructure()) {
			for(ArrayList<TokenInfo> sentence : paragraph) {
				for(Affix medicalTerm : affixes) {
					if(medicalTerm.getAffix().length() > 2) {
						weights[sentenceNum]+=getNumAffixOccurences(medicalTerm, sentence);
					}
				}
				sentenceNum++;
			}
		}
		return weights;
	}
	
	private Double getNumAffixOccurences(Affix affix, ArrayList<TokenInfo> sentence) {
		final String affixStr = affix.getAffix();
		final int affixLength = affixStr.length();
		double numOccurences = 0;
		
		for(TokenInfo token : sentence) {
			String tokenStr = token.getValue();
			int tokenLength = token.getLength();
			AffixType type = affix.getType();
			int minimumLength = type == AffixType.INFIX ? affixLength+2*extraLettersForMatch : affixLength+extraLettersForMatch;
			if(tokenLength > minimumLength) {
				switch(affix.getType()) {
					case PREFIX:
						if(tokenStr.startsWith(affixStr)) {
							numOccurences++;
						}
						break;
					case SUFFIX:
						if(tokenStr.endsWith(affixStr)) {
							numOccurences++;
						}
						break;
					case INFIX:
						if(tokenStr.indexOf(affixStr) != -1) {
							numOccurences++;
						}
						break;
				}
			}
		}

		return numOccurences;
	}

}
