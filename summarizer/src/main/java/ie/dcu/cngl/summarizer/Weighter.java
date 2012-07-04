package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.SectionInfo;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.Vector;

public class Weighter {
	
	private Vector<TokenInfo> tokens;
	private Vector<SectionInfo> sentences;
	private Vector<SectionInfo> paragraphs;
	
	private Vector<TokenInfo> title;
	private Vector<TokenInfo> query;

	public Weighter(Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) {
		this.tokens = tokens;
		this.sentences = sentences;
		this.paragraphs = paragraphs;
	}
	
	public Vector<Double[]> calculateWeights() {
		Vector<Double[]> weights = new Vector<Double[]>();
		Vector<Feature> features = new Vector<Feature>();
		
		//Adding all features
		try {
			SkimmingFeature skimFeat = new SkimmingFeature(tokens, sentences, paragraphs);
			skimFeat.setTopTermCutoff(0.3f);
			features.add(skimFeat);
		} catch (IOException e) {
			System.err.println("Skimming feature failed.");
			e.printStackTrace();
		}
		
		try {
			features.add(new TFISFFeature(tokens, sentences, paragraphs));
		} catch (IOException e) {
			System.err.println("TS-ISF feature failed.");
			e.printStackTrace();
		}
		
		try {
			features.add(new NamedEntityFeature(tokens, sentences, paragraphs));
		} catch (IOException e) {
			System.err.println("Named entity feature failed.");
			e.printStackTrace();
		}
		
		if(title != null) {
			try {
				features.add(new TitleTermFeature(title, tokens, sentences, paragraphs));
			} catch (IOException e) {
				System.err.println("Title terms feature failed.");
				e.printStackTrace();
			}
		}
		
		try {
			features.add(new CuePhraseFeature(tokens, sentences, paragraphs));
		} catch (IOException e) {
			System.err.println("Cue phrases feature failed.");
			e.printStackTrace();
		}
		
		try {
			features.add(new ShortSentenceFeature(tokens, sentences, paragraphs));
		} catch (IOException e) {
			System.err.println("Short sentence feature failed.");
			e.printStackTrace();
		}
		
		if(query != null) {
			try {
				features.add(new QueryBiasFeature(query, tokens, sentences, paragraphs));
			} catch (IOException e) {
				System.err.println("Query bias feature failed.");
				e.printStackTrace();
			}
		}
		
		//Executing features
		for(Feature feature : features) {
			weights.add(feature.getWeights());
		}
		
		return weights;
	}
	
	public void setTitle(Vector<TokenInfo> titleTokens) {
		this.title = titleTokens;
	}
	
	public void setQuery(Vector<TokenInfo> queryTokens) {
		this.query = queryTokens;
	}

}
