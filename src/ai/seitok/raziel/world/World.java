package ai.seitok.raziel.world;

import ai.seitok.raziel.Direction;
import ai.seitok.raziel.ui.RazielUI;

import java.io.*;

public class World {

    // starting location and direction for the robot
    private int robotX, robotY;
    private Direction robotDir = Direction.NORTH;

    // the tiles used in this world (actually 0-indexed unlike Karel)
    private Tile[][] tiles;
    // the name of the world
    private String name;

    // used to check if we can edit safely (i.e, we aren't cheating)
    private boolean isEditable;

    // limit access to the no-args for stability reasons
    private World(){}

    // creates a world with the given arguments
    public World(String name, int width, int height){
        this.name = name;
        this.tiles = new Tile[width][height];

        for(int x=0; x < width; x++){
            for(int y=0; y < height; y++){
                tiles[x][y] = new Tile();
            }
        }
    }

    // get a tile from this world
    public Tile getTile(int x, int y){
        // bound checking
        if(x >= tiles.length || y >= tiles[0].length || x < 0 || y < 0)
            throw new IllegalArgumentException("x and y must be within bounds of world size!");
        return tiles[x][y];
    }

    public void setRobotX(int x){
        checkEditable();
        robotX = x;
    }

    public void setRobotY(int y){
        checkEditable();
        robotY = y;
    }

    public void setRobotDir(Direction dir){
        checkEditable();
        robotDir = dir;
    }

    public String getName(){
        return name;
    }

    // get the width of the map
    public int getWidth(){
        return tiles.length;
    }

    // get the height of the map
    public int getHeight(){
        return tiles[0].length;
    }

    // get a copy of the tiles
    public Tile[][] getTiles() {
        return tiles.clone();
    }

    // get the starting robot position X coord
    public int getRobotX(){
        return robotX;
    }

    // get the starting robot position Y coord
    public int getRobotY(){
        return robotY;
    }

    // get the starting robot direction
    public Direction getRobotDir(){
        return robotDir;
    }

    public boolean isEditable(){
        return isEditable;
    }

    public World toUnmodifiableWorld(){
        // don't check if RazielUI is on the stack
        this.isEditable = false;
        return this;
    }

    public World toEditableWorld(){
        if(!isRasielOnStack()){
            throw new SecurityException("I'm sorry Dave, I'm afraid I can't do that.");
        }

        this.isEditable = true;
        return this;
    }

    // read and import a world from a path
    public static World importWorld(String path){
        DataInputStream dis = null;
        World world = null;
        int width, height;
        try {
            // read from input and create a world to return
            dis = new DataInputStream(new FileInputStream(path));
            world = new World();

            // read information about the world
            world.name = dis.readUTF();
            world.robotX = dis.readInt();
            world.robotY = dis.readInt();
            world.robotDir = Direction.valueOf(dis.readInt());
            world.tiles = new Tile[width = dis.readInt()][height = dis.readInt()];

            // loop and read each tile
            for(int x=0; x < width; x++){
                for(int y=0; y < height; y++){
                    world.tiles[x][y] = Tile.read(dis);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            // close the input stream
            try {
                if(dis != null) {
                    dis.close();
                }
            } catch (IOException e){}
        }
        return world;
    }

    public void exportWorld(String path){
        DataOutputStream dos = null;
        try {
            // open up an output
            dos = new DataOutputStream(new FileOutputStream(path));

            // write out basic information
            dos.writeUTF(name);
            dos.writeInt(robotX);
            dos.writeInt(robotY);
            dos.writeInt(robotDir.ordinal());
            dos.writeInt(tiles.length);
            dos.writeInt(tiles[0].length);

            // write out tile information
            for(int x=0; x < tiles.length; x++){
                for(int y=0; y < tiles[0].length; y++){
                    tiles[x][y].write(dos);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            // properly flush and close the output stream
            try {
                if (dos != null) {
                    dos.flush();
                    dos.close();
                }
            } catch (IOException e){}
        }
    }

    private final boolean isRasielOnStack(){
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        boolean safe = false;
        for(StackTraceElement stack : elements){
            if(stack.getClassName().equals(RazielUI.class.getName())){
                safe = true;
                break;
            }
        }
        return safe;
    }

    private final void checkEditable(){
        if(!isEditable)
            throw new IllegalStateException(getName() + " is not editable!");
    }

}
