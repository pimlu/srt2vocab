package srt2vocab;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("initting...");
        long start = System.currentTimeMillis();
        Lookup.init();
        System.gc();
        double elapsed = (System.currentTimeMillis()-start)/1000.0;
        System.out.println("took "+elapsed);
        String test = "私はたべたい";
        VocabSplit.getVocab(test);
    }
    
}
