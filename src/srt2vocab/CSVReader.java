package srt2vocab;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class CSVReader implements Iterable<CSVRecord> {
    
    InputStreamReader reader = null;
    CSVParser parser = null;
    public static InputStream readFile(String path)
            throws FileNotFoundException {
        InputStream is = CSVReader.class.getClassLoader()
                    .getResourceAsStream("resources/"+path);
        if(is != null) return is;
        return new FileInputStream("res/"+path);
    }
    public CSVReader(String path) throws IOException {
        reader = new InputStreamReader(readFile(path));
        parser = CSVFormat.RFC4180.withFirstRecordAsHeader()
                .withIgnoreEmptyLines(true).withTrim().parse(reader);
    }
    public static void writeSerializable(Serializable s, String path)
            throws IOException {
        FileOutputStream fos = null;
        ObjectOutputStream out = null;
        fos = new FileOutputStream(path);
        out = new ObjectOutputStream(fos);
        out.writeObject(s);
        out.close();
    }
    public static Serializable readSerializable(String path)
            throws IOException, ClassNotFoundException {
        InputStream is = readFile(path);
        ObjectInputStream ois = new ObjectInputStream(is);
        Serializable s = (Serializable) ois.readObject();
        ois.close();
        return s;
    }
    
    public void close() throws IOException {
        if(reader != null) reader.close();
        if(parser != null) parser.close();
    }

    @Override
    public Iterator<CSVRecord> iterator() {
        return parser.iterator();
    }
}
