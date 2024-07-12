package byow.Core;

import byow.TileEngine.TERenderer;
import byow.TileEngine.TETile;
//import byow.Core.RandomUtils;
//import byow.TileEngine.Tileset;
import edu.princeton.cs.algs4.StdDraw;
import org.antlr.v4.runtime.misc.Utils;

import java.io.*;
import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//import java.util.Arrays;
import java.util.Random;


public class Engine {  
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 90;
    public static final int HEIGHT = 60;
    public TETile[][] world = new TETile[WIDTH][HEIGHT];
    public int avatarInt = 0;
    public  WorldObject worldObjectK = new WorldObject(new Random(0));



    /**
     * Method used for exploring a fresh world. This method should handle all inputs,
     * including inputs from the main menu.
     */
    public void interactWithKeyboard() {
        InputSource inputSource = new KeyboardInput();
        String seedString;
        Random seed;
        StringBuilder stringBuilder = new StringBuilder();
        boolean quitSign = false;
        boolean inmap = false;
        boolean Chinese = false;

        while (inputSource.possibleNextInput()) {
            char c = inputSource.getNextKey();

            if (c == 'N' || c == 'n') {
                stringBuilder.append(c);
                seedString = lisentingSeed(stringBuilder, Chinese);
                long inputLong = Long.parseLong(seedString);
                seed = new Random(inputLong);
                worldObjectK = drawWorld(seed,avatarInt);
                inmap = true;

            } else if (c == 'L' || c == 'l') {

                File avatar = new File("avatar.txt");
                try {
                    avatarInt = Utils.readFile(String.valueOf(avatar))[0];
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File save = new File(String.valueOf(FileSystems.getDefault().getPath("save.txt")));
                String savedInput = "";
                try {
                    for (int i = 0; i < Utils.readFile(String.valueOf(save)).length; i++) {
                        savedInput += Utils.readFile(String.valueOf(save))[i];
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                char[] savedInputChar = savedInput.toCharArray();
                stringBuilder.append(savedInputChar[0]);


                StringBuilder seedstringBuilder = new StringBuilder();
                for (int i = 1; i < savedInput.length(); i += 1){
                    if (savedInputChar[i] == 's' || savedInputChar[i] == 'S') {
                        stringBuilder.append(savedInputChar[i]);
                        break;
                    }
                    seedstringBuilder.append(savedInputChar[i]);
                    stringBuilder.append(savedInputChar[i]);

                }
                seedString = seedstringBuilder.toString();
                long inputLong = Long.parseLong(seedString);
                seed = new Random(inputLong);
                worldObjectK = drawWorld(seed,avatarInt);

                int readseedChar = 1 + seedString.length() + 1;

                //get movement
                StringBuilder movementstringBuilder = new StringBuilder();
                for (int i = readseedChar; i < savedInput.length(); i += 1){
                    movementstringBuilder.append(savedInputChar[i]);
                    stringBuilder.append(savedInputChar[i]);
                }
                String movementString = movementstringBuilder.toString();
                worldObjectK.makeMovements(movementString);
                if (! movementString.equals("")) {
                    this.ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(worldObjectK.world);
                    StdDraw.pause(1000);
                }

                inmap = true;

            } else if ((c == 'Q' || c == 'q') && inmap == false) {
                System.exit(0);
                break;

            } else if (c == 'R' || c == 'r') {
                String archiveString = stringBuilder.toString();

                File replay = new File("replay.txt");
                try {
                    FileWriter fileWriter = new FileWriter(replay);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    bufferedWriter.write(archiveString);
                    bufferedWriter.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                replay = new File(String.valueOf(FileSystems.getDefault().getPath("replay.txt")));
                String savedInput = "";
                try {
                    for (int i = 0; i < Utils.readFile(String.valueOf(replay)).length; i++) {
                        savedInput += Utils.readFile(String.valueOf(replay))[i];
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                char[] savedInputChar = savedInput.toCharArray();

                StringBuilder seedstringBuilder = new StringBuilder();
                for (int i = 1; i < savedInput.length(); i += 1) {
                    if (savedInputChar[i] == 's' || savedInputChar[i] == 'S') {
                        break;
                    }
                    seedstringBuilder.append(savedInputChar[i]);
                }
                seedString = seedstringBuilder.toString();
                long inputLong = Long.parseLong(seedString);
                seed = new Random(inputLong);
                worldObjectK = drawWorld(seed,avatarInt);

                int readseedChar = 1 + seedString.length() + 1;

                //get movement
                StringBuilder movementstringBuilder = new StringBuilder();
                for (int i = readseedChar; i < savedInput.length(); i += 1) {
                    movementstringBuilder.append(savedInputChar[i]);
                    worldObjectK.makeAMovement(savedInputChar[i]);
                    ter.renderFrame(worldObjectK.world);
                    StdDraw.pause(300);
                }

            } else if (c == 'T' || c == 't') {
                Chinese = !Chinese;
                defaultStartingPage(Chinese);
/*
            } else

                if (c == 'U' || c == 'u') {

                char undo = ' ';
                if (lastmovement == 'W' || lastmovement == 'w') {
                    undo = 's';
                } else if (lastmovement == 'A' || lastmovement == 'a') {
                    undo = 'd';
                } else if (lastmovement == 'S' || lastmovement == 's') {
                    undo = 'w';
                } else if (lastmovement == 'D' || lastmovement == 'd') {
                    undo = 'a';
                }
                if ( undo == 'w' || undo == 'a' || undo == 's' || undo == 'd') {
                    stringBuilder.append(undo);
                    worldObjectK.makeAMovement(undo);
                    defaultStartingPage(Chinese);
                    ter.renderFrame(worldObjectK.world);
                }


 */
            } else if (c == 'W' || c == 'w' || c == 'A' || c == 'a' || c == 'S' || c == 's' || c == 'D' || c == 'd') {
                stringBuilder.append(c);
                //System.out.println(c+"0");
                worldObjectK.makeAMovement(c);
                //System.out.println(c+"1");
                //this.ter.initialize(WIDTH, HEIGHT);
                //System.out.println(c+"2");
                ter.renderFrame(worldObjectK.world);
                //System.out.println(c+"3");

            } else if ( (c == 'B' || c == 'b' || c == 'C' || c == 'c') && inmap == false) {
                if (c == 'B' || c == 'b') {
                    avatarInt = 1;
                } else {
                    avatarInt = 2;
                }

            } else if (c == ':') {
                quitSign = true;
            } else if ((c == 'Q' || c == 'q') && quitSign == true){
                String archiveString = stringBuilder.toString();

                File avatar = new File("avatar.txt");
                try {
                    FileWriter fileWriter = new FileWriter(avatar);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    bufferedWriter.write(avatarInt);
                    bufferedWriter.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                File save = new File("save.txt");
                try {
                    FileWriter fileWriter = new FileWriter(save);
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                    bufferedWriter.write(archiveString);
                    bufferedWriter.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                System.exit(0);
                break;
            }
            if (inmap) {
                //mouse
                int MX = (int) StdDraw.mouseX();
                int MY = (int) StdDraw.mouseY();
                TETile MTile = worldObjectK.world[MX][MY];
                StdDraw.setPenColor(StdDraw.WHITE);
                if (Chinese) {
                    StdDraw.text(4, HEIGHT - 1, " 模块： " + MTile.description());
                } else {
                    StdDraw.text(4, HEIGHT - 1, " Tile: " + MTile.description());
                }
                //time
                if (Chinese) {
                    DateTimeFormatter time = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH时mm分");
                    LocalDateTime now = LocalDateTime.now();
                    StdDraw.text(WIDTH - 8, HEIGHT - 1, "时间: " + time.format(now));
                } else {
                    DateTimeFormatter time = DateTimeFormatter.ofPattern("d MMM yyyy HH:mm");
                    LocalDateTime now = LocalDateTime.now();
                    StdDraw.text(WIDTH - 8, HEIGHT - 1, "Time: " + time.format(now));
                }
                //function
                if (Chinese) {
                    StdDraw.text(WIDTH / 2, 1, "地图内操作： 保存并退出(:Q/:q) 回放(R/r)");
                } else {
                    StdDraw.text(WIDTH / 2, 1, "In-map Save-and-Quit(:Q/:q) Replay(R/r)");
                }
            }
            StdDraw.show();

        }

    }

    public String lisentingSeed(StringBuilder stringBuilder, Boolean Chinese) {
        //System.out.println("seed"+"0");
        String seed = "";
        while(true) {
            StdDraw.clear();
            if (Chinese) {
                StdDraw.text(0.2, 0.6, "请输入你的种子 ");
            } else {
                StdDraw.text(0.2, 0.6, "Please enter your seed: ");
            }
            StdDraw.text(0.2, 0.5, seed);
            if (Chinese) {
                StdDraw.text(0.2, 0.4, "输入s/S代表结束");
            } else {
                StdDraw.text(0.2, 0.4, "Press s/S to stop");
            }
            StdDraw.pause(300);

            if (StdDraw.hasNextKeyTyped()) {
                char j = StdDraw.nextKeyTyped();
                stringBuilder.append(j);
                if(j == 's'){
                    return seed;
                }
                seed += Character.toString(j);
            }
        }
    }

    /**
     * Method used for autograding and testing your code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The engine should
     * behave exactly as if the user typed these characters into the engine using
     * interactWithKeyboard.
     *
     * Recall that strings ending in ":q" should cause the game to quite save. For example,
     * if we do interactWithInputString("n123sss:q"), we expect the game to run the first
     * 7 commands (n123sss) and then quit and save. If we then do
     * interactWithInputString("l"), we should be back in the exact same state.
     *
     * In other words, running both of these:
     *   - interactWithInputString("n123sss:q")
     *   - interactWithInputString("lww")
     *
     * should yield the exact same world state as:
     *   - interactWithInputString("n123sssww")
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] interactWithInputString(String input) {
        // TODO: Fill out this method so that it run the engine using the input
        // passed in as an argument, and return a 2D tile representation of the
        // world that would have been drawn if the same inputs had been given
        // to interactWithKeyboard().
        //
        // See proj3.byow.InputDemo for a demo of how you can make a nice clean interface
        // that works for many different input types.

        InputSource inputSource = new StringInput(input);
        StringBuilder stringBuilder = new StringBuilder();
        char[] inputChar = input.toCharArray();

        while (inputSource.possibleNextInput()) {

            char c = inputSource.getNextKey();

            if (c == 'N' || c == 'n') {
                stringBuilder.append(inputChar[0]);

                //get seed
                StringBuilder seedstringBuilder = new StringBuilder();
                for (int i = 1; i < input.length(); i += 1){
                    if (inputChar[i] == 's' || inputChar[i] == 'S') {
                        stringBuilder.append(inputChar[i]);
                        break;
                    }
                    seedstringBuilder.append(inputChar[i]);
                    stringBuilder.append(inputChar[i]);
                }
                String seedString = seedstringBuilder.toString();
                long inputLong = Long.parseLong(seedString);
                Random seed = new Random(inputLong);

                //draw world
                WorldObject worldObject = drawWorld(seed);
/*
                this.ter.initialize(WIDTH, HEIGHT);
                WorldObject worldObject = new WorldObject(seed);
                worldObject.createMap();
                ter.renderFrame(worldObject.world);
                StdDraw.pause(1000);

 */

                int readseedChar = 1 + seedString.length() + 1;
                // [n,s]

                //get movement
                StringBuilder movementstringBuilder = new StringBuilder();
                boolean saveandquit = false;
                for (int i = readseedChar; i < input.length(); i += 1){
                    if (inputChar[i] == ':' && (inputChar[i+1] == 'q' || inputChar[i+1] == 'Q')) {
                        saveandquit = true;
                        break;
                    }
                    movementstringBuilder.append(inputChar[i]);
                    stringBuilder.append(inputChar[i]);
                }
                String movementString = movementstringBuilder.toString();
                worldObject.makeMovements(movementString);
                if (! movementString.equals("")) {
                    //this.ter.initialize(WIDTH, HEIGHT);
                    ter.renderFrame(worldObject.world);
                    StdDraw.pause(1000);
                }

                String archiveString = stringBuilder.toString();

                int readmovementChar = movementString.length()+2;
                // (s,:q/:Q)

                int readChar = readseedChar+readmovementChar;

                if (saveandquit) {

                    StdDraw.clear();
                    StdDraw.text(WIDTH / 2, HEIGHT / 2, "You've quit the game");
                    StdDraw.show();
                    StdDraw.pause(500);

                    File save = new File("save.txt");
                    try {
                        FileWriter fileWriter = new FileWriter(save);
                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

                        bufferedWriter.write(archiveString);
                        bufferedWriter.close();

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                if (readChar < input.length() ) {
                    StringBuilder newstringBuilder = new StringBuilder();
                    for (int i = readChar; i < input.length(); i += 1) {
                        newstringBuilder.append(inputChar[i]);
                    }
                    String newString = newstringBuilder.toString();
                    return interactWithInputString(newString);

                }

                return worldObject.world;

            }else if (c == 'L' || c == 'l') {
                File save = new File(String.valueOf(FileSystems.getDefault().getPath("save.txt")));
                String savedInput = "";
                try {
                    for (int i = 0; i < Utils.readFile(String.valueOf(save)).length; i++) {
                        savedInput += Utils.readFile(String.valueOf(save))[i];
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                for (int i = 1; i < input.length(); i += 1){
                    stringBuilder.append(inputChar[i]);
                }
                String newinput = stringBuilder.toString();
                String nowInput = savedInput + newinput;
                return interactWithInputString(nowInput);


            } else if (c == 'Q' || c == 'q') {

                StdDraw.clear();
                StdDraw.text(WIDTH/2, HEIGHT/2, "You've quit the game");
                StdDraw.show();
                StdDraw.pause(500);

                return world;
            }
        }
        return world;
    }

    public void defaultStartingPage(Boolean Chinese) {
        if (Chinese) {
            StdDraw.clear();
            StdDraw.text(0.3, 0.9, "CS61BL: 雪莉的世界");
            StdDraw.text(0.3, 0.8, "开始新一局游戏(N/n)");
            StdDraw.text(0.3, 0.7, "加载上一局游戏(L/l)");
            StdDraw.text(0.3, 0.6, "退出游戏(Q/q)");
            StdDraw.text(0.3, 0.5, "语言切换(T/t)");
            StdDraw.text(0.3, 0.4, "主体外表选择(B/b)");
            StdDraw.text(0.3, 0.3, "默认 '@'; (B/b) 选择 '&'; (C/c) 选择 '^';");
            StdDraw.text(0.3, 0.2, "地图内保存并退出(:Q/:q);回放(R/r)");
            StdDraw.text(0.3, 0.1, "请遵循以上提示");
        } else {
            StdDraw.clear();
            StdDraw.text(0.3, 0.9, "CS61BL: Shirley's World");
            StdDraw.text(0.3, 0.8, "New Game(N/n)");
            StdDraw.text(0.3, 0.7, "Load Game(L/l)");
            StdDraw.text(0.3, 0.6, "Quit Game(Q/q)");
            StdDraw.text(0.3, 0.5, "Language Changing(T/t)");
            StdDraw.text(0.3, 0.4, "AVATAR's appearance choosing");
            StdDraw.text(0.3, 0.3, "Default '@'; (B/b) for '&'; (C/c) for '^';");
            StdDraw.text(0.3, 0.2, "In-map Save and Quit(:Q/:q); Replay(R/r)");
            StdDraw.text(0.3, 0.1, "Please follow the instruction above");
        }
    }

    public WorldObject drawWorld(Random seed) {
        this.ter.initialize(WIDTH, HEIGHT);
        WorldObject worldObject = new WorldObject(seed);
        worldObject.createMap();
        ter.renderFrame(worldObject.world);
        StdDraw.pause(1000);
        return worldObject;
    }

    public WorldObject drawWorld(Random seed, int avatarInt) {
        this.ter.initialize(WIDTH, HEIGHT);
        WorldObject worldObject = new WorldObject(seed,avatarInt);
        worldObject.createMap();
        ter.renderFrame(worldObject.world);
        StdDraw.pause(1000);
        return worldObject;
    }

}
