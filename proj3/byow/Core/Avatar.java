package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

public class Avatar {

    public TETile FormerTile = Tileset.FLOOR;
    public TETile NowTile = Tileset.AVATAR;
    public boolean movement = false;
    public int xNow;
    public int yNow;
    public int style = 0;

    public Avatar (int x, int y, int style) {
        this.xNow = x;
        this.yNow = y;
        this.style = style;
        if (style == 1) {
            NowTile = Tileset.AVATARb;
        } else if (style == 2) {
            NowTile = Tileset.AVATARc;
        }
    }

    public Avatar (int x, int y) {
        this.xNow = x;
        this.yNow = y;
    }

}
