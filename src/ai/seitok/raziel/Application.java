package ai.seitok.raziel;

import ai.seitok.raziel.ui.RazielUI;
import ai.seitok.raziel.ui.SelectionUI;
import ai.seitok.raziel.world.World;

import javax.swing.*;
import java.io.*;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class Application {

    private static Application SINGLETON;
    private final RazielUI ui;

    // entry
    public static void main(String[] args) throws IOException {
        // make our application pretty and stuff; just use the ugly built-in UI look if we fail
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Throwable e){}

        SINGLETON = new Application();
    }

    // creates a dummy world; not documented as there's no need for it to be
    public static void maina(String[] args) throws IOException {
        DataOutputStream dos = new DataOutputStream(new FileOutputStream("whatamidoing.rworld"));
        dos.writeUTF("The Random World");
        dos.writeInt(0); dos.writeInt(0); dos.writeInt(0); // write x, y, direction for Raziel
        dos.writeInt(50); dos.writeInt(50); // write the width and height of the map
        for(int i=0; i < 2500; i++){
            dos.writeBoolean(ThreadLocalRandom.current().nextBoolean());
            dos.writeBoolean(ThreadLocalRandom.current().nextBoolean());
            dos.writeInt(ThreadLocalRandom.current().nextBoolean() ? 1 : 0);
            dos.write(255);
            dos.write(255);
            dos.write(255);
        }
        dos.flush();
        dos.close();
    }

    // returns the delay from within the UI
    public static int getDelay(){
        return SINGLETON.ui.getDelay();
    }

    // load the basic testing world and create a RazielUI to display stuff
    public Application() throws IOException {
        Environment.setWorld(new World("No world", 5, 5));
        this.ui = new RazielUI();
        this.ui.display();
    }

    // here's where the magic in terms of finding the appropriate Raziel implementation
    public static Robot findRasielImplFromClassPath(){
        List<Robot> robots = new ArrayList<>(); // create a list of robots to use
        Stream.of(System.getProperty("java.class.path").split(";")).forEach(path -> {
            // skip jars
            if(path.endsWith(".jar")){
                return;
            } else {
                // find any implementation from the path
                List<Robot> foundRobots = findRasielImpl(path, path);
                if(foundRobots != null){
                    robots.addAll(foundRobots);
                }
            }
        });
        // rip, none found
        if(robots.isEmpty()){
            throw new IllegalArgumentException("no Raziel implementation found");
        } else if(robots.size() == 1){
            // only 1 found, just use it
            return robots.get(0);
        } else {
            // allow the user to pick a robot
            return robots.get(SelectionUI.createDialog(robots));
        }
    }

    public static List<Robot> findRasielImpl(String rootPath, String path){
        List<Robot> robotList = new ArrayList<>();
        File file = new File(path);
        if(file.isFile()){ // check if it's a file
            throw new IllegalArgumentException("path can't be a file");
        } else {
            // list the folder's contents
            File[] files = file.listFiles();
            // if there's none, return null
            if(files == null) return null;
            for(File subfile : files){
                // if it's a directory, recursivly look through it
                if(subfile.isDirectory()){
                    List<Robot> subList = findRasielImpl(rootPath, subfile.getAbsolutePath());
                    if(subList != null){
                        robotList.addAll(subList);
                    }
                } else {
                    // check if it's an actual class file
                    if(subfile.getName().endsWith(".class")){
                        // magical ClassLoader that has an exposed defineClass method so we can quickly load and check
                        // the class to see if it's an actual Raziel implementation
                        ExposedClassLoader loader = new ExposedClassLoader(Application.class.getClassLoader());
                        // beautify and properly generate the class' name
                        int substrIndex = rootPath.length() + 1;
                        String className = subfile.getAbsolutePath()
                                .substring(substrIndex, subfile.getAbsolutePath().length() - ".class".length())
                                .replace("\\", ".")
                                .replace("/", ".");
                        // read the class bytes fully
                        byte[] bytes = readFileFully(subfile);
                        try {
                            // attempt to define the class
                            Class<?> klass = loader.defineClass(className, bytes);
                            if(Robot.class.isAssignableFrom(klass) && (klass.getModifiers() & Modifier.ABSTRACT) == 0){
                                // ooo, it's an implementation
                                Class<?> safeClass = Application.class.getClassLoader().loadClass(className);
                                robotList.add((Robot)safeClass.newInstance());
                                System.out.println(safeClass.getClassLoader() == Application.class.getClassLoader());
                            }
                        } catch (LinkageError e){
                            // error, probably already defined by the main classloader
                            // the reason why we set this to null is that we want the GC to remove this asap
                            // so that we can unload the classes we loaded
                            loader = null;
                        } catch (ReflectiveOperationException e){
                            // now we're hosed, someone wasn't being nice and declared a new Constructor
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return robotList;
    }

    // basic function to read a file as a byte array
    private static byte[] readFileFully(File file){
        FileInputStream fis = null; // input
        ByteArrayOutputStream baos = new ByteArrayOutputStream(); // output
        try {
            // open input
            fis = new FileInputStream(file);
            byte[] buf = new byte[8192]; // buffer to read into and write out to baos
            int read; // amount read
            // just read and put into baos
            while((read = fis.read(buf)) != -1){
                baos.write(buf, 0, read);
            }
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            // close the input
            if(fis != null){
                try {
                    fis.close();
                } catch(IOException e){}
            }
        }
        // and now get our bytes out
        return baos.toByteArray();
    }

}
