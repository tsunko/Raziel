package ai.seitok.raziel.ui;

import ai.seitok.raziel.Environment;
import ai.seitok.raziel.NoCloneAffineTransform;
import ai.seitok.raziel.Robot;
import ai.seitok.raziel.Sprites;
import ai.seitok.raziel.world.Tile;
import ai.seitok.raziel.world.World;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

public class WorldPanel extends JPanel implements MouseListener, MouseMotionListener, ComponentListener {

    private PalettePanel palette;
    private int widthPerTile, heightPerTile;
    private int tileCenterX, tileCenterY;
    private int coinSpriteIndex = 0;
    private int tileHoverX, tileHoverY;

    // raziel "breathing" animation
    private int breath;
    private boolean breathOut;

    private AffineTransform transformer = new NoCloneAffineTransform();
    private AffineTransformOp operation = new AffineTransformOp(transformer, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

    public WorldPanel(PalettePanel palette){
        addMouseListener(this);
        addMouseMotionListener(this);
        addComponentListener(this);

        // setup regular painting for animations
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run(){
                WorldPanel.this.repaint();
            }
        }, 0, 50);
        this.palette = palette;
    }

    // please don't look at the code below
    // it makes me want to rip my eyes out, but it works.
    @Override
    public void paint(Graphics non2d){
        super.paint(non2d); // let the component clear itself

        Graphics2D graphics = (Graphics2D)non2d; // cast this up; it's safe since _most_ Swing componenets use Graphics2D
        // setup basic environment to draw from
        Robot robot = Environment.getRobot();
        Tile[][] tiles = Environment.getWorld().getTiles();

        // pre-render the robot sprite
        // perform rotation based on the current direction
        transformer.setToRotation(Math.toRadians(robot.getDirection().getDegrees()), Sprites.getRobotSprite().getWidth() / 2, Sprites.getRobotSprite().getHeight() / 2);
        BufferedImage processed = operation.filter(Sprites.getRobotSprite(), null);

        /* render world */
        // draw border
        graphics.setColor(Color.BLACK);
        graphics.drawRect(1, 1, getWidth() - 2, getHeight() - 2);
        if(++coinSpriteIndex > (Sprites.getCoinSpriteCount()*2 - 1)){
            coinSpriteIndex = 0;
        }

        // draw phantom placement stuff
        if(Environment.getWorld().isEditable() && palette.hasSelection() && palette.getSelectedName() != null){
            AlphaComposite alphaOverlay = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            Composite oldComposite = graphics.getComposite(); // store old composite (setComposite doesn't like null values)
            graphics.setComposite(alphaOverlay); // set the new overlay
            switch(palette.getSelectedName()){
                case "razielButton":{
                    // don't apply breath to this
                    graphics.drawImage(
                            processed,
                            tileHoverX * widthPerTile,
                            tileHoverY * heightPerTile,
                            widthPerTile,
                            heightPerTile,
                            null
                    );
                    break;
                }

                case "coinButton":{
                    // don't do coin animations
                    graphics.drawImage(
                            Sprites.getCoinSprite(0),
                            tileHoverX * widthPerTile + widthPerTile/4,
                            tileHoverY * heightPerTile + heightPerTile/4,
                            widthPerTile - widthPerTile/2,
                            heightPerTile - heightPerTile/2,
                            null
                    );
                    break;
                }

                case "rightWallButton":{
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(
                            tileHoverX * widthPerTile + widthPerTile - (heightPerTile/16)/2,
                            tileHoverY * heightPerTile,
                            heightPerTile/16,
                            heightPerTile
                    );
                    break;
                }

                case "bottomWallButton":{
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(
                            tileHoverX * widthPerTile,
                            tileHoverY * heightPerTile + heightPerTile - (widthPerTile/16)/2,
                            widthPerTile,
                            widthPerTile/16
                    );
                    break;
                }
            }
            // reset composite
            graphics.setComposite(oldComposite);
        }

        // draw tile indicators and walls
        for(int x=0; x < tiles.length; x++){
            for(int y=0; y < tiles[0].length; y++){
                // draw walls first
                if(tiles[x][y].hasBottomWall()){
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(
                            x * widthPerTile,
                            y * heightPerTile + heightPerTile - (widthPerTile/16)/2,
                            widthPerTile,
                            widthPerTile/16
                    );
                }

                // draw the right wall
                if(tiles[x][y].hasRightWall()){
                    graphics.setColor(Color.BLACK);
                    graphics.fillRect(
                            x * widthPerTile + widthPerTile - (widthPerTile/16)/2,
                            y * heightPerTile,
                            heightPerTile/16,
                            heightPerTile
                    );
                }

                // draw coins
                if(tiles[x][y].hasCoins()){
                    BufferedImage coin = Sprites.getCoinSprite(coinSpriteIndex/2);
                    String count = String.valueOf(tiles[x][y].getCoinCount());
                    graphics.drawImage(
                            coin,
                            x * widthPerTile + widthPerTile/4,
                            y * heightPerTile + heightPerTile/4,
                            widthPerTile - widthPerTile/2,
                            heightPerTile - heightPerTile/2,
                            null)
                    ;
                    graphics.drawString(
                            count,
                            x * widthPerTile + widthPerTile/2 - getFontMetrics(getFont()).stringWidth(count)/2, // center text
                            y * heightPerTile + heightPerTile/6
                    );
                    // definitely don't draw the center ball
                    continue;
                }

                // if we're on the robot's current tile, don't draw the center ball
                if(x == robot.getX() && y == robot.getY()){
                    continue;
                }

                // draw the center ball thing to represent a tile
                graphics.setColor(Color.BLACK);
                graphics.fillRect(
                        x * widthPerTile + widthPerTile/2 - (tileCenterX/2),
                        y * heightPerTile + heightPerTile/2 - (tileCenterY/2),
                        tileCenterX,
                        tileCenterY
                );
            }
        }

        if(breath >= widthPerTile/32){
            breathOut = true;
        } else if(breath <= -widthPerTile/32){
            breathOut = false;
        }

        if(breathOut) breath--;
        else breath++;

        // now actually draw the robot
        graphics.drawImage(processed, robot.getX() * widthPerTile , robot.getY() * heightPerTile + breath, widthPerTile, heightPerTile, null);

        // dispose of the graphics so we don't leak memory
        graphics.dispose();
    }

    public void recalculate(){
        // grab a copy of the world tiles
        Tile[][] tiles = Environment.getWorld().getTiles();
        // calculate basic information about how big things should be
        widthPerTile = (getWidth() / tiles.length);
        heightPerTile = (getHeight() / tiles[0].length);
        tileCenterX = widthPerTile / 8;
        tileCenterY = heightPerTile / 8;
    }

    @Override
    public void componentResized(ComponentEvent e){
        recalculate(); // recalc the sizes
        repaint(); // update now
    }

    @Override
    public void mousePressed(MouseEvent e){
        requestFocus();
        World world = Environment.getWorld();
        if(!world.isEditable()){
            return;
        }

        if(!palette.hasSelection() || palette.getSelectedName() == null) return;
        switch(palette.getSelectedName()){
            case "razielButton":{
                world.setRobotX(tileHoverX);
                world.setRobotY(tileHoverY);
                Environment.resetRobot();
                break;
            }

            case "coinButton":{
                Tile tile = world.getTile(tileHoverX, tileHoverY);
                if(e.getButton() == MouseEvent.BUTTON1){
                    tile.setCoins(tile.getCoinCount() + 1);
                } else if(e.getButton() == MouseEvent.BUTTON3 && tile.getCoinCount() > 0){
                    tile.setCoins(tile.getCoinCount() - 1);
                }
                break;
            }

            case "rightWallButton":{
                Tile tile = world.getTile(tileHoverX, tileHoverY);
                try {
                    Field rightWall_f = Tile.class.getDeclaredField("rightWall");
                    rightWall_f.setAccessible(true);
                    rightWall_f.set(tile, e.getButton() == MouseEvent.BUTTON1);
                } catch (ReflectiveOperationException e1){
                    e1.printStackTrace();
                }
                break;
            }

            case "bottomWallButton":{
                Tile tile = world.getTile(tileHoverX, tileHoverY);
                try {
                    Field bottomWall_f = Tile.class.getDeclaredField("bottomWall");
                    bottomWall_f.setAccessible(true);
                    bottomWall_f.set(tile, e.getButton() == MouseEvent.BUTTON1);
                } catch (ReflectiveOperationException e1){
                    e1.printStackTrace();
                }
                break;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e){
        if(!Environment.getWorld().isEditable()){
            return;
        }

        tileHoverX = e.getX() / widthPerTile;
        tileHoverY = e.getY() / heightPerTile;
    }

    @Override
    public void mouseDragged(MouseEvent e){}

    @Override
    public void componentMoved(ComponentEvent e){}

    @Override
    public void componentShown(ComponentEvent e){}

    @Override
    public void componentHidden(ComponentEvent e){}

    @Override
    public void mouseClicked(MouseEvent e){}

    @Override
    public void mouseReleased(MouseEvent e){}

    @Override
    public void mouseEntered(MouseEvent e){}

    @Override
    public void mouseExited(MouseEvent e){}

}
