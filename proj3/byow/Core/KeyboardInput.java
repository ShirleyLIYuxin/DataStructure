package byow.Core;

import edu.princeton.cs.algs4.StdDraw;

public class KeyboardInput implements InputSource {
    private static final boolean PRINT_TYPED_KEYS = false;

    public KeyboardInput() {
        StdDraw.text(0.3, 0.9, "CS61BL: Shirley's World");
        StdDraw.text(0.3, 0.8, "New Game(N/n)");
        StdDraw.text(0.3, 0.7, "Load Game(L/l)");
        StdDraw.text(0.3, 0.6, "Quit Game(Q/q)");
        StdDraw.text(0.3, 0.5, "Language Changing(T/t)");
        StdDraw.text(0.3, 0.4, "AVATAR's appearance choosing");
        StdDraw.text(0.3, 0.3, "Default '@'; (B/b) for '&'; (C/c) for '^';");
        StdDraw.text(0.3, 0.2, "In-map Save and Quit(:Q/:q);Replay(R/r)");
        StdDraw.text(0.3, 0.1, "Please follow the instruction above");
    }

    public char getNextKey() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = Character.toUpperCase(StdDraw.nextKeyTyped());
                if (PRINT_TYPED_KEYS) {
                    System.out.print(c);
                }
                return c;
            }
        }
    }

    public boolean possibleNextInput() {
        return true;
    }

}
