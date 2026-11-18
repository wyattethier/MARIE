public class Main {
    public static void main(String[] args) {
        int startingLine = 1673;
        int numberOfLines = 23;
        if (args.length >= 2) {
            startingLine = Integer.parseInt(args[0]);
            numberOfLines = Integer.parseInt(args[1]);
        }
        startingLine--;
        numberOfLines++;
        int startingAddress = startingLine + numberOfLines;
        try {
            MarieEmulator MARIE = new MarieEmulator();
            //MarieCompiler MARIEass = new MarieCompiler("source.marie", startingLine, startingAddress);
            //MARIE.printVariables();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
