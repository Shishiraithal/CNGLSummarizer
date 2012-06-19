package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokeniser.SectionInfo;
import ie.dcu.cngl.tokeniser.TokenInfo;

import java.util.Vector;

public class Weighter {
	
	private Vector<TokenInfo> titleTokens;
	private Vector<TokenInfo> tokens;
	private Vector<SectionInfo> sentences;
	private Vector<SectionInfo> paragraphs;

	public Weighter(Vector<TokenInfo> titleTokens, Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) {
		this.titleTokens = titleTokens;
		this.tokens = tokens;
		this.sentences = sentences;
		this.paragraphs = paragraphs;
	}
	
	public Vector<Double[]> calculateWeights() {
		Vector<Double[]> weights = new Vector<Double[]>();
		Vector<Feature> features = new Vector<Feature>();
		
		SkimmingFeature skimFeat = new SkimmingFeature(tokens, sentences, paragraphs);
		skimFeat.setTopTermCutoff(0.3f);
		
		//Adding all features
		features.add(skimFeat);
		features.add(new TitleTermFeature(titleTokens, tokens, sentences, paragraphs));
		features.add(new CuePhraseFeature(tokens, sentences, paragraphs));
		features.add(new ShortSentenceFeature(tokens, sentences, paragraphs));
		
		
		//Executing features
		for(Feature feature : features) {
			weights.add(feature.getWeights());
		}
		
		return weights;
	}

}
