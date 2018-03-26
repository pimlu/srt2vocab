package srt2vocab;

public class Main {

    public static void main(String[] args) throws Exception {
        System.err.println("initting...");
        long start = System.currentTimeMillis();
        Lookup.init();
        System.gc();
        double elapsed = (System.currentTimeMillis()-start)/1000.0;
        System.err.printf("initted in %f seconds.\n", elapsed);
        if(args.length == 0) {
            die("usage:\n"
                    + "  srt2vocab -vocab 'my japanese sentence'\n"
                    + "  srt2vocab file1.srt file2.srt etc.srt > deck.csv");
        } else if(args[0].equals("-dict")) {
            if(args.length != 2)
                die("Need 2 arguments for -dict, got "+args.length);
            VocabSplit.getVocab(args[1]);
        } else {
            for(String f : args) {
                for(String s : SrtReader.getLines(f)) {
                    System.out.println(s);
                }
            }
        }
    }
    static void die(String msg) {
        System.err.println(msg);
        System.exit(1);
    }
    
}
