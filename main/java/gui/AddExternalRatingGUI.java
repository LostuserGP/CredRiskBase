package gui;

import com.github.lgooddatepicker.components.DatePicker;
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

public class AddExternalRatingGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private DatePicker datePicker;
    private JComboBox comboBoxCounterparty;
    private JComboBox comboBoxRatingValues;
    private JComboBox comboBoxRatingAgency;
    private String counterparty;
    private Database db;
    private GUI gui;

    private static AddExternalRatingGUI ourInstance = new AddExternalRatingGUI();

    public static AddExternalRatingGUI getInstance() {
        return ourInstance;
    }

    public static void show(String counterparty) {
        show(counterparty, null);
    }

    public static void show(String counterparty, GUI gui) {
        AddExternalRatingGUI ourGui = AddExternalRatingGUI.getInstance();
        ourGui.gui = gui;
        ourGui.counterparty = counterparty;
        if (counterparty != null) {
            try {
                ourGui.comboBoxCounterparty.setSelectedItem(counterparty);
                ourGui.comboBoxCounterparty.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ourGui.comboBoxCounterparty.setSelectedIndex(0);
            ourGui.comboBoxCounterparty.setEnabled(true);
        }
        ourGui.clear();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private void clear() {
        comboBoxRatingValues.setSelectedIndex(0);
        comboBoxRatingAgency.setSelectedIndex(0);
    }

    private AddExternalRatingGUI() {
        drawGUI();
        initialize();
        pack();
    }

    private void drawGUI() {
        setContentPane(contentPane);
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
        addExternalRating();
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
        setTitle("New External Rating");
        try {
            this.db = Database.getInstance();
            comboBoxCounterparty.addItem("");
            comboBoxRatingValues.addItem("");
            comboBoxRatingAgency.addItem("");

            ResultSet rs;
            Statrs srs = db.getCounterparties();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxCounterparty.addItem(rs.getString("Name"));
            }
            srs.close();

            srs = db.getRatingValues();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxRatingValues.addItem(rs.getString("Name"));
            }
            srs.close();

            srs = db.getRatingAgencies();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxRatingAgency.addItem(rs.getString("Name"));
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

    private void addExternalRating() {
        try {
            db.createExternalRatingCounterparty(
                    (String) comboBoxRatingValues.getSelectedItem(),
                    ((String) comboBoxRatingAgency.getSelectedItem()).replace("'", "''"),
                    datePicker.getDate(),
                    (String) comboBoxCounterparty.getSelectedItem());
            this.setVisible(false);
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        datePicker = Helper.createDatePicker();
    }

}
