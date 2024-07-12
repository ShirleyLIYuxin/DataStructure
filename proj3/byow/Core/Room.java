

package byow.Core;

import byow.TileEngine.TETile;
import byow.TileEngine.TERenderer;
import byow.TileEngine.Tileset;

public class Room {

    public int LX;
    public int RX;
    public int UY;
    public int DY;
    public boolean decision;

    public Room(int LX, int RX, int UY, int DY, boolean decision){
        this.LX = LX;
        this.RX = RX;
        this.UY = UY;
        this.DY = DY;
        this.decision = decision;
    }

    public int getsize_x(){
        return RX - LX;
    }
    public int getsize_y(){
        return UY - DY;
    }
    public int getLocation_x(){ return (LX+RX)/2; }
    public int getLocation_y(){ return (UY+DY)/2; }


}
