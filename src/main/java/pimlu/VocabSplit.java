package pimlu;

import com.atilika.kuromoji.unidic.Token;
import com.atilika.kuromoji.unidic.Tokenizer;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
    public static void countVocab(DefCounter counter, String input, Predicate<Token> filter) {
        for(Token t : tokenizer.tokenize(input)) {
            if(!filter.test(t)) continue;
            List<Def> defs = Lookup.lookup(t.getLemma(), t.getLemmaReadingForm(), t.getPartOfSpeechLevel1());
            if(defs == null) continue;
            String txt = String.join("\n", defs.stream().map(d -> d.toHTML()).collect(Collectors.toList()));
            counter.hit(t.getWrittenBaseForm(), txt);
        }
    }
    public static final Predicate<Token> prtFilter = t -> {
        String l = t.getLemma();
        boolean isPrt = t.getPartOfSpeechLevel1().equals("助詞");
        boolean isMonoPrt = isPrt && l.length()==1;
        return !isMonoPrt && !l.equals("だ");
    };
}
