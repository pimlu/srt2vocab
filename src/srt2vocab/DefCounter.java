package srt2vocab;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class DefCounter  {
    static class DefText {
        static final double CUTOFF = 0.05;
        final HashMap<String, Integer> writtens = new HashMap();
        final String txt;
        public DefText(String txt) {
            this.txt = txt;
        }
        public void hit(String w) {
            if(!writtens.containsKey(w)) writtens.put(w, 0);
            writtens.put(w, writtens.get(w)+1);
        }
        public String title() {
            ArrayList<String> keys = new ArrayList(writtens.keySet());
            keys.sort((a,b) ->
                    Integer.compare(writtens.get(b), writtens.get(a)));
            int cumHits = 0;
            String title = "";
            for(String k : keys) {
                int curHits = writtens.get(k);
                cumHits += curHits;
                if(curHits / (double) cumHits < CUTOFF) break;
                title += ", "+k;
            }
            return title.substring(2);
        }
        public int hits() {
            return writtens.values().stream().mapToInt(Integer::intValue).sum();
        }
    }
    // treemap because defs are long
    private final TreeMap<DefText, DefText> defs =
            new TreeMap<>((a, b) -> a.txt.compareTo(b.txt));
    public void hit(String written, String txt) {
        DefText dt = new DefText(txt);
        if(!defs.containsKey(dt)) defs.put(dt, dt);
        defs.get(dt).hit(written);
    }
    public void writeTSV(String path) throws IOException {
        BufferedWriter writer = null;
        CSVPrinter p = null;
        try {
            writer = Files.newBufferedWriter(Paths.get(path));
            p = new CSVPrinter(writer, CSVFormat.POSTGRESQL_TEXT);
            ArrayList<DefText> freq = new ArrayList(defs.keySet());
            freq.sort((a,b) -> Integer.compare(b.hits(), a.hits()));
            for(DefText dt : freq) {
                p.printRecord(dt.title(), dt.hits(), dt.txt);
            }
        } finally {
            if(p != null) p.close();
            if(writer != null) writer.close();
        }
    }
}
