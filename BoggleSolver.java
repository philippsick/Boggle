/* *****************************************************************************
 *  Name: Philipp Sick
 *  Date: September 9, 2023
 *  Description: A Boggle solver that finds all valid words in a given Boggle
 *  board, using a given dictionary.
 **************************************************************************** */

import edu.princeton.cs.algs4.Bag;
import edu.princeton.cs.algs4.Queue;

import java.util.ArrayList;

public class BoggleSolver {
    // Initializes the data structure using the given array of strings as the dictionary.
    // (You can assume each word in the dictionary contains only the uppercase letters A through Z.)

    private static final int R = 26; // Radix, 26 letters
    private static final int OFFSET = 65; // Offset for ASCII
    private final String[] dict;
    private final TrieST<Integer> dictST;
    private int row;
    private int col;


    private static class Node {
        private int val = 0;
        private Node[] next = new Node[R];
    }

    // Trie symbol table to store each word in dictionary
    private static class TrieST<Integer> {

        private Node root;      // root of trie
        private int n;          // number of keys in trie

        public TrieST() {
        }

        public int get(String key) {
            if (key == null) throw new IllegalArgumentException("argument to get() is null");
            Node x = get(root, key, 0);
            if (x == null) return -1;
            return x.val;
        }

        public boolean contains(String key) {
            if (key == null) throw new IllegalArgumentException("argument to contains() is null");
            return get(key) == 1;
        }

        private Node get(Node x, String key, int d) {
            if (x == null) return null;
            if (d == key.length()) return x;
            char c = key.charAt(d);
            return get(x.next[c], key, d + 1);
        }

        public void put(String key, int val) {
            root = put(root, key, val, 0);
        }

        private Node put(Node x, String key, int val, int d) {
            if (x == null) x = new Node();
            if (d == key.length()) {
                if (x.val == 0) n++;
                x.val = val;
                return x;
            }

            char c = (char) (key.charAt(d) - OFFSET);
            x.next[c] = put(x.next[c], key, val, d + 1);
            return x;
        }
    }

    public BoggleSolver(String[] dictionary) {
        dict = new String[dictionary.length];
        for (int i = 0; i < dictionary.length; i++) {
            dict[i] = dictionary[i];
        }
        dictST = new TrieST<>();
        for (String word : dict) {
            dictST.put(word, 1);
        }
    }

    // Returns the set of all valid words in the given Boggle board, as an Iterable.
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        row = board.rows();
        col = board.cols();

        // List of adjacent tiles for each tile
        ArrayList<ArrayList<Integer>> adj = new ArrayList<ArrayList<Integer>>(row * col);

        for (int i = 0; i < row * col; i++) {
            adj.add(new ArrayList<Integer>());
            if (row == 1 || col == 1) {
                if (i > 0) {
                    adj.get(i).add(i - 1);
                }
                if (i < row * col - 1) {
                    adj.get(i).add(i + 1);
                }
            }
            else {
                // Top
                if (i > col - 1) {
                    adj.get(i).add(i - col);
                }
                // Bottom
                if (i < row * col - col) {
                    adj.get(i).add(i + col);
                }

                if (i == 0 || i % col == 0) { // Right
                    adj.get(i).add(i + 1);
                    if (i > col - 1) {
                        adj.get(i).add(i - col + 1);
                    }
                    if (i < row * col - col) {
                        adj.get(i).add(i + col + 1);
                    }
                }
                else if ((i + 1) % col == 0) { // Left
                    adj.get(i).add(i - 1);
                    if (i > col - 1) { // Top Left
                        adj.get(i).add(i - col - 1);
                    }
                    if (i < row * col - col) {
                        adj.get(i).add(i + col - 1); // Bottom Left
                    }
                }
                else { // Middle
                    adj.get(i).add(i + 1);
                    adj.get(i).add(i - 1);
                    if (i > col - 1) {
                        adj.get(i).add(i - col - 1); // Top Left
                        adj.get(i).add(i - col + 1); // Top Right
                    }
                    if (i < row * col - col) {
                        adj.get(i).add(i + col - 1); // Bottom Left
                        adj.get(i).add(i + col + 1); // Bottom Right
                    }
                }
            }
        }

        boolean[] marked = new boolean[row * col];
        Queue<String> words = new Queue<>();
        boolean identical = true;
        for (int i = 0; i < row * col - 1; i++) {
            int y = i / col;
            int x = i - y * col;
            int y1 = y;
            int x1 = x + 1;
            if ((i + 1) % col == 0) {
                y1++;
                x1 = 0;
            }
            char c1 = board.getLetter(y, x);
            char c2 = board.getLetter(y1, x1);
            if (c2 != c1) {
                identical = false;
            }
        }

        Bag<Node> modifiedNodes = new Bag<>();

        if (identical) {
            StringBuilder word = new StringBuilder();
            for (int i = 0; i < row * col; i++) {
                char c = board.getLetter(0, 0);
                if (c == 'Q') { // Check if letter is q
                    word.append("QU");
                }
                else {
                    word.append(c);
                }
                if (word.length() > 2) {
                    words.enqueue(word.toString());
                }
            }
        }
        else {
            for (int i = 0; i < row * col; i++) {
                int y = i / col;
                int x = i - y * col;
                char c = (char) (board.getLetter(y, x) - OFFSET);
                StringBuilder word = new StringBuilder();
                word.append((char) (c + OFFSET));
                // Check if char is in symbol table
                if (dictST.root.next[c] != null) {
                    if ((char) (c + OFFSET) == 'Q') { // Special case for Q
                        dfs(marked, adj, i, dictST.root.next[c].next[(char) ('U' - OFFSET)], board,
                            words,
                            word.append("U"), modifiedNodes);
                    }
                    else {
                        dfs(marked, adj, i, dictST.root.next[c], board, words, word, modifiedNodes);
                    }
                }
            }
        }
        for (Node n : modifiedNodes) {
            n.val = 1;
        }
        return words;
    }

    // Depth first search
    private void dfs(boolean[] marked, ArrayList<ArrayList<Integer>> adj, int v, Node n,
                     BoggleBoard board, Queue<String> words, StringBuilder word,
                     Bag<Node> modifiedNodes) {
        marked[v] = true;

        if (n.val == 1 && word.length() > 2) {
            words.enqueue(word.toString());
            n.val = 2;
            modifiedNodes.add(n);
        }

        // Check each adjacent tile
        for (int w : adj.get(v)) {
            if (!marked[w]) {
                int y = w / col;
                int x = w - y * col;
                char c = board.getLetter(y, x);
                if (c == 'Q') {
                    c = (char) (c - OFFSET);
                    if (n.next[c] != null && n.next[c].next[(char) ('U' - OFFSET)] != null) {
                        dfs(marked, adj, w, n.next[c].next[(char) ('U' - OFFSET)], board, words,
                            word.append("QU"), modifiedNodes);
                        word.deleteCharAt(word.length() - 1);
                        word.deleteCharAt(word.length() - 1);
                    }
                }
                else {
                    c = (char) (c - OFFSET);
                    if (n.next[c] != null) {
                        dfs(marked, adj, w, n.next[c], board, words,
                            word.append((char) (c + OFFSET)), modifiedNodes);
                        word.deleteCharAt(word.length() - 1);
                    }
                }
            }
        }
        marked[v] = false;
    }


    // Returns the score of the given word if it is in the dictionary, zero otherwise.
    // (You can assume the word contains only the uppercase letters A through Z.)
    public int scoreOf(String word) {
        char[] result = word.toCharArray();
        for (int i = 0; i < result.length; i++) {
            result[i] -= OFFSET;
        }
        String destString = new String(result);
        if (dictST.contains(destString)) {
            int length = word.length();
            if (length < 3) {
                return 0;
            }
            else if (length < 5) {
                return 1;
            }
            else if (length == 5) {
                return 2;
            }
            else if (length == 6) {
                return 3;
            }
            else if (length == 7) {
                return 5;
            }
            else {
                return 11;
            }
        }
        else {
            return 0;
        }
    }
}
