import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class MarieEmulator {
    private final Scanner inputScanner = new Scanner(System.in);

    public ArrayList<String> lines;
    public ArrayList<String>[] lineTokens;

    public HashMap<String, Integer> variables;
    public HashMap<String, Integer> markers;

    public int ac;

    public PrintChars chars;

    public MarieEmulator(String filePath) throws MARIEException {
        lines = new ArrayList<>();
        variables = new HashMap<>();
        markers = new HashMap<>();

        ac = 0;

        chars = new PrintChars();

        readCode(filePath);
        findMarkers();
        evaluate();
    }

    public MarieEmulator() throws MARIEException {
        this("source.marie");
    }

    @SuppressWarnings ("unchecked")
    public void readCode(String filePath) {
        try {
            BufferedReader buff = new BufferedReader(new FileReader(filePath));
            
            String line;
            while ((line = buff.readLine()) != null) {
                lines.add(line);
            }

            buff.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        lineTokens = new ArrayList[lines.size()];

        for (int i = 0; i < lines.size(); i++) {
            lineTokens[i] = tokenize(i);
        }
    }

    public ArrayList<String> tokenize(int lineNumber) {
        ArrayList<String> tokens = new ArrayList<>();

        Scanner scanner = new Scanner(lines.get(lineNumber));
        while (scanner.hasNext()) {
            tokens.add(scanner.next());
        }
        scanner.close();

        return tokens;
    }

    public void findMarkers() {
        for (int i = 0; i < lineTokens.length; i++) {
            if (lineTokens[i].contains(":")) {
                markers.put(lineTokens[i].get(lineTokens[i].indexOf(":") + 1), i + 1);
            }
        }
    }

    public void evaluate() throws MARIEException {
        int lineNumber = 1;
        int nextLine = 1;

        while (lineNumber <= lineTokens.length) {
            if (lineTokens[lineNumber - 1].isEmpty()) {
                nextLine++;
                lineNumber = nextLine;
                continue;
            }
            int tokenCounter = 0;
            ArrayList<String> tokens = lineTokens[lineNumber - 1];
            String first = tokens.get(tokenCounter);
            tokenCounter++;
            if (first.toLowerCase().equals("load")) {
                if (tokens.size() >= tokenCounter + 1) load(tokens.get(tokenCounter));
                else throw new NoArgumentException(first);
                nextLine++;
                tokenCounter += 2;
            } else if (first.toLowerCase().equals("store")) {
                if (tokens.size() >= tokenCounter + 1) store(tokens.get(tokenCounter));
                else throw new NoArgumentException(first);
                nextLine++;
                tokenCounter += 2;
            } else if (first.toLowerCase().equals("add")) {
                if (tokens.size() >= tokenCounter + 1) add(tokens.get(tokenCounter));
                else throw new NoArgumentException(first);
                nextLine++;
                tokenCounter += 2;
            } else if (first.toLowerCase().equals("subt")) {
                if (tokens.size() >= tokenCounter + 1) subt(tokens.get(tokenCounter));
                else throw new NoArgumentException(first);
                nextLine++;
                tokenCounter += 2;
            } else if (first.toLowerCase().equals("jump")) {
                if (tokens.size() >= tokenCounter + 1) nextLine = jump(tokens.get(tokenCounter));
                else throw new NoArgumentException(first);
                tokenCounter += 2;
            } else if (first.toLowerCase().equals("skipcond")) {
                if (tokens.size() >= tokenCounter + 1) nextLine += skipcond(tokens.get(tokenCounter));
                else throw new NoArgumentException(first);
                tokenCounter += 2;
            } else if (first.toLowerCase().equals("input")) {
                ac = input();
                nextLine++;
                tokenCounter++;
            } else if (first.toLowerCase().equals("output")) {
                output();
                nextLine++;
                tokenCounter++;
            } else if (first.toLowerCase().equals("outputc")) {
                outputC();
                nextLine++;
                tokenCounter++;
            } else if (first.toLowerCase().equals("outputd")) {
                outputD();
                nextLine++;
                tokenCounter++;
            } else if (first.toLowerCase().equals("clear")) {
                clear();
                nextLine++;
                tokenCounter++;
            } else if (first.toLowerCase().equals("halt")) {
                return;
            } else {
                throw new MARIEException();
            }
            lineNumber = nextLine;
        }
    }

    public void load(String key) throws VariableDoesNotExistException {
        if (variables.containsKey(key)) {
            ac = variables.get(key);
        } else throw new VariableDoesNotExistException(key);
    }

    public void store(String key) {
        variables.put(key, ac);
    }

    public void add(String key) throws VariableDoesNotExistException {
        if (variables.containsKey(key)) {
            ac += variables.get(key);
        } else if (key.charAt(0) == '#') {
            ac += Integer.parseInt(key.substring(1));
        } else throw new VariableDoesNotExistException(key);
    }

    public void subt(String key) throws VariableDoesNotExistException {
        if (variables.containsKey(key)) {
            ac -= variables.get(key);
        } else if (key.charAt(0) == '#') {
            ac -= Integer.parseInt(key.substring(1));
        } else throw new VariableDoesNotExistException(key);
    }

    public int jump(String marker) throws MarkerDoesNotExistException {
        if (markers.containsKey(marker)) {
            return markers.get(marker);
        } else {
            try {
                Integer.parseInt(marker);
            } catch (Exception e) {
                throw new MarkerDoesNotExistException(marker);
            }
            return Integer.parseInt(marker);
        }
    }

    public int skipcond(String condition) throws UnrecognizedConditionException {
        if (condition.equals("00")) {
            if (ac < 0) return 2;
        } else if (condition.equals("01")) {
            if (ac == 0) return 2;
        } else if (condition.equals("10")) {
            if (ac > 0) return 2;
        } else throw new UnrecognizedConditionException(condition);
        return 1;
    }

    public int input() {
        return inputScanner.nextInt();
    }

    public void output() {
        System.out.println(ac);
    }

    public void outputC() {
        if (ac < 10) return;
        int digits = 1;
        int temp = ac;
        while (temp / 100 > 0) {
            digits++;
            temp /= 100;
            if (temp < 10) return;
        }
        ArrayList<Integer> letters = new ArrayList<>();
        for (int i = digits - 1; i >= 0; i--) {
            int letterCode = ac / (int) Math.pow(100, i) % 100 - 10;
            letters.add(letterCode);
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < letters.size(); j++) {
                System.out.print(chars.getLetter(letters.get(j))[i]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void outputD() {
        int digits = ac == 0 || ac == 1 ? 1 : (int) Math.ceil(logBase27(ac));
        int[] letters = new int[digits > 0 ? digits : 1];
        for (int i = digits - 1; i >= 0; i--) {
            int letterCode = (int) (ac / Math.pow(27, i));
            letters[digits - i - 1] = letterCode % 27;
        }
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < letters.length; j++) {
                System.out.print(chars.getLetter(letters[j])[i]);
            }
            System.out.println();
        }
        System.out.println();
    }

    public void clear() {
        ac = 0;
    }

    public void printVariables() {
        System.out.println(variables.toString());
    }

    public void printMarkers() {
        System.out.println(markers.toString());
    }

    public void printAC() {
        System.out.println("AC: " + ac);
    }

    public double logBase27(double num) {
        return Math.log(num) / Math.log(27);
    }

    private class MARIEException extends Exception {
        public MARIEException(String message) {
            super(message);
        }
        public MARIEException() {
            this("This isn't how you program dumbass.");
        }
    }

    private class VariableDoesNotExistException extends MARIEException {
        public VariableDoesNotExistException(String variable) {
            super("Variable: " + variable + " does not exist.");
        }
    }

    private class NoArgumentException extends MARIEException {
        public NoArgumentException(String instruction) {
            super("Instruction: " + instruction + " requires an argument.");
        }
    }

    private class MarkerDoesNotExistException extends MARIEException {
        public MarkerDoesNotExistException(String marker) {
            super("Marker: " + marker + " does not exist.");
        }
    }

    private class UnrecognizedConditionException extends MARIEException {
        public UnrecognizedConditionException(String condition) {
            super("Condition: " + condition + " does not exist.");
        }
    }
}