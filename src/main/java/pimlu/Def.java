package pimlu;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.csv.CSVRecord;

public class Def implements Serializable {
    public static class Sense implements Serializable {
        public String[] glosses, pos, misc;
        public Sense(CSVRecord r) {
            glosses = rs(r, "def");
            pos = rs(r, "pos");
            misc = rs(r, "misc");
        }
        public boolean misc(String str) {
            for(String m : misc)
                if(m.equals(str)) return true;
            return false;
        }
        public String toString() {
            String prefix = String.join(", ", pos)+"; "+String.join(", ", misc);
            String sep = "\n    ";
            return prefix + sep + String.join(sep, glosses);
        }
        public String toHTML() {
            String prefix = String.format("  <div class=\"s\">%s; %s.</div>\n",
                    String.join(", ", pos), String.join(", ", misc));
            prefix += "  <ul>\n    <li>";
            String sep = "</li>\n    <li>";
            String post = "</li>\n  </ul>";
            return prefix + String.join(sep, glosses) + post;
        }
    }
    public int eid;
    public String[] words, readings;
    public Sense[] senses;
    public static int rp(CSVRecord r, String k) {
        return Integer.parseInt(r.get(k));
    }
    public static String[] rs(CSVRecord r, String k) {
        return r.get(k).split("@");
    }
    public Def(CSVRecord r) {
        eid = rp(r, "eid");
        words = rs(r, "kanji");
        readings = rs(r, "reading");
    }
    public Def(Def o) {
        eid = o.eid;
        words = o.words;
        readings = o.readings;
        senses = o.senses;
    }
    public Set<String> pos() {
        return Stream.of(senses)
                .flatMap(s -> Stream.of(s.pos)).collect(Collectors.toSet());
    }
    private static <T> Set<T> intersect(Set<T> s, List<T> i) {
        s.retainAll(i);
        return s;
    }
    public boolean matchAnyPos(List<String> o) {
        return !intersect(pos(), o).isEmpty();
    }
    public boolean matchAllPos(List<String> o) {
        return pos().equals(new HashSet(o));
    }
    public boolean misc(String str) {
        for(Sense s : senses)
            if(s.misc(str)) return true;
        return false;
    }
    public boolean empty() {
        return senses.length == 0;
    }
    public Def filterMisc(String str) {
        Def def = new Def(this);
        def.senses = Arrays.stream(senses)
                .filter(s -> s.misc(str)).toArray(Sense[]::new);
        return def;
    }
    
    public String toString() {
        String header = String.format("%s: [%s]\n",String.join(", ", words),
                String.join(", ", readings));
        String body = "  "+String.join("\n  ", Stream.of(senses)
                .map(s -> s.toString()).collect(Collectors.toList()));
        return header + body;
    }
    public String toHTML() {
        String header = String.format("<div class=\"d\">\n"
                + "<div class=\"t\">%s: [%s]</div>\n",String.join(", ", words),
                String.join(", ", readings));
        String body = String.join("\n", Stream.of(senses)
                .map(s -> s.toHTML()).collect(Collectors.toList()));
        return header + body + "\n</div>";
    }
}