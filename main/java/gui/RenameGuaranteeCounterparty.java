package gui;

import GuarantiesTool.GuarantiesList;
import database.Database;
import util.Helper;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class RenameGuaranteeCounterparty extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField currentNameField;
    private JTextField newNameField;
    private JTextField searchNameField;
    private JList counterpartiesList;
    private DefaultListModel listModel;
    private Database db;
    private GUI gui;

    private static RenameGuaranteeCounterparty ourInstance = new RenameGuaranteeCounterparty();

    public static RenameGuaranteeCounterparty getInstance() {
        ourInstance.setVisible(true);
        return ourInstance;
    }

    public static void show(String oldName, GUI gui) {
        RenameGuaranteeCounterparty ourGui = RenameGuaranteeCounterparty.getInstance();
        ourGui.currentNameField.setText(oldName);
        ourGui.gui = gui;
        ourGui.reload();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setVisible(true);
    }

    public void reload() {
        fillTheList();
        newNameField.setText("");
    }

    private RenameGuaranteeCounterparty() {
        drawGUI();
        pack();
    }

    private void drawGUI() {
        db = Database.getInstance();
        setContentPane(contentPane);
        //setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        //listModel = counterpartiesList.getModel();
        listModel = new DefaultListModel();
        counterpartiesList.setModel(listModel);

        counterpartiesList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                chooseByClick(evt);
            }
        });

        searchNameField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillTheList();
            }
        });

        searchNameField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (searchNameField.getText().length() > 2) fillTheList();
            }
        });

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (renameCounterparty()) setVisible(false);
            }
        });

        refresh();
    }

    private boolean renameCounterparty() {
        String oldName = currentNameField.getText();
        String newName = newNameField.getText();
        if (newName.length() > 1) {
            GuarantiesList gl = GuarantiesList.getInstance();
            gl.renameCounterparty(oldName, newName);
            return true;
        } else {
            Helper.showMsg("Имя контрагента должно быть длиннее одного символа");
            return false;
        }
    }

    private void chooseByClick(MouseEvent evt) {
        if (evt.getClickCount() >= 1) {
            String newName = (String) counterpartiesList.getSelectedValue();
            newNameField.setText(newName);
        }
    }

    private void fillTheList() {
        try {
            listModel.removeAllElements();
            ArrayList<String> values = db.searchForCounterparties(searchNameField.getText());
            for (String string : values) {
                listModel.addElement(string);
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void onCancel() {
        setVisible(false);
    }

    public void refresh() {
        fillTheList();
    }

}
