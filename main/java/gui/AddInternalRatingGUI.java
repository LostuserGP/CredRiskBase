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
import java.util.Calendar;
import java.util.Date;

public class AddInternalRatingGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxCounterparty;
    private JComboBox comboBoxRatingValues;
    private JComboBox comboBoxRatingValuesWithotCountry;
    private JComboBox comboBoxRiskClass;
    private JComboBox comboBoxFS;
    private DatePicker datePicker;
    private JTextField analystTextField;
    private JCheckBox isConservativeCheckBox;
    private JTextArea textArea;
    private Database db;
    private String counterparty;
    private GUI gui;

    private static AddInternalRatingGUI ourInstance = new AddInternalRatingGUI();

    public static AddInternalRatingGUI getInstance() {
        return ourInstance;
    }

    public static void show(String counterparty) {
        show(counterparty, null);
    }

    public static void show(String counterparty, GUI gui) {
        AddInternalRatingGUI ourGui = AddInternalRatingGUI.getInstance();
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
        comboBoxRatingValuesWithotCountry.setSelectedIndex(0);
        comboBoxRiskClass.setSelectedIndex(0);
        comboBoxFS.setSelectedIndex(0);
        textArea.setText("");
    }

    private AddInternalRatingGUI() {
        drawGUI();
        initialize();
        pack();
    }

    private void drawGUI() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        textArea.setFont(Helper.outputFont);

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
        addInternalRating();
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
        setTitle("New Internal Rating");
        try {
            this.db = Database.getInstance();
            analystTextField.setText(System.getProperty("user.name"));
            comboBoxRatingValues.addItem("");
            comboBoxRatingValuesWithotCountry.addItem("");
            comboBoxRiskClass.addItem("");
            comboBoxCounterparty.addItem("");

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
                String value;
                value = rs.getString("Name");
                comboBoxRatingValues.addItem(value);
                comboBoxRatingValuesWithotCountry.addItem(value);
            }
            srs.close();

            srs = db.getRiskClasses();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxRiskClass.addItem(rs.getString("Name"));
            }
            srs.close();

            comboBoxCounterparty.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        updateFSComboBox();
                    } catch (Exception e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }

                }
            });
            updateFSComboBox();

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    private void updateFSComboBox() throws Exception {
        comboBoxFS.removeAllItems();
        comboBoxFS.addItem("");
        //if ((String) comboBoxCounterparty.getSelectedItem() != null || !(((String) comboBoxCounterparty.getSelectedItem()).equals(""))) {
        if (comboBoxCounterparty.getSelectedItem() != null || !(comboBoxCounterparty.getSelectedItem().equals(""))) {
            Statrs srs = db.getFSbyCounterpartyId(db.getCounterpartyIDbyName((String) comboBoxCounterparty.getSelectedItem()));
            ResultSet rs = srs.getRs();
            while (rs.next())
                comboBoxFS.addItem(rs.getInt("ID") + " | " + createDate(rs.getDate("Date")) + " | " + rs.getString("Standards"));
            srs.close();
        }
    }

    private String createDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        String sqlDate = year + "-" + (month < 10 ? "0" + month : month + "") + "-" + (day < 10 ? "0" + day : day + "");
        return sqlDate;
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        datePicker = Helper.createDatePicker();
    }

    private void addInternalRating() {
        try {
            db.createInternalRating(
                    (String) comboBoxRatingValues.getSelectedItem(),
                    (String) comboBoxRatingValuesWithotCountry.getSelectedItem(),
                    (String) comboBoxRiskClass.getSelectedItem(),
                    isConservativeCheckBox.isSelected(),
                    (String) comboBoxFS.getSelectedItem(),
                    datePicker.getDate(),
                    (String) comboBoxCounterparty.getSelectedItem(), analystTextField.getText(),
                    textArea.getText());
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
