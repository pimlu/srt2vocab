package pimlu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.apache.commons.csv.CSVRecord;

public class Lookup {
    private static final boolean FILTER_KANA = false;
    
    private static class MultiDict<K,V> implements Serializable {
        private HashMap<K, ArrayList<V>> dict = new HashMap<>();
        
        public void add(K key, V val) {
            if(!dict.containsKey(key)) dict.put(key, new ArrayList());
            dict.get(key).add(val);
        }
        public void add(K key, V[] val) {
            if(!dict.containsKey(key)) dict.put(key, new ArrayList());
            dict.get(key).addAll(Arrays.asList(val));
        }
        public ArrayList<V> get(K key) {
            return dict.get(key);
        }
        public boolean has(K key) {
            return dict.containsKey(key);
        }
    }
    private static WanaKanaJava wkj = new WanaKanaJava(false);
    private static MultiDict<String, String> posmap = mkPosMap();
    private static MultiDict<String, Def> words = new MultiDict(),
            phons = new MultiDict();
    public static void init() throws IOException {
        CSVReader entryReader = null, senseReader = null;
        //try {
            entryReader = new CSVReader("entry.csv");
            senseReader = new CSVReader("sense.csv");
        //} catch (IOException ex) {
        //    throw new IOException("Failed to load csv files.");
        //}
        HashMap<Integer, Def> eidLookup = new HashMap();
        MultiDict<Integer, Def.Sense> senseArrs = new MultiDict(); 
        for(CSVRecord r : entryReader) {
            Def def = new Def(r);
            for(String word : def.words) {
                words.add(word, def);
            }
            for(String reading : def.readings) {
                phons.add(process(reading), def);
            }
            eidLookup.put(Def.rp(r, "eid"), def);
        }
        for(CSVRecord r : senseReader) {
            Def.Sense s = new Def.Sense(r);
            senseArrs.add(Def.rp(r, "eid"), s);
        }
        
        for(Entry e : eidLookup.entrySet()) {
            Def def = (Def) e.getValue();
            List<Def.Sense> senses = senseArrs.get((Integer) e.getKey());
            def.senses = senses.toArray(new Def.Sense[senses.size()]);
        }
    }
    private static MultiDict<String, String> mkPosMap() {
        MultiDict<String, String> pm = new MultiDict();
        // Mappping made mostly by hand from guesses looking at the data.
        // LHS is from a parser's perspective (goes into great detail about
        // particles), RHS is from a dictionary's perspective (detail on
        // conjugations and such).  Probably has mistakes.
        pm.add("名詞", "n vs adv-to n-adv n-t".split(" "));
        pm.add("代名詞", "n pn".split(" "));
        pm.add("接頭辞", "n-pref pref".split(" "));
        pm.add("動詞", ("v1 v2a-s v4h v4r v5 v5aru v5b v5g v5k v5k-s v5m v5n "+
                "v5r v5r-i v5s v5t v5u v5u-s v5uru v5z vz vi vk vn vr")
                .split(" "));
        pm.add("形容詞", "adj-i adj-na".split(" "));
        // what even? shape word? they're all na adjectives
        pm.add("形状詞", "adj-na n".split(" "));
        pm.add("副詞", "adv");
        pm.add("連体詞", "adj-pn");
        pm.add("接続詞", "conj");
        pm.add("助詞", "prt");
        pm.add("助動詞", "aux aux-v aux-adj".split(" "));
        pm.add("感動詞", "int");
        pm.add("接尾辞", "n-suf suf".split(" "));
        
        String[] none = new String[0];
        
        // Hide punctuation
        pm.add("補助記号", none);
        // ???
        pm.add("その他", none);
        pm.add("空白", none);
        pm.add("記号", none);
        return pm;
    }
    private static String process(String in) {
        return wkj.toHiragana(in.replaceAll("\\s", ""));
    }
    private static List<Def> filter(ArrayList<Def> raws, List<String> pos, boolean kana) {
        List<Def> defs = raws.stream()
                .filter(def -> def.matchAnyPos(pos))
                .collect(Collectors.toList());
        // Some defintions are sketchy and just give you "exp" for the pos.
        // such matches come after proper pos matches.
        List<Def> expMatches = raws.stream()
                .filter(def -> def.matchAllPos(
                        Arrays.asList(new String[] {"exp"})))
                .collect(Collectors.toList());
        // None of our source poses have exp in them, so we can just add them on
        defs.addAll(expMatches);
        if(FILTER_KANA && kana) {
            List<Def> kanas = defs.stream().filter(def -> def.misc("uk"))
                    .map(def -> def.filterMisc("uk"))
                    .collect(Collectors.toList());
            if(!kanas.isEmpty()) defs = kanas;
        }
        return defs;
    }
    public static List<Def> lookup(String word, String read, String pos) {
        List<String> jmdpos = posmap.get(pos);
        List<Def> match = null;
        // Shouldn't occur, but better to not crash
        if(jmdpos == null) return null;
        //TODO better system for filtering?
        if(words.has(word)) {
            match = filter(words.get(word), jmdpos, false);
            if(match.isEmpty()) match = null;
            if(match != null) return match;
        }
        String hira = process(read);
        if(phons.has(hira)) {
            match = filter(phons.get(hira), jmdpos, false);
            if(match.isEmpty()) match = null;
            if(match != null) return match;
        }
        return null;
    }
}