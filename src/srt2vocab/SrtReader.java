package srt2vocab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class SrtReader {
    public static ArrayList<String> getLines(InputStream input)
            throws FileNotFoundException, ParseException, UnsupportedEncodingException, IOException {
        ArrayList<String> lines = new ArrayList();
        BufferedReader sc = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8));
        //Scanner sc = new Scanner(new File(path));
        skip(sc);
        String timestamp = "[0-9]{2}:[0-9]{2}:[0-9]{2},[0-9]+";
        String numFmt = "[0-9]+";
        String timeFmt = timestamp+" --> "+timestamp;
        String l;
        for(int lnum=1; (l = sc.readLine()) != null;) {
            expect(numFmt, "number", lnum, l);
            lnum++;
            expect(timeFmt, "timestamp", lnum, sc.readLine());
            lnum++;
            // possible to have empty subs... (why?!)
            for(boolean first=true; true; first=false) {
                l = sc.readLine();
                lnum++;
                if(l.isEmpty()) {
                    if(!first) break;
                } else lines.add(l);
            }
        }
        return lines;
    }
    public static void expect(String pattern, String name, int lnum, String l)
            throws ParseException, IOException {
        if(!Pattern.matches(pattern, l))
            throw new ParseException("expected "+name+" at line "+lnum+", got '"+l+"'", lnum);
    }
    // Trick from SO to consume byte order mark
    public static void skip(Reader reader) throws IOException {
        reader.mark(1);
        char[] possibleBOM = new char[1];
        reader.read(possibleBOM);

        if (possibleBOM[0] != '\ufeff') {
            reader.reset();
        }
    }
}
