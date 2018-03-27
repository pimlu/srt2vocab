package srt2vocab;

import java.io.FileInputStream;
import java.io.IOException;

public class Main {
    static final String usage = "usage:\n"
                    + "  srt2vocab --dict 'お前はもう死んでいる'\n"
                    + "  srt2vocab --srt deck.tsv file1.srt f2.srt etc.srt";
    public static void main(String[] args) throws Exception {
        if(args.length == 0) {
            die(usage);
        } else if(args[0].equals("--dict")) {
            
            if(args.length != 2)
                die("need 2 arguments for --dict, got "+args.length);
            
            init();
            VocabSplit.getVocab(args[1]);
        } else if(args[0].equals("--srt")) {
            if(args.length < 3)
                die("need at least 3 arguments for --srt, got"+args.length);
            init();
            DefCounter dc = new DefCounter();
            for(int i=2; i<args.length; i++) {
                System.out.printf("processing %s...\n", args[i]);
                for(String s : SrtReader.getLines(new FileInputStream(args[i]))) {
                    VocabSplit.countVocab(dc, s, VocabSplit.prtFilter);
                }
            }
            dc.writeTSV(args[1]);
        }
    }
    static void init() throws IOException {
        System.out.println("initting...");
        long start = System.currentTimeMillis();
        Lookup.init();
        System.gc();
        double elapsed = (System.currentTimeMillis()-start)/1000.0;
        System.out.printf("initted in %f seconds.\n", elapsed);
    }
    static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    
}
