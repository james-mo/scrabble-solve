package solver;

import java.util.ArrayList;

/**
 * Represents a rack of tiles.
 */
public class Rack {
    private final ArrayList<Character> rack;
    private final Character blank = '*';

    public Rack() {
        rack = new ArrayList<>();
    }

    public Rack(String rackStr) {
        this.rack = new ArrayList<>();
        for(Character c : rackStr.toCharArray()) {
            this.rack.add(c);
        }
    }

    public boolean contains(Character c) {
        return rack.contains(c);
    }

    public boolean containsBlank() {
        return rack.contains(blank);
    }

    public void add(Character c) {
        rack.add(c);
    }

    public void addBlank() {
        rack.add(blank);
    }

    public void remove(Character c) {
        rack.remove(c);
    }

    public void removeBlank() {
        rack.remove(blank);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(Character c : this.rack) {
            sb.append(c).append(" ");
        }

        return sb.toString();
    }

    public Rack copy() {
        Rack copy = new Rack();
        for(Character c : this.rack) {
            if(c==this.blank) {
                copy.addBlank();
            }
            else {
                copy.add(c);
            }
        }
        return copy;
    }

    public int size() {
        return rack.size();
    }

    public ArrayList<Character> getRack() {
        return this.rack;
    }
}
