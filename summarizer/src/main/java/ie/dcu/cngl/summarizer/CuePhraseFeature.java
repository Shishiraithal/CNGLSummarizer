package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.PageStructure;
import ie.dcu.cngl.tokenizer.TokenizerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.lang.StringUtils;

public class CuePhraseFeature extends Feature {
	
	private ArrayList<SentenceScore> cuePhrases;

	public CuePhraseFeature(PageStructure structure) throws IOException {
		super(structure);
		cuePhrases = new ArrayList<SentenceScore>();
		try {
            File file = new File(SummarizerUtils.cuePhrasesFile);
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while((line = reader.readLine()) != null) {
                if (!(line.equals(StringUtils.EMPTY) || line.startsWith(TokenizerUtils.COMMENT))) {
				    line = line.toLowerCase();
				    String [] phraseAndWeight = line.split(",");
				    SentenceScore cuePhrase = new SentenceScore(phraseAndWeight[0].trim(), Integer.parseInt(phraseAndWeight[1].trim()));
				    cuePhrases.add(cuePhrase);
                }
            }
            reader.close();
        } catch (IOException o) {
            System.out.println("ERROR: exception " + o);
            return;
        }
	}

	@Override
	public Double[] getWeights() {
		Double [] weights = new Double[structure.getNumSentences()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 0.0;
		}

		for(int i = 0; i < structure.getNumSentences(); i++) {
			for(int j = 0; j < cuePhrases.size(); j++) {
				SentenceScore cuePhrase = cuePhrases.get(j);
				weights[i]+=(getNumOccurences(cuePhrase.getSentence().toLowerCase(), structure.getSentences().get(i).getValue().toLowerCase())*cuePhrase.getScore());
			}
		}
		
		normalise(weights);
		return weights;
	}

}
