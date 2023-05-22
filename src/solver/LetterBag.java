package solver;

import java.util.ArrayList;

public class LetterBag {

    ArrayList<Character> bag;

    public LetterBag() {
        this.bag = new ArrayList<Character>();
        String alphabet = "abcdefghijklmnopqrstuvwxy";
        for(char c : alphabet.toCharArray()) {
            int charNum = 0;
            int howMany = LetterValues.getLetterBagCount(c);
            while(charNum < howMany) {
                bag.add(c);
                charNum++;
            }
        }
    }

    public void add(Character c) {
        this.bag.add(c);
    }

    public void remove(Character c) {
        bag.remove(c);
    }

    public boolean contains(Character c) {
        return bag.contains(c);
    }

    public ArrayList<Character> getBag() {
        return this.bag;
    }
}
