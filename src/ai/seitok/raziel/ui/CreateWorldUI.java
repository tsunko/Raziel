package ai.seitok.raziel.ui;

import ai.seitok.raziel.world.World;

import javax.swing.*;
import java.awt.*;

public class CreateWorldUI extends JDialog {

    private static CreateWorldUI dialog;
    private static World world;

    public static World createDialog(Frame root){
        dialog = new CreateWorldUI(root);
        dialog.setModal(true);
        dialog.setVisible(true);
        return world;
    }

    private CreateWorldUI(Frame root){
        super(root);
        setTitle("Creating World");

        Container container = getContentPane();
        GroupLayout layout = new GroupLayout(container);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        container.setLayout(layout);

        JLabel nameLabel = new JLabel("Name of world:");
        JTextField worldNameInput = new JTextField();

        JLabel widthLabel = new JLabel("Width:");
        JSpinner widthSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        JLabel heightLabel = new JLabel("Height:");
        JSpinner heightSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));

        JButton finishButton = new JButton("Create");
        finishButton.addActionListener(event -> {
            if(worldNameInput.getText().isEmpty()){
                JOptionPane.showMessageDialog(this, "Give your world a name!", "No name given!", JOptionPane.ERROR_MESSAGE);
                return;
            }

            System.out.println(widthSpinner.getValue());
            world = new World(worldNameInput.getText(), (int)widthSpinner.getValue(), (int)heightSpinner.getValue());
            dialog.setVisible(false);
        });

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(event -> {
            world = null;
            dialog.setVisible(false);
        });

        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addComponent(nameLabel)
                        .addComponent(worldNameInput)
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(widthLabel)
                                        .addComponent(widthSpinner)
                                        .addComponent(heightLabel)
                                        .addComponent(heightSpinner)
                        )
                        .addGroup(
                                layout.createSequentialGroup()
                                        .addComponent(finishButton)
                                        .addComponent(cancelButton)
                        )
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addComponent(nameLabel)
                        .addComponent(worldNameInput)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED,
                                10, Short.MAX_VALUE)
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(widthLabel)
                                        .addComponent(widthSpinner)
                                        .addComponent(heightLabel)
                                        .addComponent(heightSpinner)
                        )
                        .addGroup(
                                layout.createParallelGroup()
                                        .addComponent(finishButton)
                                        .addComponent(cancelButton)
                        )
        );

        pack();
        setLocationRelativeTo(null);
    }

}
