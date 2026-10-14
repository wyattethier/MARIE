import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import java.io.*;

public class MarieAssembler {
    public ArrayList<String> lines;
    public ArrayList<String>[] lineTokens;

    public HashMap<String, Integer> opCodes;
    public HashMap<Integer, Character> hexValues;
    public HashMap<String, Integer> addresses;
    public HashMap<String, Integer> markers;

    public MarieAssembler() throws Exception {
        this("source.marie", 1, 160);
    }

    public MarieAssembler(String filePath, int startingLine, int startingAddress) throws Exception {
        lines = new ArrayList<>();

        opCodes = new HashMap<>();
        opCodes.put("load", 1);
        opCodes.put("store", 2);
        opCodes.put("add", 3);
        opCodes.put("subt", 4);
        opCodes.put("input", 5);
        opCodes.put("output", 6);
        opCodes.put("halt", 7);
        opCodes.put("skipcond", 8);
        opCodes.put("jump", 9);
        opCodes.put("clear", 10);

        hexValues = new HashMap<>();
        hexValues.put(0, '0');
        hexValues.put(1, '1');
        hexValues.put(2, '2');
        hexValues.put(3, '3');
        hexValues.put(4, '4');
        hexValues.put(5, '5');
        hexValues.put(6, '6');
        hexValues.put(7, '7');
        hexValues.put(8, '8');
        hexValues.put(9, '9');
        hexValues.put(10, 'a');
        hexValues.put(11, 'b');
        hexValues.put(12, 'c');
        hexValues.put(13, 'd');
        hexValues.put(14, 'e');
        hexValues.put(15, 'f');
        
        addresses = new HashMap<>();
        addresses.put("One", startingAddress + 3);
        addresses.put("Two", startingAddress + 4);
        markers = new HashMap<>();

        readCode(filePath);
        findMarkers(startingLine);
        compile(startingLine, startingAddress);
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

        for (int i = 0; i < lineTokens.length; i++) {
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

    public void findMarkers(int startingLine) {
        for (int i = 0; i < lineTokens.length; i++) {
            if (lineTokens[i].contains(":")) {
                markers.put(lineTokens[i].get(lineTokens[i].indexOf(":") + 1), i + 1 + startingLine);
            }
        }
    }

    public void print(int lineNumber, int opCode, int address) throws Exception {
        System.out.printf("%-6s%16s%7s\n", 
        intToHex(lineNumber, 3).toString(), 
        intToBinary(opCode, 4) + intToBinary(address, 12), 
        intToHex(opCode, 1).toString() + intToHex(address, 3).toString());
    }

    public Hex intToHex(int input, int length) throws Exception {
        char[] digits = new char[length];
        for (int i = 0; i < length; i++) {
            digits[length - i - 1] = hexValues.get((int) (input / Math.pow(16, i)) % 16);
        }
        String hex = "";
        for (int i = 0; i < length; i++) {
            hex += digits[i];
        }
        return new Hex(hex);
    }

    public String intToBinary(int input, int length) {
        String binary = Integer.toBinaryString(input);
        while (binary.length() < length) {
            binary = "0" + binary;
        }
        return binary;
    }

    public int binaryToInt(String binary) {
        int sum = 0;
        for (int i = 0; i < binary.length(); i++) {
            if (binary.charAt(i) == '1') {
                sum += Math.pow(2, binary.length() - i - 1);
            }
        }
        return sum;
    }

    public double logBase2(int num) {
        return Math.log(num) / Math.log(2);
    }

    public void compile(int startingLine, int startingAddress) throws Exception {
        int lineNumber = 1;
        int numAddresses = 0;
        while (lineNumber <= lineTokens.length) {
            if (lineTokens[lineNumber - 1].isEmpty()) {
                lineNumber++;
                continue;
            }
            ArrayList<String> tokens = lineTokens[lineNumber - 1];
            int opCode = opCodes.get(tokens.get(0).toLowerCase());
            switch (opCode) {
                case 1 : 
                    print(lineNumber + startingLine, opCode, addresses.get(tokens.get(1)));
                    break;
                case 2 :
                    addresses.put(tokens.get(1), startingAddress + numAddresses);
                    print(lineNumber + startingLine, opCode, startingAddress + numAddresses);
                    numAddresses++;
                    break;
                case 3 :
                    print(lineNumber + startingLine, opCode, addresses.get(tokens.get(1)));
                    break;
                case 4 :
                    print(lineNumber + startingLine, opCode, addresses.get(tokens.get(1)));
                    break;
                case 5 :
                    print(lineNumber + startingLine, opCode, 0);
                    break;
                case 6 :
                    print(lineNumber + startingLine, opCode, 0);
                    break;
                case 7 :
                    print(lineNumber + startingLine, opCode, 0);
                    break;
                case 8 :
                    print(lineNumber + startingLine, opCode, binaryToInt(tokens.get(1) + "0000000000"));
                    break;
                case 9 :
                    print(lineNumber + startingLine, opCode, markers.get(tokens.get(1)));
                    break;
                case 10 :
                    print(lineNumber + startingLine, opCode, 0);
                    break;
                default :
                    break;
            }
            lineNumber++;
        }
    }

    @SuppressWarnings ("unused")
    private class Hex {
        private char[] number;

        public Hex(String value) throws HexException {
            number = new char[value.length()];
            for (int i = 0; i < number.length; i++) {
                if (Character.isDigit(value.charAt(i))) {
                    number[i] = value.charAt(i);
                } else if (Character.toLowerCase(value.charAt(i)) > 96 && Character.toLowerCase(value.charAt(i)) < 103) {
                    number[i] = Character.toLowerCase(value.charAt(i));
                } else throw new HexException(value.charAt(i));
            }
        }

        public void increment(char[] value, int length) {
            if (length == 0) return;
            int lastIndex = length - 1;
            if (value[lastIndex] > 47 && value[lastIndex] < 57 || value[lastIndex] > 96 && value[lastIndex] < 102) {
                value[lastIndex]++;
            } else if (value[lastIndex] == '9') {
                value[lastIndex] = 'a';
            } else if (value[lastIndex] == 'f') {
                value[lastIndex] = '0';
                increment(value, length - 1);
            }
        }

        public char getChar(int index) {
            return number[index];
        }

        public boolean equals(Hex other) {
            for (int i = 0; i < number.length; i++) {
                if (this.getChar(i) != other.getChar(i)) return false;
            } 
            return true;
        }

        public String toString() {
            String string = "";
            for (int i = 0; i < number.length; i++) {
                string += number[i];
            }
            return string;
        }

        public class HexException extends Exception {
            public HexException() {
                super("Improper hexadecimal value.");
            }
            public HexException(String message) {
                super("Value, " + message + " is not a proper hexadecimal value.");
            }
            public HexException(char c) {
                super("Digit, " + c + ", is not a proper hexadecimal value.");
            }
        }
    }
}