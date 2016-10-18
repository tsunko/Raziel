package ai.seitok.raziel.ui;

import ai.seitok.raziel.Robot;
import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SelectionUI extends JDialog {

    private static SelectionUI dialog;
    private static int value;

    private DefaultListModel<String> listModel;
    private JList<String> selection;

    public static int createDialog(List<Robot> list){
        dialog = new SelectionUI(list);
        dialog.setModal(true);
        dialog.setVisible(true);
        return value;
    }

    private SelectionUI(List<Robot> list){
        setTitle("Select an implementation to run...");

        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        listModel = new DefaultListModel<>();
        list.forEach(e -> listModel.addElement(e.getClass().getName()));
        selection = new JList<>(listModel);
        selection.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selection.setLayoutOrientation(JList.VERTICAL);
        selection.setPreferredSize(new Dimension(500, 500));
        selection.setSelectedIndex(0);

        JButton confirmButton = new JButton("Confirm Selection");
        confirmButton.addActionListener(event ->{
            value = selection.getSelectedIndex();
            dialog.setVisible(false);
        });
        container.add(confirmButton, BorderLayout.SOUTH);

        container.add(new JLabel("Select a robot implementation to use"), BorderLayout.NORTH);

        JScrollPane scrolly = new JScrollPane(selection);
        container.add(scrolly, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

}
