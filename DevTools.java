import java.util.Scanner;
public class DevTools {
    public static void main(String[] args) {
        String input;
        if (args.length >= 1) {
            input = args[0];
        } else {
            Scanner scanner = new Scanner(System.in);
            input = scanner.next();
            scanner.close();
        }
        int chars = input.length();
        int number = 0;
        if (chars > 0) {
            input = input.toLowerCase();
            for (int i = 0; i < input.length(); i++) {
                char current = input.charAt(i);
                int exp = (int) Math.pow(27, chars - i - 1);
                /* switch (current) {
                    case 'a': number += exp; break;
                    case 'b': number += exp * 2; break;
                    case 'c': number += exp * 3; break;
                    case 'd': number += exp * 4; break;
                    case 'e': number += exp * 5; break;
                    case 'f': number += exp * 6; break;
                    case 'g': number += exp * 7; break;
                    case 'h': number += exp * 8; break;
                    case 'i': number += exp * 9; break;
                    case 'j': number += exp * 10; break;
                    case 'k': number += exp * 11; break;
                    case 'l': number += exp * 12; break;
                    case 'm': number += exp * 13; break;
                    case 'n': number += exp * 14; break;
                    case 'o': number += exp * 15; break;
                    case 'p': number += exp * 16; break;
                    case 'q': number += exp * 17; break;
                    case 'r': number += exp * 18; break;
                    case 's': number += exp * 19; break;
                    case 't': number += exp * 20; break;
                    case 'u': number += exp * 21; break;
                    case 'v': number += exp * 22; break;
                    case 'w': number += exp * 23; break;
                    case 'x': number += exp * 24; break;
                    case 'y': number += exp * 25; break;
                    case 'z': number += exp * 26; break;
                    default : return;
                } */
                if (current < 97 || current > 123) return;
                number += exp * (current - 'a' + 1);
            }
        } else return;
        System.out.println(number);
    }
}
