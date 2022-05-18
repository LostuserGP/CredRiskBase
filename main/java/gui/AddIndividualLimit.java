package gui;

import database.Database;
import database.Statrs;
import org.apache.commons.lang3.exception.ExceptionUtils;
import util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddIndividualLimit extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxDepartment;
    private JComboBox comboBoxGuarantorRating;
    private JTextField textFieldAnalyst;
    private JTextArea textAreaComment;
    private JTextField textFieldIndividualLimit;
    private JTextField textFieldGuarantorLimit;
    private Database db;
    private int counterpartyId;
    private GUI gui;

    private static AddIndividualLimit ourInstance = new AddIndividualLimit();

    public static AddIndividualLimit getInstance() {
        return ourInstance;
    }

    public static void show(int counterpartyId) {
        show(counterpartyId, null);
    }

    public static void show(int counterpartyId, GUI gui) {
        AddIndividualLimit ourGui = AddIndividualLimit.getInstance();
        ourGui.counterpartyId = counterpartyId;
        ourGui.gui = gui;
        ourGui.clear();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private void clear() {
        comboBoxDepartment.setSelectedIndex(0);
        comboBoxGuarantorRating.setSelectedIndex(0);
        textAreaComment.setText("");
        textFieldIndividualLimit.setText("0");
        textFieldGuarantorLimit.setText("0");
    }

    private AddIndividualLimit() {
        drawGUI();
        initialize();
        pack();
    }

    private void drawGUI() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        textAreaComment.setFont(Helper.outputFont);

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

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

    }

    private void onOK() {
        // add your code here
        addIndividualLimit();
        if (gui != null) {
            try {
                gui.reload();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private void onCancel() {
        // add your code here if necessary
        setVisible(false);
    }

    private void initialize() {
        setTitle("New Internal Limit");
        try {
            this.db = Database.getInstance();
            textFieldAnalyst.setText(System.getProperty("user.name"));
            comboBoxDepartment.addItem("");
            comboBoxDepartment.addItem("DEP");
            comboBoxDepartment.addItem("DTO");
            comboBoxDepartment.addItem("DEN");
            comboBoxGuarantorRating.addItem("");

            ResultSet rs;
            Statrs srs = db.getRatingValues();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxGuarantorRating.addItem(rs.getString("Name"));
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

    private void addIndividualLimit() {
        try {
            int cl = Integer.parseInt(textFieldIndividualLimit.getText());
            int gl = Integer.parseInt(textFieldGuarantorLimit.getText());
            db.addIndividualLimit(
                    (String) comboBoxDepartment.getSelectedItem(),
                    cl,
                    (String) textAreaComment.getText(),
                    (String) comboBoxGuarantorRating.getSelectedItem(),
                    gl,
                    counterpartyId);
            this.dispose();
            Helper.showDone();
        } catch (Exception ex) {
            Object[] options = {"Ok", "Copy to Clipboard and close"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ex.getMessage(), "Exception message", JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]);
            Helper.debug("error");

            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(ex)), null);

            ex.printStackTrace();
        }

    }

}
