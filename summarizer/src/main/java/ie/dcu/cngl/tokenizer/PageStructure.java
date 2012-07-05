package ie.dcu.cngl.tokenizer;

import java.util.ArrayList;
import java.util.HashMap;

public class PageStructure {
	
	private ArrayList<ArrayList<ArrayList<TokenInfo>>> structure;
	private ArrayList<SectionInfo> sentences;
	private ArrayList<SectionInfo> paragraphs;
	private HashMap<Integer, Integer> sentenceToParagraph;
	private HashMap<Integer, Integer> sentenceToRelativePosition;

	public PageStructure(ArrayList<ArrayList<ArrayList<TokenInfo>>> structure) {
		this.structure = structure;
		this.sentences = getSentencesPriv();
		this.paragraphs = getParagraphsPriv();
		this.sentenceToParagraph = new HashMap<Integer, Integer>();
		this.sentenceToRelativePosition = new HashMap<Integer, Integer>();
		mapSentencesToParagraphs();
	}

	/**
	 * Maps each sentence to its paragraph to aid later retrieval
	 */
	private void mapSentencesToParagraphs() {
		int sentenceNumber = 0, paragraphNumber = 0, sentenceParagraphStarter = 0;
		for(ArrayList<ArrayList<TokenInfo>> paragraph : structure) {
			int numSentences = paragraph.size();
			for(int i = 0; i < numSentences; i++) {
				sentenceToParagraph.put(sentenceNumber, paragraphNumber);
				sentenceToRelativePosition.put(sentenceNumber, sentenceParagraphStarter);
				sentenceNumber++;
			}
			sentenceParagraphStarter = sentenceNumber;
			paragraphNumber++;
		}
	}

	private ArrayList<SectionInfo> getSentencesPriv() {
		return getSectionInfo(structure);
	}
	
	private ArrayList<SectionInfo> getParagraphsPriv() {
		//Prior to calling getSectionInfo we need all tokens of each paragraph in one array
		ArrayList<TokenInfo> individualParagraphTokens;
		ArrayList<ArrayList<TokenInfo>> allParagraphTokens = new ArrayList<ArrayList<TokenInfo>>();
		for(ArrayList<ArrayList<TokenInfo>> paragraph : structure) {
			individualParagraphTokens = new ArrayList<TokenInfo>();
			for(ArrayList<TokenInfo> sentence : paragraph) {
				for(TokenInfo token : sentence) {
					individualParagraphTokens.add(token);
				}
			}
			allParagraphTokens.add(individualParagraphTokens);
		}
		ArrayList<ArrayList<ArrayList<TokenInfo>>> paragraphsHolder = new ArrayList<ArrayList<ArrayList<TokenInfo>>>();
		paragraphsHolder.add(allParagraphTokens);
		
		return getSectionInfo(paragraphsHolder);
	}
	
	public ArrayList<TokenInfo> getSentenceFromParagraphTokens(int sentenceNumber, int paragraphNumber) {
		try {
			return structure.get(paragraphNumber).get(sentenceNumber);
		} catch(Exception e) {
			return null;
		}
	}
	
	public ArrayList<TokenInfo> getSentenceTokens(int sentenceNumber) {
		return getSentenceFromParagraphTokens(sentenceNumber-sentenceToRelativePosition.get(sentenceNumber), sentenceToParagraph.get(sentenceNumber));
	}
	
	public ArrayList<ArrayList<ArrayList<TokenInfo>>> getStructure() {
		return this.structure;
	}
	
	public ArrayList<SectionInfo> getSentences() {
		return this.sentences;
	}
	
	public ArrayList<SectionInfo> getParagraphs(){
		return this.paragraphs;
	}
	
	public int getNumSentences() {
		return this.sentences.size();
	}
	
	public int getNumParagraphs() {
		return this.paragraphs.size();
	}
	
	private ArrayList<SectionInfo> getSectionInfo(ArrayList<ArrayList<ArrayList<TokenInfo>>> rawSections) {
		ArrayList<SectionInfo> sections = new ArrayList<SectionInfo>();
		int sectionCount = 0;
		for(ArrayList<ArrayList<TokenInfo>> paragraph : rawSections) {
			ArrayList<String> strSentences = TokenizerUtils.recombineTokens2d(paragraph);
			for(String sentence : strSentences) {
				SectionInfo sentenceInfo = new SectionInfo(sentence, sectionCount);
				sections.add(sentenceInfo);
				sectionCount++;
			}
		}
		return sections;
	}

}
