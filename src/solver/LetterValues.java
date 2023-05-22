package solver;

public class LetterValues {
    public static int getLetterValue(char c) {
        return switch (c) {
            case 'a', 'e', 'u', 'i', 'l', 'n', 'o', 'r', 's', 't' -> 1;
            case 'd', 'g' -> 2;
            case 'b', 'c', 'm', 'p' -> 3;
            case 'f', 'h', 'v', 'w', 'y' -> 4;
            case 'k' -> 5;
            case 'j', 'x' -> 8;
            case 'q', 'z' -> 10;
            default -> 0;
        };
    }
    public static int getLetterBagCount(char c) {
        return switch (c) {
            case 'j', 'k', 'q', 'x', 'z' -> 1;
            case 'b', 'f', 'h', 'm', 'p', 'w', 'y', '*' -> 2;
            case 'g' -> 3;
            case 'd', 'l', 's', 'u' -> 4;
            case 'n', 'r', 't' -> 6;
            case 'o' -> 8;
            case 'a', 'i' -> 9;
            case 'e' -> 12;
            default -> 0;
        };
    }
}
