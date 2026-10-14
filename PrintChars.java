import java.util.*;
import java.io.*;

public class PrintChars {
    private HashMap<Integer, String[]> letters;

    private String[] smiley = {
        "--00----00--",
        "---0----0---",
        "-----00-----",
        "--0------0--",
        "---000000---"
    };
    
    public PrintChars() {
        letters = new HashMap<>();
        try {
            Scanner scanner = new Scanner(new File("letters.txt"));
            int letter = 1;
            while (letter <= 26) {
                String[] lines = new String[5];
                for (int i = 0; i < lines.length; i++) {
                    lines[i] = scanner.next();
                }
                letters.put(letter, lines);
                letter++;
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        letters.put(0, smiley);
    }

    public String[] getLetter(int i) {
        return letters.get(i);
    }
}
