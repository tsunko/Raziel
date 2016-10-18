package ai.seitok.raziel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public final class Sprites {

    private static final BufferedImage ROBOT_SPRITE;
    private static final BufferedImage[] COIN_SPRITES = new BufferedImage[8];
    static {
        try {
            ROBOT_SPRITE = ImageIO.read(new File("sprite.png"));

            BufferedImage coinAnimationFrames = ImageIO.read(new File("coin_animation.png"));
            for(int i=0; i < 512; i += 64){
                COIN_SPRITES[i / 64] = coinAnimationFrames.getSubimage(i, 0, 64, 64);
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public static BufferedImage getRobotSprite(){
        return ROBOT_SPRITE;
    }

    public static BufferedImage getCoinSprite(int animationIndex){
        return COIN_SPRITES[animationIndex];
    }

    public static int getCoinSpriteCount(){
        return COIN_SPRITES.length;
    }

    private Sprites(){}

}
