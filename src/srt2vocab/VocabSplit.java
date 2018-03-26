package srt2vocab;

import com.atilika.kuromoji.unidic.Token;
import com.atilika.kuromoji.unidic.Tokenizer;
import java.util.List;

public class VocabSplit {
    private static Tokenizer tokenizer = new Tokenizer();
    private static WanaKanaJava wkj = new WanaKanaJava(false);
    public static void getVocab(String input) {
        
        for(Token t : tokenizer.tokenize(input)) {
            
            System.out.println("Word: " + t.getWrittenBaseForm() + " " + t.getPartOfSpeechLevel1());
            List<Def> defs = Lookup.lookup(t.getLemma(), t.getLemmaReadingForm(), t.getPartOfSpeechLevel1());
            if(defs == null) {
                System.out.println("MISS");
            } else {
                System.out.println(defs.size()+" hits");
                for(Def def : defs) {
                    System.out.println(def);
                }
            }
            
        }
    }
}
