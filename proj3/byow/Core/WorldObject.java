package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
import byow.TileEngine.Tileset;

import java.util.Random;

public class WorldObject {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public int WIDTH = 90;
    public int HEIGHT = 60;
    public TETile[][] world = new TETile[WIDTH][HEIGHT];
    public int roomNum;
    public Room[] rooms;
    private Random rand;
    public Avatar you;

    public WorldObject (Random seed,int style){
        //System.out.println("0.1");
        initializeTile();
        this.rand = seed;
        this.roomNum = RandomUtils.uniform(this.rand,8,15);
        this.rooms = new Room[this.roomNum];
        this.you = new Avatar(0,0,style);
    }

    public WorldObject (Random seed){
        //System.out.println("0.1");
        initializeTile();
        this.rand = seed;
        this.roomNum = RandomUtils.uniform(this.rand,8,15);
        this.rooms = new Room[this.roomNum];
        this.you = new Avatar(0,0);
    }

    public void initializeTile(){
        for (int x = 0; x < WIDTH; x += 1){
            for (int y = 0; y < HEIGHT; y += 1){
                this.world[x][y] = Tileset.NOTHING;
            }
        }
    }

    public void createMap(){
        this.rooms = randRooms();
        decidingRooms();
        if (this.roomNum <= 3){
            createMap();
        }else{
            locateRooms();
            locateHallways();
            locateTree();
            locateAvatar();
        }
    }
    //Room related
    public Room[] randRooms(){
        for (int i = 0; i < this.roomNum; i += 1){
            rooms[i] = new Room(1,1,1,1,true);
            rooms[i].LX = RandomUtils.uniform(this.rand,1,this.WIDTH-11);
            rooms[i].RX = RandomUtils.uniform(this.rand,rooms[i].LX+4,rooms[i].LX+10);
            rooms[i].DY = RandomUtils.uniform(this.rand,1,this.HEIGHT-11);
            rooms[i].UY = RandomUtils.uniform(this.rand,rooms[i].DY+4,rooms[i].DY+10);
        }
        return rooms;
    }
    public void decidingRooms(){
        for (int i = 1; i < this.roomNum; i += 1){
            for (int j = 0; j < i; j += 1){
                if (this.rooms[j].decision){
                    if ((this.rooms[j].LX < this.rooms[i].RX && this.rooms[j].RX > this.rooms[i].LX) &&
                            (this.rooms[j].DY < this.rooms[i].UY && this.rooms[j].UY > this.rooms[i].DY)){
                        this.rooms[i].decision = false;
                    }
                }
            }
            if (!this.rooms[i].decision){
                this.roomNum -= 1;
            }
        }
    }
    public void locateRooms(){
        for (int i = 0; i < this.roomNum; i += 1){
            if (this.rooms[i].decision){
                locateRoom(this.rooms[i]);
            }
        }
    }
    public void locateRoom(Room room){
        //Room wall
        //Room corner
        this.world[room.LX][room.DY] = Tileset.WALL;
        this.world[room.LX][room.UY] = Tileset.WALL;
        this.world[room.RX][room.DY] = Tileset.WALL;
        this.world[room.RX][room.UY] = Tileset.WALL;
        //Room side
        for(int x = room.LX; x <= room.RX; x += 1) {
            this.world[x][room.DY] = Tileset.WALL;
            this.world[x][room.UY] = Tileset.WALL;
        }
        for(int y = room.DY; y <= room.UY; y += 1 ) {
            this.world[room.LX][y] = Tileset.WALL;
            this.world[room.RX][y] = Tileset.WALL;
        }
        //Room floor
        for(int x = room.LX+1; x < room.RX; x += 1) {
            for(int y = room.DY+1; y < room.UY; y += 1 ) {
                this.world[x][y] = Tileset.FLOOR;
            }
        }
    }

    //Hallway related
    public void locateHallways(){
        for (int i = 1; i < roomNum; i += 1) {
            if (this.rooms[i].decision) {
                for (int j = i - 1; j >= 0; j -= 1) {
                    if (this.rooms[j].decision){
                        if (compareLocation(this.rooms[i], this.rooms[j])) {
                            compareX(i, j);
                            break;
                        }
                    }
                }
                for (int j = i - 1; j >= 0; j -= 1) {
                    if (this.rooms[j].decision){
                            compareX(i, j);
                            break;
                    }
                }
            }
        }
    }

    public boolean compareLocation(Room room1, Room room2){
        if(room1.getLocation_x() != room2.getLocation_x() &&
                room1.getLocation_y() != room2.getLocation_y()){
            return true;
        }else{
            return false;
        }
    }

    public void compareX(int i,int j){
        if (this.rooms[i].getLocation_x() < this.rooms[j].getLocation_x()) {
            Bridging(this.rooms[i], this.rooms[j]);
        } else if (this.rooms[i].getLocation_x() > this.rooms[j].getLocation_x()) {
            Bridging(this.rooms[j], this.rooms[i]);
        }
    }

    public void Bridging(Room room1, Room room2){
        int x1 = room1.getLocation_x();
        int y1 = room1.getLocation_y();
        int x2 = room2.getLocation_x();
        int y2 = room2.getLocation_y();

        if(this.world[x1+1][y2+1] == Tileset.NOTHING){
            this.world[x1+1][y2+1] = Tileset.WALL;
        }
        if(this.world[x1+1][y2-1] == Tileset.NOTHING){
            this.world[x1+1][y2-1] = Tileset.WALL;
        }
        if(this.world[x1-1][y2-1] == Tileset.NOTHING){
            this.world[x1-1][y2-1] = Tileset.WALL;
        }
        if(this.world[x1-1][y2+1] == Tileset.NOTHING){
            this.world[x1-1][y2+1] = Tileset.WALL;
        }

        if(y1 < y2){

            for(int i = y1; i <= y2; i += 1){
                this.world[x1][i] = Tileset.FLOOR;
                if(this.world[x1-1][i] == Tileset.NOTHING){
                    this.world[x1-1][i] = Tileset.WALL;
                }
                if(this.world[x1+1][i] == Tileset.NOTHING){
                    this.world[x1+1][i] = Tileset.WALL;
                }
            }

        }else{
/*
            if(this.world[x1-1][y2-1] == Tileset.NOTHING){
                this.world[x1-1][y2-1] = Tileset.WALL;
            }

 */

            for(int i = y2; i <= y1; i += 1){
                this.world[x1][i] = Tileset.FLOOR;
                this.world[x1][i] = Tileset.FLOOR;
                if(this.world[x1-1][i] == Tileset.NOTHING){
                    this.world[x1-1][i] = Tileset.WALL;
                }
                if(this.world[x1+1][i] == Tileset.NOTHING){
                    this.world[x1+1][i] = Tileset.WALL;
                }
            }

        }

        for(int i = x1; i <= x2; i += 1){
            this.world[i][y2] = Tileset.FLOOR;
            if(this.world[i][y2+1] == Tileset.NOTHING){
                this.world[i][y2+1] = Tileset.WALL;
            }
            if(this.world[i][y2-1] == Tileset.NOTHING){
                this.world[i][y2-1] = Tileset.WALL;
            }
        }
    }

    //create Tree
    public void locateTree() {

        for (int i = 0; i < this.rooms.length; i += 1) {
            if (this.rooms[i].decision) {
                for (int j = 0; j < RandomUtils.uniform(this.rand,2,4); j += 1) {
                    Random jRandom = new Random(j);
                    int x = RandomUtils.uniform(jRandom, rooms[i].LX, rooms[i].RX);
                    int y = RandomUtils.uniform(jRandom, rooms[i].DY, rooms[i].UY);
                    if (this.world[x][y] == Tileset.FLOOR) {
                        this.world[x][y] = Tileset.TREE;
                    }
                }
            }
        }


    }

    //create Avatar
    public void locateAvatar() {
        boolean locateAvatarFinished = false;

        for (int i = 0; i < this.rooms.length; i += 1) {
            if (this.rooms[i].decision) {
                this.you.xNow = RandomUtils.uniform(this.rand, rooms[i].LX, rooms[i].RX);
                this.you.yNow = RandomUtils.uniform(this.rand, rooms[i].DY, rooms[i].UY);
                if (this.world[this.you.xNow][this.you.yNow] != Tileset.WALL) {
                    // this.you.FormerTile = this.world[this.you.xNow][this.you.yNow];
                    this.world[this.you.xNow][this.you.yNow] = this.you.NowTile;
                    locateAvatarFinished = true;
                    break;
                }
            }
        }

        if (!locateAvatarFinished) {
            this.you.xNow = rooms[0].LX+1;
            this.you.yNow = rooms[0].DY+1;
            // this.you.FormerTile = this.world[this.you.xNow][this.you.yNow];
            this.world[this.you.xNow][this.you.yNow] = this.you.NowTile;
        }

    }

    public void makeMovements(String movement) {
        char[] movementChar = movement.toCharArray();
        for (int i = 0; i < movementChar.length; i += 1) {
            makeAMovement(movementChar[i]);
        }
    }

    public void makeAMovement(char c) {
        //System.out.println(c+"0.1");
        if (c == 'w' || c == 'W') {
            if (this.world[this.you.xNow][this.you.yNow+1] != Tileset.WALL) {
                this.world[this.you.xNow][this.you.yNow] = this.you.FormerTile;
                this.you.yNow += 1;
                this.you.FormerTile = this.world[this.you.xNow][this.you.yNow];
                this.world[this.you.xNow][this.you.yNow] = this.you.NowTile;
            }
        } else if (c == 's' || c == 'S') {
            if (this.world[this.you.xNow][this.you.yNow-1] != Tileset.WALL) {
                this.world[this.you.xNow][this.you.yNow] = this.you.FormerTile;
                this.you.yNow -= 1;
                this.you.FormerTile = this.world[this.you.xNow][this.you.yNow];
                this.world[this.you.xNow][this.you.yNow] = this.you.NowTile;
            }
        } else if (c == 'a' || c == 'A') {
            if (this.world[this.you.xNow-1][this.you.yNow] != Tileset.WALL) {
                this.world[this.you.xNow][this.you.yNow] = this.you.FormerTile;
                this.you.xNow -= 1;
                this.you.FormerTile = this.world[this.you.xNow][this.you.yNow];
                this.world[this.you.xNow][this.you.yNow] = this.you.NowTile;
            }
        } else if (c == 'd' || c == 'D') {
            if (this.world[this.you.xNow+1][this.you.yNow] != Tileset.WALL) {
                this.world[this.you.xNow][this.you.yNow] = this.you.FormerTile;
                this.you.xNow += 1;
                this.you.FormerTile = this.world[this.you.xNow][this.you.yNow];
                this.world[this.you.xNow][this.you.yNow] = this.you.NowTile;
            }
        }
    }



}
