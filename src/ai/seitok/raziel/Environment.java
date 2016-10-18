package ai.seitok.raziel;

import ai.seitok.raziel.world.World;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;

public final class Environment {

    // the singleton instance
    private static final Environment SINGLETON = new Environment();

    // the robot
    private Robot theRobot;
    // the world the robot is in
    private World theWorld;

    // automatically check for a robot implementation
    private Environment(){
        theRobot = Application.findRasielImplFromClassPath();
    }

    public static void setWorld(World world){
        SINGLETON._setWorld(world);
    }

    public static void loadWorld(String pathToWorld){
        SINGLETON._loadWorld(pathToWorld);
    }

    public static World getWorld(){
        return SINGLETON._getWorld();
    }

    public static Robot getRobot(){
        return SINGLETON._getRobot();
    }

    public static void saveWorld(String path){
        SINGLETON._saveWorld(path);
    }

    public static void resetRobot(){
        SINGLETON._resetRobot();
    }

    private void _setWorld(World world){
        theWorld = world;
        resetRobot();
    }

    // loads a world and resets the bot
    private void _loadWorld(String pathToWorld){
        theWorld = World.importWorld(pathToWorld);
        resetRobot();
    }

    private void _saveWorld(String pathToWorld){
        theWorld.exportWorld(pathToWorld);
    }

    // returns the world
    private World _getWorld(){
        return theWorld;
    }

    // returns the robot
    private Robot _getRobot(){
        return theRobot;
    }

    // resets the robot to it's basic starting position
    private void _resetRobot(){
        try {
            // use reflection to access really dirty and hidden setX/Y/Direction methods
            // why use reflection and why not just expose these methods?
            // cheaters. that's why.
            // i mean, if they can use and understand Reflection, do they really belong in AP CS-A, honestly.
            // in theory, we can remove the need for methods and simple call getDeclaredField(field) and then call
            // Field.set from there, however, creating methods can prove to be more useful in the case that we want
            // to modify Raziel to perform additional checking to the X/Y/Direction coords.
            Method x = Raziel.class.getDeclaredMethod("setX", int.class);
            Method y = Raziel.class.getDeclaredMethod("setY", int.class);
            Method d = Raziel.class.getDeclaredMethod("setDirection", Direction.class);
            // allow us access to these methods if we don't already
            AccessibleObject.setAccessible(new AccessibleObject[]{x, y, d}, true);
            // invoke all of them to set the fields
            x.invoke(theRobot, theWorld.getRobotX());
            y.invoke(theRobot, theWorld.getRobotY());
            d.invoke(theRobot, theWorld.getRobotDir());
        } catch (Throwable t){
            // if we actually have an error, then it's probably due to a SecurityManager being installed and denying
            // our right to Reflection. rip. we can't really reset the bot now, can we?
            t.printStackTrace();
        }
    }

}
