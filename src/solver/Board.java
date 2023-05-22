package solver;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

/*public class root.Board {
    int size;
    char[][] tiles;
    private String tray;
    public root.Board(int size) {
        this.size = size;
        tiles = new char[size][size];
        for(int i = 0; i < size; i++) {
            for(int j = 0; j < size; j++) {
                tiles[i][j] = Character.UNASSIGNED;
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(char[] row : tiles) {
            for(char c : row){
                if(c!=Character.UNASSIGNED) {
                    sb.append(c);
                }
                else {
                    sb.append("_");
                }
            }
            sb.append("\n");
        }

        return sb.toString();
    }

    public ArrayList<int[]> allPositions() {
        ArrayList<int[]> result = new ArrayList<>();
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                int[] temp = {i,j};
                result.add(temp);
            }
        }

        return result;
    }

    public Character getTile(int[] pos) {
        int row = pos[0];
        int col = pos[1];

        return this.tiles[row][col];
    }

    public void setTile(int[] pos, char tile) {
        int row = pos[0];
        int col = pos[1];

        this.tiles[row][col] = tile;
    }

    public boolean inBounds(int[] pos) {
        int row = pos[0];
        int col = pos[1];

        return row>=0 && row < this.size && col >= 0 && col < this.size;
    }

    public boolean isEmpty(int[] pos) {
        return this.inBounds(pos) && this.getTile(pos)==Character.UNASSIGNED;
    }

    public boolean isFilled(int[] pos) {
        return this.inBounds(pos) && this.getTile(pos)!=Character.UNASSIGNED;
    }

    public root.Board copy() {
        root.Board result = new root.Board(this.size);
        for(int[] pos : this.allPositions()) {
            result.setTile(pos, this.getTile(pos));
        }
        return result;
    }


}*/

public class Board {
    private int size;
    private ScrabbleSquare[][] tiles;
    private Rack rack = new Rack();

    public Board(ScrabbleSquare[][] tiles) {

        int index = 0;
        for(ScrabbleSquare[] row : tiles) {
            index++;
        }

        this.size = index;
        this.tiles = tiles;
    }
    
    public Board(int size) {
        this.size = size;
        this.tiles = new ScrabbleSquare[size][size];
    }

    public Board(File file) throws Exception {
        Scanner sc = new Scanner(file);
        this.size = Integer.parseInt(sc.nextLine().strip());
        this.tiles = new ScrabbleSquare[this.size][this.size];
        ArrayList<String> lines = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!sc.hasNextLine()) {
                for (Character c : line.toCharArray()) {
                    this.rack.add(c);
                }
                break;
            }
            StringBuilder lineNoSpaces = new StringBuilder(line);
            for (int i = 2; i < lineNoSpaces.length(); i += 2) {
                lineNoSpaces.deleteCharAt(i);
            }
            line = lineNoSpaces.toString().toLowerCase();
            lines.add(line);
        }
        sc.close();

        int rowIndex = 0;
        for (String line : lines) {
            for (int col = 0; col < 2 * (this.size) - 1; col += 2) {
                int[] pos = new int[]{rowIndex, col};
                ScrabbleSquare square;
                char c = line.charAt(col);
                if (c != ' ') {
                    square = new ScrabbleSquare(pos);
                    if (c != '.') {
                        square.setWordMulti(Character.getNumericValue(c));
                    } else {
                        if (line.charAt(col + 1) != '.') {
                            square.setLetterMulti(Character.getNumericValue(
                                    line.charAt(col + 1)));
                        }
                    }
                } else {
                    square = new ScrabbleSquare(pos, line.charAt(col + 1));
                }
                int[] posSet = new int[]{rowIndex, col / 2};
                square.setPos(posSet);
                this.tiles[rowIndex][col / 2] = square;
            }
            rowIndex++;
        }
    }

    public Board(ArrayList<String> lines) {
        int size = Integer.parseInt(lines.get(0));
        this.size = size;
        this.tiles = new ScrabbleSquare[size][size];
        for(int i = 1; i <= size; i++) {
            String line = lines.get(i);

            for(int j = 0; j < line.length(); j+=3) {
                
                int[] pos = new int[] {i-1, j/3};
                ScrabbleSquare square = new ScrabbleSquare(pos);
                if(line.charAt(j)==' ') {
                    Character letter = line.charAt(j+1);
                    if(Character.isUpperCase(letter)) {
                        square.setIsBlank();
                    }
                    square.setLetter(letter);
                }
                else if(line.charAt(j)=='.') {
                    if(line.charAt(j+1)!='.') {
                        square.setLetterMulti(Character.getNumericValue(line.charAt(j+1)));
                    }
                }
                else {
                    square.setWordMulti(Character.getNumericValue(line.charAt(j)));
                }

                this.tiles[i-1][j/3] = square;
            }
        }
        String rackStr = lines.get(lines.size()-1);
        Rack boardRack = new Rack(rackStr);
        this.rack = boardRack;
    }

    public Board() throws Exception {

        File file = new File("resources/standardBoard.txt");
        Scanner sc = new Scanner(file);
        this.size = Integer.parseInt(sc.nextLine().strip());
        this.tiles = new ScrabbleSquare[this.size][this.size];
        ArrayList<String> lines = new ArrayList<>();
        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            StringBuilder lineNoSpaces = new StringBuilder(line);
            for(int i = 2; i < lineNoSpaces.length(); i += 2) {
                lineNoSpaces.deleteCharAt(i);
            }
            line = lineNoSpaces.toString().toLowerCase();
            lines.add(line);
        }
        sc.close();
        int rowIndex = 0;
        for(String line : lines) {
            for(int col = 0; col < 2*(this.size)-1; col+=2) {
                int[] pos = new int[] {rowIndex, col};
                ScrabbleSquare square;
                char c = line.charAt(col);
                if(c!=' ') {
                    square = new ScrabbleSquare(pos);
                    if(c!='.') {
                        square.setWordMulti(Character.getNumericValue(c));
                    }
                    else {
                        if(line.charAt(col+1)!='.') {
                            square.setLetterMulti(Character.getNumericValue(
                                    line.charAt(col+1)));
                        }
                    }
                }
                else {
                    square = new ScrabbleSquare(pos, line.charAt(col+1));
                }
                int[] posSet = new int[] {rowIndex, col/2};
                square.setPos(posSet);
                this.tiles[rowIndex][col/2] = square;
            }
            rowIndex++;
        }

        this.rack = new Rack();
    }

    public ScrabbleSquare[][] getTiles() {
        return this.tiles;
    }

    public ArrayList<int[]> getBoardCoords() {
        ArrayList<int[]> result = new ArrayList<>();
        for(int i = 0; i < this.size; i++) {
            for(int j = 0; j < this.size; j++) {
                int[] temp = {i,j};
                result.add(temp);
            }
        }

        return result;
    }

    public int getSize() {
        return this.size;
    }

    public Rack getRack() {
        return this.rack;
    }

    public void setTile(int[] pos, ScrabbleSquare tile) {
        int row = pos[0];
        int col = pos[1];

        this.tiles[row][col] = tile;
        this.tiles[row][col].setPos(pos);
    }
    public ScrabbleSquare getTile(int[] pos) {
        int row = pos[0];
        int col = pos[1];

        return this.tiles[row][col];
    }

    public boolean inBounds(int[] pos) {
        int row = pos[0];
        int col = pos[1];

        return row>=0 && row < this.size && col >= 0 && col < this.size;
    }

    public boolean isEmpty(int[] pos) {
        return this.inBounds(pos) && this.getTile(pos).getType()!=ScrabbleSquare.SquareType.OCCUPIED;
    }

    public boolean isFilled(int[] pos) {
        boolean filled = this.inBounds(pos) && this.getTile(pos).getType()==ScrabbleSquare.SquareType.OCCUPIED;
        return filled;
    }

    public Board copy() throws Exception {
        Board result = new Board(this.size);
        for(int[] pos : this.getBoardCoords()) {
            result.setTile(pos, this.getTile(pos));
        }
        return result;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for(ScrabbleSquare[] row : this.tiles) {
            for(ScrabbleSquare square : row) {
                sb.append(square.toString());
            }
            sb.append("\n");
        }
        sb.append("Tray: ");
        for(Character c : this.rack.getRack()) {
            sb.append(c);
        }

        return sb.toString();
    }

    public int[] before(int[] pos) {
        int[] before;
        before = new int[]{pos[0], pos[1]-1};
        return before;
    }

    public int[] after(int[] pos) {
        int[] after;
        after = new int[]{pos[0], pos[1]+1};
        return after;
    }

    public int[] beforeCross(int[] pos) {
        int[] beforeCross;
        beforeCross = new int[] {pos[0]-1, pos[1]};
        return beforeCross;
    }

    public int[] afterCross(int[] pos) {
        int[] afterCross;
        afterCross = new int[] {pos[0]+1, pos[1]};
        return afterCross;
    }
}