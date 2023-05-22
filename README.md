# CS 351 Project 3: Scrabble

### Overview
Contains two programs: a solver and a Scrabble game.

### Solver
#### Usage
        java -jar ScrabbleSolver.jar <dictionary_file.txt>

### Scrabble Game
#### Usage
        java -jar ScrabbleGame.jar
#### Known Issues
Doesn't work very well. Game.java is bloated, probably should
be split into multiple classes. If a word is invalid the game
won't accept new words sometimes. Blanks are not implemented in
the JavaFX game (though they are in the solver). There is no way
to make a new game except quitting the program. Sometimes doesn't
recognize valid words.