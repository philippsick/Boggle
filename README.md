My solution to the Boggle assignment from Princeton's Algorithms, Part II course. The assignment spec can be found here: https://coursera.cs.princeton.edu/algs4/assignments/boggle/specification.php. A brief summary is below. 

Boggle is a word game designed by Allan Turoff and distributed by Hasbro. It involves a board made up of 16 cubic dice, where each die has a letter printed on each of its 6 sides. At the beginning of the game, the 16 dice are shaken and randomly distributed into a 4-by-4 tray, with only the top sides of the dice visible. The players compete to accumulate points by building valid words from the dice, according to these rules:

- A valid word must be composed by following a sequence of adjacent dice—two dice are adjacent if they are horizontal, vertical, or diagonal neighbors.
- A valid word can use each die at most once.
- A valid word must contain at least 3 letters.
- A valid word must be in the dictionary (which typically does not contain proper nouns).

For the assignment, I wrote a Boggle solver that finds all valid words in a given Boggle board, using a given dictionary. I used a trie data structure to store the dictionary and depth-first search to find possible tile combinations. To optimize performance, I terminate the search if the current path (word) is not contained in the trie.
