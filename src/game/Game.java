package game;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import solver.*;
import solver.ScrabbleSquare.SquareType;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * @author James Morris
 * jmorris7@unm.edu
 */

/**
 * Game object used to create a JavaFX implementation of Scrabble.
 */
public class Game extends Application {

    private File dictFile = new File("resources/dictionary.txt");

    private double tileSize;
    private ArrayList<Character> playedChars = new ArrayList<>();
    private Rectangle highlighted = new Rectangle();
    private StackPane highlightedStack = new StackPane();
    //private ArrayList<StackPane> lastPlaced = new ArrayList<>();

    private HashMap<int[],StackPane> lastPlaced = new HashMap<>();
    private StackPane[][] squares;
    private ScrabbleSquare[][] scrabbleSquares;
    private GridPane rack;
    private int[] prevPos;
    private Rectangle bg;
    private WordGraph dict;
    private Label thinking;
    private Rack aiRack = new Rack();
    private Rack playerRack = new Rack();
    private Rack prevRack = new Rack();

    private LetterBag bag;

    private int playerScore = 0;
    private int compScore = 0;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root);

        this.bag = new LetterBag();

        Board b = null;
        try {
            b = getBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
        scene.setRoot(drawGame(scene, b));

        primaryStage.setScene(scene);
        primaryStage.show();
    }


    /**
     * Draws the basic game with board and controls.
     * @param scene scene to update
     * @param board the previous board
     * @return pane with updated board & score
     */
    private Pane drawGame(Scene scene, Board board) {

        this.lastPlaced.clear();



        try {
            this.dict = WordGraph.readFile(dictFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //making basic background stuff
        Pane game = new Pane();

        game.setMinWidth(1200);
        game.setMinHeight(800);

        this.bg = new Rectangle(5000, 5000, Color.web("#796343"));

        Rectangle boardRect = new Rectangle(652.5, 652.5);
        boardRect.setStyle("-fx-fill: white; -fx-stroke: black; -fx-stroke-width: 2.5;");
        boardRect.layoutXProperty().bind(game.widthProperty().divide(2).subtract(500));
        boardRect.layoutYProperty().bind(game.heightProperty().divide(2).subtract(350));

        game.getChildren().addAll(this.bg, boardRect);

        //gridpane to hold scrabble squares
        GridPane boardSquares = new GridPane();

        boardSquares.layoutXProperty().bind(boardRect.layoutXProperty().add(2.5));
        boardSquares.layoutYProperty().bind(boardRect.layoutYProperty().add(2.5));
        boardSquares.setHgap(2.5);
        boardSquares.setVgap(2.5);

        boardRect.widthProperty().bind(boardSquares.widthProperty().add(5));
        boardRect.heightProperty().bind(boardSquares.heightProperty().add(5));

        //the board we are drawing
        Board b = board;

        this.squares = new StackPane[15][15];
        this.scrabbleSquares = new ScrabbleSquare[15][15];

        //going through each tile and updating the gridpane
        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                double size = 650.0 / 15;
                this.tileSize = size;
                Rectangle rect = new Rectangle(size, size);

                int[] pos = new int[]{i, j};

                ScrabbleSquare square = b.getTile(pos);
                this.scrabbleSquares[i][j] = square;

                StackPane stack = new StackPane();

                switch (square.getType()) {
                    case NORMAL -> {
                        rect.setStyle("-fx-fill: #b69c7b;");
                        stack.getChildren().addAll(rect);
                    }
                    case OCCUPIED -> {
                        rect.setStyle("-fx-fill: #d3a479; -fx-stroke: black; -fx-stroke-width: 1;");
                        Text ch = new Text();
                        Character c = square.getLetter();
                        String toUpper = c.toString().toUpperCase();
                        ch.setText(toUpper);
                        ch.setTextAlignment(TextAlignment.CENTER);
                        ch.setFill(Color.BLACK);
                        ch.setFont(Font.font("Helvetica Neue"));
                        ch.setStyle("-fx-font-size: 25px;");
                        int letterVal = LetterValues.getLetterValue(square.getLetter());
                        Text num = new Text();
                        String numStr = Integer.toString(letterVal);
                        num.setText(numStr);
                        num.setStyle("-fx-font-size: 10px; -fx-font-family: Helvetica Neue");
                        if (letterVal == 10) {
                            num.setTranslateX((size / 2) - 7.5);
                        } else {
                            num.setTranslateX((size / 2) - 5);
                        }
                        num.setTranslateY((size / 2) - 5);
                        stack.getChildren().addAll(rect, ch, num);
                    }
                    case TW -> {
                        rect.setStyle("-fx-fill: #d83b2d;");
                        Label tripWordTop = new Label("TRIPLE");
                        Label tripWordMid = new Label("WORD");
                        Label tripWordBot = new Label("SCORE");
                        tripWordTop.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        tripWordMid.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        tripWordBot.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        tripWordTop.setTranslateY(-10);
                        tripWordBot.setTranslateY(10);
                        stack.getChildren().addAll(rect, tripWordTop, tripWordMid, tripWordBot);
                    }
                    case DL -> {
                        rect.setStyle("-fx-fill: #aec5d3;");
                        Label doubLetTop = new Label("DOUBLE");
                        Label doubLetMid = new Label("LETTER");
                        Label doubLetBot = new Label("SCORE");
                        doubLetTop.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        doubLetMid.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        doubLetBot.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        doubLetTop.setTranslateY(-10);
                        doubLetBot.setTranslateY(10);
                        stack.getChildren().addAll(rect, doubLetTop, doubLetMid, doubLetBot);
                    }
                    case DW -> {
                        rect.setStyle("-fx-fill: #fabaaa");
                        if (!(pos[0] == 7 && pos[1] == 7)) {
                            Label doubWordTop = new Label("DOUBLE");
                            Label doubWordMid = new Label("WORD");
                            Label doubWordBot = new Label("SCORE");
                            doubWordTop.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                            doubWordMid.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                            doubWordBot.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                            doubWordTop.setTranslateY(-10);
                            doubWordBot.setTranslateY(10);
                            stack.getChildren().addAll(rect, doubWordTop, doubWordMid, doubWordBot);
                        } else {
                            FileInputStream inputStream = null;
                            try {
                                inputStream = new FileInputStream("resources/blackStar.png");
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }

                            Image image = new Image(inputStream);
                            ImageView imageView = new ImageView(image);

                            imageView.setFitHeight(size);
                            imageView.setFitWidth(size);
                            imageView.setPreserveRatio(true);

                            stack.getChildren().addAll(rect, imageView);
                        }
                    }
                    case TL -> {
                        rect.setStyle("-fx-fill: #409cb2");
                        Label tripLetTop = new Label("TRIPLE");
                        Label tripLetMid = new Label("LETTER");
                        Label tripLetBot = new Label("SCORE");
                        tripLetTop.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        tripLetMid.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        tripLetBot.setStyle("-fx-font-weight: bold; -fx-font-size: 8.5px; -fx-fill: black; font-family: Helvetica Neue");
                        tripLetTop.setTranslateY(-10);
                        tripLetBot.setTranslateY(10);
                        stack.getChildren().addAll(rect, tripLetTop, tripLetMid, tripLetBot);
                    }
                }

                this.squares[i][j] = stack;

                boardSquares.getChildren().addAll(stack);
                GridPane.setConstraints(stack, j, i);
            }
        }

        b = new Board(this.scrabbleSquares);

        //making rectangle to display rack tiles
        Rectangle rackHolder = new Rectangle();
        rackHolder.setFill(Color.web("#ccab99"));

        rackHolder.setHeight(60);

        rackHolder.layoutXProperty().bind(boardRect.layoutXProperty().add(652.5).add(100));
        rackHolder.layoutYProperty().bind(boardRect.layoutYProperty().add(650).subtract(60));

        //making gridpane for rack
        this.rack = new GridPane();
        rack.layoutXProperty().bind(rackHolder.layoutXProperty().add(2.5));
        rack.layoutYProperty().bind(rackHolder.layoutYProperty().add(2.5));
        rack.setHgap(2.5);


        Collections.shuffle(this.bag.getBag());

        int bagIndex = 0;

        //adding tiles to rack from bag of letters

        while(this.playerRack.getRack().size() < 7) {
            Character c = bag.getBag().get(bagIndex);
            b.getRack().add(c);
            this.playerRack.add(c);
            bagIndex++;
        }

        //removing the letters we just added from the bag
        bag.getBag().subList(0, bagIndex+1).clear();

        Collections.shuffle(bag.getBag());

        //doing the same for the AI
        bagIndex = 0;
        while(this.aiRack.getRack().size()<7) {
            this.aiRack.getRack().add(bag.getBag().get(bagIndex));
            bagIndex++;
        }
        bag.getBag().subList(0,bagIndex+1).clear();

        int index = 0;

        //making rack tiles

        for (Character c : this.playerRack.getRack()) {

            StackPane stack = new StackPane();

            Rectangle rect = new Rectangle();
            rect.setHeight(tileSize);
            rect.setWidth(tileSize);
            rect.setStyle("-fx-fill: #d3a479; -fx-stroke: black; -fx-stroke-width: 1;");
            Text ch = new Text();
            String toUpper = c.toString().toUpperCase();
            ch.setText(toUpper);
            ch.setTextAlignment(TextAlignment.CENTER);
            ch.setFill(Color.BLACK);

            ch.setFont(Font.font("Helvetica Neue"));
            ch.setStyle("-fx-font-size: 25px;");

            int letterVal = LetterValues.getLetterValue(c);

            Text num = new Text();
            String numStr = Integer.toString(letterVal);
            num.setText(numStr);
            num.setStyle("-fx-font-size: 10px; -fx-font-family: Helvetica Neue");

            if (letterVal == 10) {
                num.setTranslateX((tileSize / 2) - 7.5);
            } else {
                num.setTranslateX((tileSize / 2) - 5);
            }
            num.setTranslateY((tileSize / 2) - 5);
            stack.getChildren().addAll(rect, ch, num);

            stack.setOnMouseClicked(event -> {
                Rectangle prevHighlighted = this.highlighted;
                this.highlighted = rect;
                this.highlightedStack = stack;
                updateHighlight(prevHighlighted);
                checkClick(squares, boardSquares, this.rack, stack, false, c);
            });

            this.prevRack.add(c);

            this.rack.getChildren().add(stack);

            GridPane.setConstraints(stack, index, 0);
            index++;
        }

        rackHolder.setWidth(tileSize * 7 + (2.5 * 9) + 7);

        //making controls / info for the game
        VBox controls = new VBox();
        controls.setLayoutX(1000);
        controls.setLayoutY(450);
        controls.setSpacing(5);

        this.thinking = new Label();
        this.thinking.setStyle("-fx-font-family: Helvetica Neue; -fx-font-size: 16px; -fx-text-fill: white;");
        this.updateTurnLabel("player");
        Button play = new Button("Play Word");
        play.setStyle("-fx-font-family: Helvetica Neue; -fx-font-size: 14px; -fx-stroke: black; -fx-stroke-width: 2px");
        Button clearBoard = new Button("Clear Board");
        clearBoard.setStyle("-fx-font-family: Helvetica Neue; -fx-font-size: 14px; -fx-stroke: black; -fx-stroke-width: 2px");

        HBox playBox = new HBox();
        playBox.setSpacing(10);

        Label validWord = new Label();
        playBox.getChildren().addAll(play, validWord);

        play.setOnMouseClicked(event -> {
            Board newBoard = new Board(this.scrabbleSquares);
            boolean isWord = checkWord(board, newBoard);
            if(isWord) {
                this.updateTurnLabel("comp");
                validWord.setText("");

                getCompMove(scene, newBoard);
            }
            else {
                validWord.setText("Not a valid word.");
            }
        });

        Label humanScore = new Label("Player Score : " + this.playerScore);
        Label compScore = new Label("Computer Score : " + this.compScore);
        humanScore.setStyle("-fx-font-size: 20px;");
        humanScore.setTextFill(Color.WHITE);
        humanScore.setFont(Font.font("Helvetica Neue"));
        compScore.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

        controls.getChildren().addAll(humanScore, compScore, thinking, playBox);

        game.getChildren().addAll(boardSquares, rackHolder, this.rack, controls);

        return game;
    }

    private Board getBoard() throws Exception {

        return new Board();
    }

    /**
     * adds a tile to the board
     * @param pos the pos in the board to update
     * @param squares the gridpane to update
     * @param stack the stackpane to add to the grid
     * @param c the character in the stackpane
     * @param newSquare the scrabblesquare to place in this.scrabblesquares
     */
    private void addToBoard(int[] pos, GridPane squares, StackPane stack, char c, ScrabbleSquare newSquare) {
        this.lastPlaced.put(pos, stack);
        this.playedChars.add(c);
        this.scrabbleSquares[pos[0]][pos[1]] = newSquare;
        squares.getChildren().add(stack);
        stack.setOnMouseClicked(event -> {
            this.highlightedStack = stack;
            Rectangle previousRect = this.highlighted;
            this.highlighted = (Rectangle) stack.getChildren().get(0);
            updateHighlight(previousRect);
            this.prevPos = pos;
            checkClick(this.squares, squares, this.rack, stack, true, c);
        });
        GridPane.setConstraints(stack, pos[1], pos[0]);
    }

    /**
     * removing a tile from the board
     * @param pos the position of the tile to remove
     * @param squares the gridpane to update
     * @param stack the stackpane we are removing
     */
    private void removeFromBoard(int[] pos, GridPane squares, StackPane stack) {
        for (int i = 0; i < 15; i++) {
            for (int j = 1; j < 15; j++) {
                if (pos[0] == i && pos[1] == j) {
                    squares.getChildren().remove(stack);
                    this.scrabbleSquares[i][j] = new ScrabbleSquare(pos);
                }
            }
        }
    }

    /**
     * adds a tile to the rack
     * @param stack the StackPane to add to the rack
     */
    private void addToRack(StackPane stack) {
        this.rack.getChildren().add(stack);
        int size = this.rack.getChildren().size();

        for (int i = 0; i < size; i++) {
            Node child = this.rack.getChildren().get(i);
            GridPane.setConstraints(child, i, 0);
        }

    }

    /**
     * remove a tile from the rack
     * @param stack the StackPane to remove
     * @param c the character to remove
     */
    private void removeFromRack(StackPane stack, Character c) {
        this.playerRack.remove(c);
        this.rack.getChildren().remove(stack);
    }

    /**
     * updates the highlighted StackPane
     * @param previous the previously highlighted stackpane
     */
    private void updateHighlight(Rectangle previous) {
        previous.setFill(Color.web("#d3a479"));
        if (!previous.equals(this.highlighted)) {
            this.highlighted.setFill(Color.WHITE);
        } else {
            this.highlightedStack = new StackPane();
            this.highlighted = new Rectangle();
        }
    }

    /**
     * checks a click on the board or to remove a character
     * @param squares the array of stackpanes representing the board
     * @param board the gridpane representing the board
     * @param rack the rack
     * @param clicked the tile that was clicked
     * @param remove whether we are removing or adding to / from board
     * @param c the character to remove or add
     */
    private void checkClick(StackPane[][] squares, GridPane board, GridPane rack, StackPane clicked, boolean remove, Character c) {
        int rowIndex = 0;
        int colIndex = 0;

        for (StackPane[] row : squares) {
            for (StackPane stack : row) {
                ScrabbleSquare square = this.scrabbleSquares[rowIndex][colIndex];
                int[] pos = new int[]{rowIndex, colIndex};
                int finalRowIndex = rowIndex;
                int finalColIndex = colIndex;
                stack.setOnMouseClicked(event -> {
                    if (remove) {
                        this.removeFromBoard(this.prevPos, board, this.highlightedStack);
                    }
                    if (square.getType() != ScrabbleSquare.SquareType.OCCUPIED) {
                        this.addToBoard(pos, board, clicked, c, square);
                        this.scrabbleSquares[finalRowIndex][finalColIndex] = new ScrabbleSquare(pos, c);
                        this.removeFromRack(clicked, c);
                        this.clearHighlighted();
                    }
                });
                colIndex++;
            }
            colIndex = 0;
            rowIndex++;
        }

        bg.setOnMouseClicked(event -> {
            this.removeFromBoard(this.prevPos, board, this.highlightedStack);
            this.addToRack(clicked);
        });

    }

    /**
     * clears the previously highlighted tile
     */
    private void clearHighlighted() {
        this.highlighted = new Rectangle();
        this.highlightedStack = new StackPane();
    }

    private boolean checkWord(Board prev, Board curr) {

        int[] root = null;

        boolean acrossWord = false;
        boolean downWord = false;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 15; i++) {
            for (int j = 0; j < 15; j++) {
                int[] pos = new int[]{i, j};
                ScrabbleSquare prevTile = prev.getTile(pos);
                ScrabbleSquare currTile = curr.getTile(pos);
                if (prevTile.getType() != currTile.getType()) {
                    root = pos;
                    break;
                }
            }
        }

        ScrabbleSquare bfCheck = null;

        if(curr.inBounds(curr.before(root))) {
            bfCheck = curr.getTile(curr.before(root));
        }
        ScrabbleSquare bfCrossCheck = null;

        if(curr.inBounds(curr.beforeCross(root))) {
            bfCrossCheck = curr.getTile(curr.beforeCross(root));
        }

        if (bfCheck.getType() == ScrabbleSquare.SquareType.OCCUPIED) {
            while (bfCheck.getType() == ScrabbleSquare.SquareType.OCCUPIED) {
                root = bfCheck.getPos();
                if(curr.inBounds(curr.before(root))) {
                    bfCheck = curr.getTile(curr.before(root));
                }
                else {
                    break;
                }
            }
        } else if (bfCrossCheck.getType() == ScrabbleSquare.SquareType.OCCUPIED) {
            while (bfCrossCheck.getType() == ScrabbleSquare.SquareType.OCCUPIED) {
                root = bfCrossCheck.getPos();
                if(curr.inBounds(curr.beforeCross(root))) {
                    bfCrossCheck = curr.getTile(curr.beforeCross(root));
                }
                else {
                    break;
                }
            }

        }

        sb.append(curr.getTile(root).getLetter());


        if(curr.inBounds(curr.after(root))) {
            if (curr.getTile(curr.after(root)).getType()==ScrabbleSquare.SquareType.OCCUPIED) {
                acrossWord = true;
            } else if(curr.getTile(curr.afterCross(root)).getType()== ScrabbleSquare.SquareType.OCCUPIED) {
                downWord = true;
            }
        }

        if (acrossWord) {
            ScrabbleSquare afterTile = curr.getTile(curr.after(root));
            while (afterTile.getType() == ScrabbleSquare.SquareType.OCCUPIED) {
                sb.append(afterTile.getLetter());
                if(curr.inBounds(curr.after(afterTile.getPos()))) {
                    afterTile = curr.getTile(curr.after(afterTile.getPos()));
                }
                else {
                    break;
                }
            }
        }
        else if (downWord) {
            ScrabbleSquare afterTile = curr.getTile(curr.afterCross(root));
            while (afterTile.getType() == ScrabbleSquare.SquareType.OCCUPIED) {
                sb.append(afterTile.getLetter());
                if(curr.inBounds(curr.afterCross(afterTile.getPos()))) {
                    afterTile = curr.getTile(curr.afterCross(afterTile.getPos()));
                }
                else {
                    break;
                }
            }
        }
        else if (acrossWord && downWord) {

        }

        boolean isWord = this.dict.isWord(sb.toString());

        if(isWord) {

            int[] pos = root;
            int playScore = 0;

            int baseWord = 0;

            int currWordMulti = 1;

            while(curr.isFilled(pos)) {

                char c = curr.getTile(pos).getLetter();
                boolean blank = !this.playerRack.contains(c) && !prev.isFilled(pos);

                int letterVal;
                if(!blank) {
                    letterVal = LetterValues.getLetterValue(c);
                }
                else {
                    letterVal = 0;
                }

                letterVal = LetterValues.getLetterValue(c);


                int letterMulti = prev.getTile(pos).getLetterMulti();
                int wordMulti = prev.getTile(pos).getWordMulti();

                letterVal *= letterMulti;
                currWordMulti *= wordMulti;

                playScore += letterVal;
                baseWord += letterVal;

                int beforeWord = 0;
                int afterWord = 0;

                boolean isBefore = false;
                boolean isAfter = false;

                if(!prev.isFilled(pos)) {
                    int[] beforePos = null;
                    if(downWord) {
                        beforePos = curr.before(pos);
                    }
                    else if (acrossWord) {
                        beforePos = curr.beforeCross(pos);
                    }

                    while(curr.isFilled(beforePos)) {
                        isBefore = true;
                        char beforeChar = curr.getTile(beforePos).getLetter();
                        int beforeLetterVal = LetterValues.getLetterValue(beforeChar);

                        beforeWord += beforeLetterVal;
                        beforePos = curr.beforeCross(beforePos);
                    }

                    int[] afterPos = null;
                    if(acrossWord) {
                        afterPos = curr.afterCross(pos);
                    }
                    else if(downWord) {
                        afterPos = curr.after(pos);
                    }

                    while(curr.isFilled(afterPos)) {
                        isAfter = true;
                        char afterChar = curr.getTile(afterPos).getLetter();
                        int afterLetterVal = LetterValues.getLetterValue(afterChar);

                        afterWord += afterLetterVal;
                        afterPos = curr.afterCross(afterPos);
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

                if(acrossWord) {
                    pos = curr.after(pos);
                }
                else {
                    pos = curr.afterCross(pos);
                }
            }

            playScore += (baseWord * (currWordMulti-1));

            int tilesPlayed = 7-this.playerRack.size();
            if(tilesPlayed == 7) {
                playScore += 50;
            }

            this.playerScore += playScore;
        }

        return isWord;
    }

    private void getCompMove(Scene scene, Board board) {

        Solver solver = new Solver(this.dict, board, this.aiRack);
        solver.getMoves();
        
        Board compMove = solver.getOutputBoard();
        int compScore = solver.getCurrHighScore();
        String word = solver.getOutputWord();

        for(Character c : word.toCharArray()) {
            this.aiRack.remove(c);
        }

        this.compScore += compScore;

        scene.setRoot(this.drawGame(scene, compMove));

    }

    private void updateTurnLabel(String turn) {
        if(turn.equals("comp")) {
            this.thinking.setText("Computer is thinking...");
        }
        else if(turn.equals("player")) {
            this.thinking.setText("Your turn");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}