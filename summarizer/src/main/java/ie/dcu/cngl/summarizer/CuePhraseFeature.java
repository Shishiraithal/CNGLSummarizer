package ie.dcu.cngl.summarizer;

import ie.dcu.cngl.tokenizer.SectionInfo;
import ie.dcu.cngl.tokenizer.TokenInfo;
import ie.dcu.cngl.tokenizer.TokenizerUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;

public class CuePhraseFeature extends Feature {
	
	private ArrayList<SentenceScore> cuePhrases;

	public CuePhraseFeature(Vector<TokenInfo> tokens, Vector<SectionInfo> sentences, Vector<SectionInfo> paragraphs) throws IOException {
		super(tokens, sentences, paragraphs);
		cuePhrases = new ArrayList<SentenceScore>();
		try {
            File file = new File(SummarizationUtils.cuePhrasesFile);
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
		Double [] weights = new Double[sentences.size()];
		for(int i = 0; i < weights.length; i++) {
			weights[i] = 0.0;
		}

		for(int i = 0; i < sentences.size(); i++) {
			for(int j = 0; j < cuePhrases.size(); j++) {
				SentenceScore cuePhrase = cuePhrases.get(j);
				weights[i]+=(getNumOccurences(cuePhrase.getSentence().toLowerCase(), sentences.get(i).getValue().toLowerCase())*cuePhrase.getScore());
			}
		}
		
		normalise(weights);
		return weights;
	}

}
