# CS 351 Project 3: Scrabble

### Overview
Completed for CS 351 at the University of New Mexico (Fall 2021)

Contains two programs: a solver and a Scrabble game.

The solver is based on Appel and Jacobson's algorithm detailed in [The World's Fastest Scrabble Program](https://www.cs.cmu.edu/afs/cs/academic/class/15451-s06/www/lectures/scrabble.pdf) (1988).

Computer always plays the best move available.

Sample dictionary file is included in resources/dictionary.txt. Format of the dictionary file is just a txt list of all valid words.

The file resources/standardBoard.txt defines the board the ScrabbleGame.jar uses. The format is the a number `n` representing size of the board on the first line (eg 15 means a 15x15 board) and then the following n lines define the tiles. The format of each tile is a string of two characters. The first character is the word multiplier of that tile and the second character is the letter multiplier. e.g. `3.` represents a triple word tile and `.2` represents a double letter tile.

This can be changed if you wish to recompile the game.

ScrabbleSolver.jar can be passed a dictionary file and it will run the tests in the file test_sowpods.txt which defines several board states and the current letter rack. The tests can also be modified if you wish to recompile.

### Requirements
Java + JavaFX

### Solver
#### Usage
        java -jar ScrabbleSolver.jar <path/to/dictionary_file.txt>

### Scrabble Game
#### Usage
        java -jar ScrabbleGame.jar
#### Known Issues
Scoreboard clips onto the screen