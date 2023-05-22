package solver;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {

        String dictString = args[0];

        File f = new File("resources/test_sowpods.txt");
        File dictFile = new File(dictString);
        WordGraph dict = WordGraph.readFile(dictFile);

        ArrayList<Board> boards = new ArrayList<>();

        Scanner sc = new Scanner(f);

        while(sc.hasNextLine()) {

            String line = sc.nextLine();

            ArrayList<String> lines = new ArrayList<>();
            lines.add(line);
            int size = Integer.parseInt(line.strip());
            for(int i = 0 ; i <= size; i++) {
                line = sc.nextLine();
                lines.add(line);
            }
            Board b = new Board(lines);
            boards.add(b);
        }

        sc.close();

        long start = System.currentTimeMillis();

        for(Board b : boards) {
            System.out.println("Input Board:\n"+b);
            Solver s = new Solver(dict, b, b.getRack());
            s.getMoves();
            System.out.println("Solution " + s.getOutputWord() + " has " + s.getCurrHighScore() + " points");
            System.out.println("Solution board:\n"+s.getOutputBoard()+"\n");
        }

        
        long end = System.currentTimeMillis();

        System.out.println(end-start + " ms");

    }
}