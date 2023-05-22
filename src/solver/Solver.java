package solver;

import java.util.*;

/**
 * @author James Morris
 * Adapted from Guy Jacobson & Andrew Appel's "World's Fastest Scrabble Algorithm" (1988)
 */
public class Solver {

    public WordGraph dictionary;
    private Rack rack;
    private Rack origRack;
    private Board board;
    private HashMap<int[], ArrayList<Character>> crossChecks;
    public String direction;

    private int currHighScore = 0;
    private Board outputBoard;

    private String outputWord;

    /**
     * making a solver object
     * @param dictionary the WordGraph to use
     * @param board the board to solve
     * @param rack the rack to use
     */
    public Solver(WordGraph dictionary, Board board, Rack rack) {
        this.dictionary = dictionary;
        this.rack = rack;
        this.origRack = this.rack.copy();
        this.board = board;
        this.direction = null;
    }

    /**
     * getting coordinates of left / upward tile
     * @param pos tile to get left of
     * @return coords of before tile
     */
    public int[] before(int[] pos) {
        int row = pos[0];
        int col = pos[1];
        if (this.direction.equals("across")) {
            return new int[]{row, col - 1};
        } else {
            return new int[]{row - 1, col};
        }
    }

    /**
     * getting right / down tile
     * @param pos coords to go right of
     * @return coords of right tile
     */
    public int[] after(int[] pos) {
        int row = pos[0];
        int col = pos[1];
        if (this.direction.equals("across")) {
            return new int[]{row, col + 1};
        } else {
            return new int[]{row + 1, col};
        }
    }

    /**
     * getting left / up tile
     * @param pos coords to get before of
     * @return coords of upward tile
     */
    public int[] beforeCross(int[] pos) {
        int row = pos[0];
        int col = pos[1];
        if (this.direction.equals("across")) {
            return new int[]{row - 1, col};
        } else {
            return new int[]{row, col - 1};
        }
    }

    /**
     * getting up / down tile
     * @param pos coords to get before of
     * @return coords of before tile
     */
    public int[] afterCross(int[] pos) {
        int row = pos[0];
        int col = pos[1];
        if (this.direction.equals("across")) {
            return new int[]{row + 1, col};
        } else {
            return new int[]{row, col + 1};
        }
    }

    /**
     * whether the tile at these coords has a filled neighbor
     * @param pos the tile to be checked
     * @return true if tile has a filled neighbor
     */
    public boolean neighborFilled(int[] pos) {
        boolean neighborFilled = this.board.isFilled(this.before(pos)) ||
                this.board.isFilled(this.after(pos)) ||
                this.board.isFilled(this.beforeCross(pos)) ||
                this.board.isFilled(this.afterCross(pos));

        return neighborFilled;
    }

    /**
     * finding "anchors" of words (places where a tile can build off another tile)
     * @return all coords of potential anchors
     */
    public ArrayList<int[]> findWordAnchors() {
        ArrayList<int[]> anchors = new ArrayList<>();
        ArrayList<int[]> allPositions = this.board.getBoardCoords();
        for (int[] pos : allPositions) {
            boolean empty = this.board.isEmpty(pos);
            boolean neighborFilled = this.neighborFilled(pos);

            if (empty && neighborFilled) {
                anchors.add(pos);
            }
        }
        return anchors;
    }

    /**
     * if a move is legal, check its score
     * @param word the word we played
     * @param lastPos the final position we altered on the board
     */
    public void isLegal(String word, int[] lastPos) throws Exception {

        Board tempBoard = this.board.copy();
        Board prevBoard = tempBoard.copy();

        int[] playPos = lastPos;
        int wordIndex = word.length() - 1;

        int tilesPlayed = 0;


        while (wordIndex >= 0) {
            char c = word.charAt(wordIndex);

            ScrabbleSquare square = new ScrabbleSquare(playPos, c);
            if (!this.board.isFilled(playPos)) {
                tempBoard.setTile(playPos, square);
                tilesPlayed++;
            }

            wordIndex -= 1;
            playPos = this.before(playPos);
        }

        int playScore = scorePlay(prevBoard, tempBoard, this.after(playPos), tilesPlayed);

        if (playScore > this.currHighScore) {
            this.outputWord = word;
            this.currHighScore = playScore;
            this.outputBoard = tempBoard;
        }

    }

    /**
     * @return the highest scoring word
     */
    public String getOutputWord() {
        return this.outputWord;
    }

    /**
     *
     * @return the highest scoring board
     */
    public Board getOutputBoard() {
        return this.outputBoard;
    }

    /**
     *
     * @return the highest score
     */
    public int getCurrHighScore() {
        return this.currHighScore;
    }

    /**
     * scoring a move
     * @param prevBoard the board before the play
     * @param board the board after the play
     * @param firstPos the first position in the word
     * @param tilesPlayed the number of tiles we played
     * @return the score of the play
     */
    public int scorePlay(Board prevBoard, Board board, int[] firstPos, int tilesPlayed) {
        int[] pos = firstPos;
        int playScore = 0;

        int baseWord = 0;

        int currWordMulti = 1;

        while(board.isFilled(pos)) {

            char c = board.getTile(pos).getLetter();
            boolean blank = false;

            //checking to see if a tile played was a blank
            if(!this.origRack.contains(c) && !prevBoard.isFilled(pos)) {
                blank = true;
            }

            int letterVal;
            if(!blank) {
                letterVal = LetterValues.getLetterValue(c);
            }
            else {
                letterVal = 0;
            }
            int letterMulti = prevBoard.getTile(pos).getLetterMulti();
            int wordMulti = prevBoard.getTile(pos).getWordMulti();

            letterVal *= letterMulti;
            currWordMulti *= wordMulti;

            playScore += letterVal;
            baseWord += letterVal;

            int beforeWord = 0;
            int afterWord = 0;

            boolean isBefore = false;
            boolean isAfter = false;

            if(!prevBoard.isFilled(pos)) {
                int[] beforePos = this.beforeCross(pos);

                while(board.isFilled(beforePos)) {
                    isBefore = true;
                    ScrabbleSquare beforeSquare = board.getTile(beforePos);
                    int beforeLetterVal = beforeSquare.getLetterValue();

                    beforeWord += beforeLetterVal;
                    beforePos = this.beforeCross(beforePos);
                }

                int[] afterPos = this.afterCross(pos);

                while(board.isFilled(afterPos)) {
                    isAfter = true;
                    ScrabbleSquare afterSquare = board.getTile(afterPos);
                    int afterLetterVal = afterSquare.getLetterValue();

                    afterWord += afterLetterVal;
                    afterPos = this.afterCross(afterPos);
                }
            }
            int combinedWord = 0;

            if(isBefore && !isAfter) {
                beforeWord += letterVal;
                beforeWord *= wordMulti;
            }

            else if(!isBefore && isAfter) {
                afterWord += letterVal;
                afterWord *= wordMulti;
            }

            else if(isBefore && isAfter) {
                combinedWord = beforeWord + afterWord;
                combinedWord += letterVal;
                combinedWord *= wordMulti;
            }

            if (combinedWord==0) {
                playScore += beforeWord + afterWord;
            }
            else {
                playScore += combinedWord;
            }

            pos = this.after(pos);
        }

        playScore += (baseWord * (currWordMulti-1));

        if(tilesPlayed == 7) {
            playScore += 50;
        }

        return playScore;
    }

    /**
     * finding crosschecks (the characters we can play in a given coord)
     * @return map of valid chars for each position on the board
     */
    public HashMap<int[],ArrayList<Character>> findCrossChecks() {
        HashMap<int[],ArrayList<Character>> result = new HashMap<>();
        for(int[] pos : this.board.getBoardCoords()) {
            if (this.board.isFilled(pos)) {
                continue;
            }
            StringBuilder lettersBefore = new StringBuilder();
            StringBuilder lettersAfter = new StringBuilder();
            int[] scanPos = pos;
            while (this.board.isFilled(this.beforeCross(scanPos))) {
                scanPos = this.beforeCross(scanPos);
                lettersBefore.insert(0, this.board.getTile(scanPos).getLetter());
            }
            scanPos = pos;
            while (this.board.isFilled(this.afterCross(scanPos))) {
                scanPos = this.afterCross(scanPos);
                lettersAfter.append(this.board.getTile(scanPos).getLetter());
            }
            ArrayList<Character> legalHere = new ArrayList<>();
            String legalStr = "abcdefghijklmnopqrstuvwxyz";
            if (lettersBefore.length() == 0 && lettersAfter.length() == 0) {
                for (char c : legalStr.toCharArray()) {
                    legalHere.add(c);
                }                    
            } else {
                for (char c : legalStr.toCharArray()) {
                    StringBuilder lettersBeforeCopy = new StringBuilder(lettersBefore);
                    StringBuilder lettersAfterCopy = new StringBuilder(lettersAfter);
                    StringBuilder wordFormed = lettersBeforeCopy.append(c).append(lettersAfterCopy);

                    if (this.dictionary.isWord(wordFormed.toString())) {
                        legalHere.add(c);
                    }
                }
            }
            result.put(pos, legalHere);
        }
        return result;

    }

    /**
     * getting the suffix of a word
     * @param partialWord the word so far
     * @param currentNode the node we are checking in the dictionary
     * @param anchorPos the position we are scanning
     * @param limit making sure we don't go out of bounds
     */
    public void getWordPrefix(String partialWord, WordGraph.Node currentNode, int[] anchorPos, int limit) {
        this.extendRight(partialWord, currentNode, anchorPos, false);
        if(limit > 0) {
            for (Character nextLetter : currentNode.getChildren().keySet()) {
                boolean containsBlank = this.rack.containsBlank();
                if (this.rack.contains(nextLetter) || containsBlank) {
                    boolean addLetter = false;
                    if(this.rack.contains(nextLetter)) {
                        this.rack.remove(nextLetter);
                        addLetter=true;
                    }
                    else {
                        this.rack.removeBlank();
                    }
                    StringBuilder sb = new StringBuilder();
                    sb.append(partialWord).append(nextLetter);
                    this.getWordPrefix(
                            sb.toString(),
                            currentNode.getChildren().get(nextLetter),
                            anchorPos,
                            limit - 1
                    );
                    if(addLetter) {
                        this.rack.add(nextLetter);
                    }
                    else {
                        this.rack.addBlank();
                    }
                }
            }
        }
    }

    /**
     * getting the suffix of a word
     * @param partialWord the word we have so far
     * @param currentNode the current node in the dict
     * @param nextPos the next pos to extend to
     * @param anchorFilled checking if an anchor tile is filled
     */
    public void extendRight(String partialWord, WordGraph.Node currentNode, int[] nextPos, boolean anchorFilled) {
        if((this.board.isEmpty(nextPos) || !this.board.inBounds(nextPos)) && currentNode.isWord() && anchorFilled) {
            try {
                this.isLegal(partialWord, this.before(nextPos));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        HashMap<int[], ArrayList<Character>> crossChecks = new HashMap<>();
        for(int[] pos : this.crossChecks.keySet()) {
            if(pos[0] == nextPos[0] &&
                pos[1] == nextPos[1]) {
                    crossChecks.put(pos, this.crossChecks.get(pos));;
            }
        }
        if(this.board.inBounds(nextPos)) {
            if(this.board.isEmpty(nextPos)) {
                for (Character nextLetter : currentNode.getChildren().keySet()) {
                    boolean contains = false;
                    for(int[] pos : crossChecks.keySet()) {
                        if(pos[0] == nextPos[0] &&
                            pos[1] == nextPos[1]) {

                            if (crossChecks.get(pos).contains(nextLetter)) {
                                contains = true;
                                break;
                            }
                        }
                    }
                    boolean containsBlank = this.rack.containsBlank();
                    if ((this.rack.contains(nextLetter) || containsBlank) && contains) {
                        boolean addLetter = false;
                        if(this.rack.contains(nextLetter)) {
                            this.rack.remove(nextLetter);
                            addLetter = true;
                        }
                        else {
                            this.rack.removeBlank();
                        }
                        this.extendRight(
                                partialWord + nextLetter,
                                currentNode.getChildren().get(nextLetter),
                                this.after(nextPos),
                                true
                        );
                        if(addLetter) {
                            this.rack.add(nextLetter);
                        }
                        else {
                            this.rack.addBlank();
                        }
                    }
                }
            }
            else {
                char existingChar = this.board.getTile(nextPos).getLetter();
                if(currentNode.getChildren().containsKey(existingChar)) {
                    this.extendRight(
                            partialWord + existingChar,
                            currentNode.getChildren().get(existingChar),
                            this.after(nextPos),
                            true
                    );
                }
            }
        }
    }

    /**
     * getting all valid moves for the board
     */
    public void getMoves() {

        for (String direction : new String[]{"across", "down"}) {
            this.direction = direction;
            ArrayList<int[]> anchors = this.findWordAnchors();
            this.crossChecks = this.findCrossChecks();
            for (int[] anchorCoords : anchors) {
                if (this.board.isFilled(this.before(anchorCoords))) {
                    int[] scanCoords = this.before(anchorCoords);
                    StringBuilder partialWord = new StringBuilder(this.board.getTile(scanCoords).getLetter().toString());
                    while (this.board.isFilled(this.before(scanCoords))) {
                        scanCoords = this.before(scanCoords);
                        partialWord.insert(0,this.board.getTile(scanCoords).getLetter());
                    }

                    WordGraph.Node currNode = this.dictionary.lookupWord(partialWord.toString());
                    if (currNode != null) {
                        this.extendRight(
                                partialWord.toString(),
                                currNode,
                                anchorCoords,
                                false
                        );
                    }
                } else {
                    int limit = 0;
                    int[] scanCoords = anchorCoords;

                    boolean contains = false;
                    for(int [] pos : anchors) {
                        if(pos[0]==this.before(scanCoords)[0] &&
                            pos[1]==this.before(scanCoords)[1]) {
                            contains = true;
                            break;
                        }
                    }
                    while (this.board.isEmpty(this.before(scanCoords)) && !(contains)) {
                        limit = limit + 1;
                        scanCoords = this.before(scanCoords);
                        for(int [] coords : anchors) {
                            if(coords[0]==this.before(scanCoords)[0] &&
                                coords[1]==this.before(scanCoords)[1]) {
                                contains = true;
                                break;
                            }
                        }
                    }
                    this.getWordPrefix("", this.dictionary.getRoot(), anchorCoords, limit);
                }
            }
        }
    }
}
