package gui;

import database.Database;
import database.Statrs;
import org.apache.commons.lang3.exception.ExceptionUtils;
import util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddRatingLimit extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox groupComboBox;
    private JComboBox ratingComboBox;
    private JTextField groupLimitTextField;
    private JTextField bankLimitTextField;
    private JButton deleteButton;
    private int id;
    private Database db;
    private GUI gui;

    private static AddRatingLimit ourInstance = new AddRatingLimit();

    public static AddRatingLimit getInstance() {
        return ourInstance;
    }

    public static void show(int id, GUI gui) {
        AddRatingLimit ourGui = AddRatingLimit.getInstance();
        ourGui.gui = gui;
        ourGui.id = id;
        ourGui.initialize();
        ourGui.ratingRefill();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private AddRatingLimit() {
        drawGUI();
        pack();
        setLocationRelativeTo(gui);
    }

    private void  drawGUI() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onDelete();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initialize() {
        setTitle("New Group Limit");
        try {
            this.db = Database.getInstance();
            addGroupItems();
            groupLimitTextField.setText("0");
            bankLimitTextField.setText("0");
            ratingComboBox.addItem("");

            Statrs srs = db.getRatingValues();
            ResultSet rs = srs.getRs();
            while (rs.next()) {
                ratingComboBox.addItem(rs.getString("Name"));
            }
            srs.close();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void addGroupItems() {
        groupComboBox.removeAllItems();
        groupComboBox.addItem("");
        groupComboBox.addItem("1");
        groupComboBox.addItem("2");
        groupComboBox.addItem("3");
        groupComboBox.addItem("4");
        groupComboBox.addItem("5");
    }

    private void onOK() {
        // add your code here
        addLimit();
        setVisible(false);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onDelete() {
        // add your code here if necessary
        //deleteApprovedGuarantee();
    }

    private void addLimit() {
        try {
            int groupNumber = Integer.parseInt(groupComboBox.getSelectedItem().toString());
            int ratingID = db.getRatingIdByValue(ratingComboBox.getSelectedItem().toString());
            int groupLimit = Integer.parseInt(groupLimitTextField.getText());
            int bankLimit = Integer.parseInt(bankLimitTextField.getText());

            if (id == -1) {
                db.createRatingLimit(groupNumber, ratingID, groupLimit, bankLimit);
            } else {
                //db.updateRatingLimit(id, groupNumber, ratingID, groupLimit, bankLimit);
            }
            gui.reload();
            this.setVisible(false);
            Helper.showDone();
        } catch (Exception ex) {
            Object[] options = {"Ok", "Copy to Clipboard and close"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ex.getMessage(), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]);
            DebugGUI.getInstance("Error in Module addLimit");
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(ex)), null);
            ex.printStackTrace();
        }
    }

    private void ratingRefill() {

    }
}
