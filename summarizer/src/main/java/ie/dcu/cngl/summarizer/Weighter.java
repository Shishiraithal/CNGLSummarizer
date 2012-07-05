package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.io.IOException;
import java.util.ArrayList;

public class Weighter implements IWeighter {
	
	private PageStructure structure;
	ArrayList<Feature> features;
	
	private ArrayList<TokenInfo> title;
	private ArrayList<TokenInfo> query;
	
	public Weighter() {
		this.features = new ArrayList<Feature>();
	}
	
	public ArrayList<Double[]> calculateWeights() {
		ArrayList<Double[]> weights = new ArrayList<Double[]>();
		addFeatures();
		
		//Executing features
		for(Feature feature : features) {
			weights.add(feature.getWeights());
		}
		
		return weights;
	}
	
	@Override
	public void addFeatures() {
		try {
			SkimmingFeature skimFeat = new SkimmingFeature(structure);
			skimFeat.setTopTermCutoff(0.3f);
			features.add(skimFeat);
		} catch (IOException e) {
			System.err.println("Skimming feature failed.");
			e.printStackTrace();
		}
		
		try {
			features.add(new TFISFFeature(structure));
		} catch (IOException e) {
			System.err.println("TS-ISF feature failed.");
			e.printStackTrace();
		}
		
		try {
			features.add(new NamedEntityFeature(structure));
		} catch (IOException e) {
			System.err.println("Named entity feature failed.");
			e.printStackTrace();
		}
		
		if(title != null) {
			try {
				features.add(new TitleTermFeature(title, structure));
			} catch (IOException e) {
				System.err.println("Title terms feature failed.");
				e.printStackTrace();
			}
		}
		
		try {
			features.add(new CuePhraseFeature(structure));
		} catch (IOException e) {
			System.err.println("Cue phrases feature failed.");
			e.printStackTrace();
		}
		
		try {
			features.add(new ShortSentenceFeature(structure));
		} catch (IOException e) {
			System.err.println("Short sentence feature failed.");
			e.printStackTrace();
		}
		
		if(query != null) {
			try {
				features.add(new QueryBiasFeature(query, structure));
			} catch (IOException e) {
				System.err.println("Query bias feature failed.");
				e.printStackTrace();
			}
		}
	}

	@Override
	public void setStructure(PageStructure structure) {
		this.structure = structure;
	}
	
	public void setTitle(ArrayList<TokenInfo> titleTokens) {
		this.title = titleTokens;
	}
	
	public void setQuery(ArrayList<TokenInfo> queryTokens) {
		this.query = queryTokens;
	}

}
