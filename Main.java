public class Main {
    public static void main(String[] args) {
        try {
            //MarieEmulator MARIE = new MarieEmulator();
            MarieAssembler MARIEass = new MarieAssembler("source.marie", 688, 688 + 23);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
