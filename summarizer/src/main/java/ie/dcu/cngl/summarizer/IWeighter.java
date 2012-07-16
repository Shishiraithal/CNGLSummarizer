package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenInfo;

import java.util.ArrayList;

public interface IWeighter {
	
	public ArrayList<Double[]> calculateWeights();
	
	public void addFeature(Feature feature);

	public void setTitle(ArrayList<TokenInfo> vector);

	public void setQuery(ArrayList<TokenInfo> vector);

	public void setStructure(PageStructure structure);

}
