package solver;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class WordGraph {

    private Node root;
    private Node current_node;

    public class Node {
        private boolean terminal;
        private HashMap<Character,Node> children = new HashMap<>();
        public Node(boolean terminal) {
            this.terminal = terminal;
        }
        public boolean isWord() {
            return terminal;
        }

        public HashMap<Character, Node> getChildren() {
            return children;
        }
    }

    public WordGraph(ArrayList<String> words) {
        this.root = new Node(false);

        for(String word : words) {
            current_node = this.root;
            for(int i = 0; i < word.length(); i++) {
                char letter = word.charAt(i);
                current_node.children.putIfAbsent(letter, new Node(false));
                current_node = current_node.children.get(letter);
            }
            current_node.terminal = true;
        }
    }

    public Node lookupWord(String word) {
        current_node = this.root;
        for(int i = 0; i < word.length(); i++) {
            char letter = word.charAt(i);
            if(!current_node.children.containsKey(letter)) {
                return null;
            }
            current_node = current_node.children.get(letter);
        }
        return current_node;
    }

    public boolean isWord(String word) {
        Node wordNode = this.lookupWord(word);

        if(wordNode==null) {
            return false;
        }

        return wordNode.terminal;
    }

    public static WordGraph readFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);
        ArrayList<String> words = new ArrayList<>();
        while(sc.hasNextLine()) {
            String word = sc.nextLine().strip().toLowerCase();
            words.add(word);
        }
        sc.close();
        return new WordGraph(words);
    }

    public Node getRoot() {
        return this.root;
    }
}
