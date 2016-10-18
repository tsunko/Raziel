package ai.seitok.raziel.ui;

import ai.seitok.raziel.Environment;
import ai.seitok.raziel.Robot;
import ai.seitok.raziel.Sprites;
import ai.seitok.raziel.world.World;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.Timer;
/**
 * Root UI for Raziel
 */
public class RazielUI {

    // root frame; the main container
    private JFrame root;
    // run button to start a Raziel implementation
    private JButton runButton;
    // resets Raziel to it's original state on the current world
    private JButton resetButton;
    // loads a world
    private JButton loadButton;
    // ceates a world
    private JButton newButton;
    // edits a world
    private JButton editButton;
    // ceates a world
    private JButton saveButton;
    // world viewer
    private WorldPanel worldView;
    // speed adjuster
    private JSlider speedSlider;
    // basically the thread that maintains the implementation of Raziel running
    private Thread nonEDThread;

    public RazielUI(){
        this.root = new JFrame("Raziel UI - " + Environment.getWorld().getName());

        // create the world view
        PalettePanel palettePanel = new PalettePanel();
        this.worldView = new WorldPanel(palettePanel);
        this.worldView.setMinimumSize(new Dimension(500, 500));
        this.worldView.setPreferredSize(new Dimension(500, 500));

        // create and begin listening on a runButton
        this.runButton = new JButton("Run Rasial");
        this.runButton.addActionListener(event -> {
            // check if robot is running already
            if(nonEDThread != null && nonEDThread.isAlive()){
                JOptionPane.showMessageDialog(root, "Robot is already running!", "Error!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // create a new thread
            nonEDThread = new Thread(() -> {
                Robot robot = Environment.getRobot();
                robot.run();
            });
            // start it up
            nonEDThread.start();
        });

        // create and begin listening on a resetting button
        this.resetButton = new JButton("Reset World");
        this.resetButton.addActionListener(event -> {
            // stop any current threads
            if(nonEDThread != null && nonEDThread.isAlive()){
                nonEDThread.stop();
            }
            // reset and redraw the world
            Environment.resetRobot();
            worldView.repaint();
        });

        // create and begin listening on a load world button
        this.loadButton = new JButton("Load World");
        this.loadButton.addActionListener(event -> {
            // use AWT FileDialog instead of JFileChooser because JFileChooser is inherently slow and very difficult to work with
            FileDialog chooser = new FileDialog(root, "Select a world.", FileDialog.LOAD);
            chooser.setDirectory("."); // set current directory as project root
            chooser.setFile("*.rworld"); // look for ".rworld" files
            chooser.setVisible(true); // set the chooser as visible

            // get the file once we're done
            String file = chooser.getFile();
            if(file != null && (new File(file).exists())){
                // now load and repaint the world
                Environment.loadWorld(file);
                root.setTitle("Raziel UI - " + Environment.getWorld().getName());
                editButton.setText("Edit World");

                if(nonEDThread != null && nonEDThread.isAlive()){
                    nonEDThread.stop();
                }

                worldView.recalculate();
                worldView.repaint();
            }
        });

        this.newButton = new JButton("Create World");
        this.newButton.addActionListener(event -> {
            World created = CreateWorldUI.createDialog(root);
            if(created == null) return;
            Environment.setWorld(created);
            created.toEditableWorld();
            root.setTitle("Raziel UI - " + Environment.getWorld().getName());
            editButton.setText("Finish Editing");
            worldView.recalculate();
            worldView.repaint();
        });

        this.editButton = new JButton("Edit World");
        this.editButton.addActionListener(event -> {
            World world = Environment.getWorld();
            if(world.isEditable()){
                world.toUnmodifiableWorld();
                editButton.setText("Edit World");
            } else {
                world.toEditableWorld();
                editButton.setText("Finish Editing");
            }
        });

        this.saveButton = new JButton("Save World");
        this.saveButton.addActionListener(event -> {
            FileDialog chooser = new FileDialog(root, "Save world...", FileDialog.SAVE);
            chooser.setDirectory("."); // set current directory as project root
            chooser.setFile("*.rworld"); // look for ".rworld" files
            chooser.setVisible(true); // set the chooser as visible

            String file = chooser.getFile();
            if(file != null){
                // now load and repaint the world
                Environment.saveWorld(file.endsWith(".rworld") ? file : file + ".rworld");
                JOptionPane.showMessageDialog(root, "Saved successfully!", "Saved successfully!", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // create the slider (default is 1 * 100ms, which is, 100ms)
        speedSlider = new JSlider(JSlider.HORIZONTAL, 0, 10, 9);
        speedSlider.setMajorTickSpacing(1); // set the ticking to 1 so that we can press left and right arrows without having to spam them
        speedSlider.setPaintLabels(true); // paint the labels

        // create a coinLabel listing (we can't use HashMap because HashMap does not extend Dictionary)
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(0, new JLabel("Super Slow")); // add "Super Slow" to represent 1000ms delay
        labelTable.put(10, new JLabel("Super Fast")); // add "Super Fast" to represent basically 0ms delay
        speedSlider.setLabelTable(labelTable); // set the labels to use

        // grab the main container and set it's layout to a BorderLayout
        Container pane = this.root.getContentPane();
        pane.setLayout(new BorderLayout());

        JLabel coinLabel = new JLabel(" Coins: ");
        coinLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        ImageIcon icon = new ImageIcon(Sprites.getCoinSprite(0));
        coinLabel.setIcon(icon);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int index;

            @Override
            public void run(){
                if(++index >= Sprites.getCoinSpriteCount()){
                    index = 0;
                }
                icon.setImage(Sprites.getCoinSprite(index));
                // i would to love to make this automatically update as soon as pickUpCoin happens,
                // but i designed Raziel without considering that as a possible feature
                coinLabel.setText(" Coins: " + Environment.getRobot().getCoins());
                coinLabel.repaint();
            }
        }, 0, 100);

        // create a control panel to contain our settings/buttons
        JPanel controlPanel = new JPanel(new GridLayout(0, 1));
        controlPanel.add(runButton);
        controlPanel.add(resetButton);
        controlPanel.add(loadButton);
        controlPanel.add(coinLabel);
        controlPanel.add(speedSlider);

        // create a world editing panel
        JPanel worldEditPanel = new JPanel(new GridLayout(0, 1));
        worldEditPanel.add(newButton);
        worldEditPanel.add(editButton);
        worldEditPanel.add(saveButton);
        worldEditPanel.add(palettePanel);

        // create tabbing so we can filter by category
        JTabbedPane tabbing = new JTabbedPane();
        tabbing.addTab("Controls", controlPanel);
        tabbing.addTab("World Creator", worldEditPanel);

        // orient the control panel to the left and worldview to occupy the rest of the space
        pane.add(tabbing, BorderLayout.WEST);
        pane.add(worldView, BorderLayout.CENTER);

        // set our default closing operation
        root.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    // pack and display the root panel
    public void display(){
        root.pack();
        root.setLocationRelativeTo(null);
        root.setVisible(true);
    }

    // returns the delay (we subtract the max from current so that we can flip from 0ms - 100ms to 100ms - 0ms)
    public int getDelay(){
        return (speedSlider.getMaximum() * 100) - (speedSlider.getValue() * 100);
    }

}
