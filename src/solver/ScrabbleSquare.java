package solver;

public class ScrabbleSquare {
    private int[] pos;
    private int wordMulti = 1;
    private char letter;
    private int letterMulti = 1;
    private boolean isBlank = false;

    public enum SquareType {
        OCCUPIED, QW, TW, QL, TL, DW, DL, NORMAL
    }
    private SquareType type = SquareType.NORMAL;


    public void setPos(int[] pos) {
        this.pos = pos;
    }

    public int getLetterValue() {
        if(this.isBlank) {
            return 0;
        }
        else {
            return LetterValues.getLetterValue(this.letter);
        }
    }

    public int[] getPos() {
        return pos;
    }

    public ScrabbleSquare(int[] pos) {
        this.pos = pos;
    }

    public void setIsBlank() {
        this.isBlank = true;
    }

    public void setLetter(Character c) {
        c = Character.toLowerCase(c);
        this.letter = c;
        this.type = SquareType.OCCUPIED;
    }

    public ScrabbleSquare(int[] pos, char c) {
        this.pos = pos;
        this.letter = c;
        this.setWordMulti(1);
        this.setLetterMulti(1);
        this.type = SquareType.OCCUPIED;
    }

    public void setWordMulti(int wordMulti) {
        this.wordMulti = wordMulti;
        if(wordMulti==4) {
            this.type = SquareType.QW;
        }
        else if(wordMulti==3) {
            this.type = SquareType.TW;
        }
        else if(wordMulti==2) {
            this.type = SquareType.DW;
        }
    }

    public void setLetterMulti(int letterMulti) {
        this.letterMulti = letterMulti;
        if(letterMulti==4) {
            this.type = SquareType.QL;
        }
        else if(letterMulti==3) {
            this.type = SquareType.TL;
        }
        else if(letterMulti==2) {
            this.type = SquareType.DL;
        }
    }

    public int getLetterMulti() {
        return this.letterMulti;
    }

    public SquareType getType() {
        return type;
    }

    public Character getLetter() {
        return letter;
    }

    public int getWordMulti() {
        return this.wordMulti;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if(this.type==SquareType.OCCUPIED) {
            sb.append(" ").append(this.letter);
        }
        else if(this.wordMulti!=1) {
            if(this.wordMulti==2) {
                sb.append("DW");
            }
            else if(this.wordMulti==3) {
                sb.append("TW");
            }
            else if(this.wordMulti==4) {
                sb.append("4W");
            }
        }

        else if(this.letterMulti!=1) {
            if(this.letterMulti==2) {
                sb.append("DL");
            }
            else if(this.letterMulti==3) {
                sb.append("TL");
            }
            else if(this.letterMulti==4) {
                sb.append("4L");
            }
        }

        else if(this.type==SquareType.NORMAL) {
            sb.append("__");
        }

        return sb.toString();
    }
}