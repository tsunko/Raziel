package ai.seitok.raziel.world;

import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Tile {

    private boolean bottomWall; // is there a wall on the bottom of this tile
    private boolean rightWall; // is there a wall on the right of this tile
    private int coins; // coins on the tile
    private Color color = Color.WHITE; // the tile's color, not really used.

    Tile(){}

    public boolean hasBottomWall() {
        return bottomWall;
    }

    public boolean hasRightWall() {
        return rightWall;
    }

    public boolean hasCoins(){
        return coins > 0;
    }

    public int getCoinCount(){
        return coins;
    }

    public void setCoins(int coinCount){
        this.coins = coinCount;
    }

    // read basic tile information from a DataInputStream
    public static Tile read(DataInputStream dis){
        Tile tile = null;
        try {
            // create tile
            tile = new Tile();
            // read properties
            tile.bottomWall = dis.readBoolean();
            tile.rightWall = dis.readBoolean();
            tile.coins = dis.readInt();
            tile.color = new Color(dis.read(), dis.read(), dis.read());
        } catch (IOException e){
            e.printStackTrace();
        }
        return tile;
    }

    // write out tile information
    public void write(DataOutputStream dos){
        try {
            // write properties of this tile
            dos.writeBoolean(bottomWall);
            dos.writeBoolean(rightWall);
            dos.writeInt(coins);
            dos.write(color.getRed());
            dos.write(color.getGreen());
            dos.write(color.getBlue());
        } catch (IOException e){
            e.printStackTrace();
        }
    }

}
