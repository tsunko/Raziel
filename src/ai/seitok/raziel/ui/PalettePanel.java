package ai.seitok.raziel.ui;

import ai.seitok.raziel.Sprites;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

public class PalettePanel extends JPanel {

    private List<JComponent> buttons;
    private JComponent selected;

    private final MouseListener clickListener = (MouseButtonPressedListener)(event) -> {
        if(event.getButton() == MouseEvent.BUTTON1){
            buttons.forEach(x -> x.setBorder(null));
            selected = (JComponent)event.getComponent();
            selected.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    };

    public PalettePanel(){
        setLayout(new GridLayout(2, 2));

        buttons = new ArrayList<>();

        JLabel razielButton = new JLabel(new ImageIcon(Sprites.getRobotSprite().getScaledInstance(64, 64, Image.SCALE_FAST)));
        razielButton.setToolTipText("Place Raziel spawn point");
        razielButton.addMouseListener(clickListener);
        razielButton.setName("razielButton");

        JLabel coinButton = new JLabel(new ImageIcon(Sprites.getCoinSprite(0).getScaledInstance(64, 64, Image.SCALE_FAST)));
        coinButton.setToolTipText("Left click: Place/add coin on tile. Right click: Remove coin on tile.");
        coinButton.addMouseListener(clickListener);
        coinButton.setName("coinButton");

        JComponent rightWallButton = new JPanel(){

            @Override
            public void paint(Graphics g){
                super.paint(g);

                int widthOffset = getWidth()/8;
                if(getBorder() != null){
                    getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
                }

                g.setColor(Color.BLACK);
                g.fillRect(getWidth()/2 - 2, getHeight()/2 - 2, 4, 4);
                g.fillRect(getWidth() - widthOffset, widthOffset/2, 2, getHeight() - widthOffset);
            }

        };
        rightWallButton.setToolTipText("Left click: Adds a right wall on tile. Right click: Remove right wall on tile.");
        rightWallButton.addMouseListener(clickListener);
        rightWallButton.setName("rightWallButton");

        JComponent bottomWallButton = new JPanel(){

            @Override
            public void paint(Graphics g){
                super.paint(g);

                int heightOffset= getHeight()/8;
                if(getBorder() != null){
                    getBorder().paintBorder(this, g, 0, 0, getWidth(), getHeight());
                }

                g.setColor(Color.BLACK);
                g.fillRect(getWidth()/2 - 2, getHeight()/2 - 2, 4, 4);
                g.fillRect(heightOffset/2, getHeight() - heightOffset, getWidth() - heightOffset, 2);
            }

        };
        bottomWallButton.setToolTipText("Left click: Adds a bottom wall on tile. Right click: Remove bottom wall on tile.");
        bottomWallButton.addMouseListener(clickListener);
        bottomWallButton.setName("bottomWallButton");

        buttons.add(razielButton);
        buttons.add(coinButton);
        buttons.add(rightWallButton);
        buttons.add(bottomWallButton);
        buttons.forEach(this::add);
    }

    public boolean hasSelection(){
        return selected != null;
    }

    public String getSelectedName(){
        return selected.getName();
    }

    private interface MouseButtonPressedListener extends MouseListener {

        public void mousePressed(MouseEvent e);

        @Override
        public default void mouseClicked(MouseEvent e){}
        @Override
        public default void mouseReleased(MouseEvent e){}
        @Override
        public default void mouseEntered(MouseEvent e){}
        @Override
        public default void mouseExited(MouseEvent e){}

    }

}
