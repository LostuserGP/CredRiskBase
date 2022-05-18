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
import java.text.DecimalFormat;
import java.time.LocalDate;

public class AddGuaranteeGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxCounterparty;
    private JComboBox comboBoxGuarantor;
    private JComboBox comboBoxGuaranteeType;
    private JComboBox comboBoxCurrency;
    private DatePicker datePicker;
    private DatePicker datePickerTo;
    private JFormattedTextField amountTextField;
    private JTextField guaranteeNumberField;
    private Database db;
    private String counterparty;
    private int guaranteeID;
    private GUI gui;

    private static AddGuaranteeGUI ourInstance = new AddGuaranteeGUI();

    public static AddGuaranteeGUI getInstance() {
        return ourInstance;
    }

    public static void show(String counterparty) {
        show(counterparty, null);
    }

    public static void show(String counterparty, GUI gui) {
        AddGuaranteeGUI ourGui = AddGuaranteeGUI.getInstance();
        ourGui.gui = gui;
        ourGui.counterparty = counterparty;
        ourGui.guaranteeID = -1;
        ourInstance.setTitle("New Guarantee");
        ourInstance.buttonOK.setText("Create");
        ourGui.clear();
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
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    public static void show(int guaranteeID, GUI gui) {
        AddGuaranteeGUI ourGui = AddGuaranteeGUI.getInstance();
        ourGui.gui = gui;
        ourGui.counterparty = null;
        ourGui.guaranteeID = guaranteeID;
        ourGui.editMode();
        ourGui.comboBoxCounterparty.setEnabled(false);
        ourInstance.setTitle("Edit Guarantee");
        ourInstance.buttonOK.setText("Edit");
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private void clear() {
        comboBoxCounterparty.setSelectedItem(counterparty);
        comboBoxGuarantor.setSelectedIndex(0);
        guaranteeNumberField.setText("");
        comboBoxGuaranteeType.setSelectedIndex(0);
        amountTextField.setText("");
        comboBoxCurrency.setSelectedIndex(0);
        datePicker.setDateToToday();
        datePickerTo.setDateToToday();
    }

    private AddGuaranteeGUI() {
        drawGUI();
        initialize();
        pack();
    }

    private void drawGUI() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (guaranteeID>0) {
                    onOKupdate();
                } else {
                    onOKnew();
                }
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        setDefaultCloseOperation(HIDE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void setAmountEnabled() {
        // add your code here
        if (comboBoxGuaranteeType.getSelectedIndex() == 0) {
            amountTextField.setText("" + 0);
            amountTextField.setEnabled(false);
        }
        if (comboBoxGuaranteeType.getSelectedIndex() == 1) amountTextField.setEnabled(true);
    }

    private void reset() {
        comboBoxCurrency.setSelectedIndex(0);
        comboBoxGuaranteeType.setSelectedIndex(0);
        comboBoxCurrency.setSelectedIndex(0);
        comboBoxGuarantor.setSelectedItem("");
        amountTextField.setValue(0);
        guaranteeNumberField.setText("");
    }

    private void onCancel() {
        // add your code here if necessary
        setVisible(false);
    }

    private void onOKnew() {
        // add your code here
        addGuarantee();
        if (gui != null) {
            try {
                gui.reload();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private void onOKupdate() {
        // add your code here
        updateGuarantee();
        if (gui != null) {
            try {
                gui.reload();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private void initialize() {

        try {
            this.db = Database.getInstance();
            ResultSet rs;
            Statrs srs = db.getCounterparties();
            rs = srs.getRs();
            comboBoxCounterparty.addItem("");
            comboBoxGuarantor.addItem("");
            while (rs.next()) {
                String value = rs.getString("Name");
                comboBoxCounterparty.addItem(value);
                comboBoxGuarantor.addItem(value);
            }
            srs.close();

            srs = db.getCurrencies();
            rs = srs.getRs();
            comboBoxCurrency.addItem("");
            while (rs.next()) {
                comboBoxCurrency.addItem(rs.getString("Name"));
            }
            srs.close();

            srs = db.getGuaranteeTypes();
            rs = srs.getRs();
            comboBoxGuaranteeType.addItem("");
            while (rs.next()) {
                comboBoxGuaranteeType.addItem(rs.getString("Name"));
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

    private void addGuarantee() {
        try {
            db.createGuarantee(
                    (String)comboBoxCounterparty.getSelectedItem(),
                    (String)comboBoxGuarantor.getSelectedItem(),
                    (String)comboBoxGuaranteeType.getSelectedItem(),
                    (String)comboBoxCurrency.getSelectedItem(),
                    (comboBoxGuaranteeType.getSelectedIndex() == 0) ? -1 : ((Number) amountTextField.getValue()).longValue(),
                    datePicker.getDate(),
                    datePickerTo.getDate(),
                    guaranteeNumberField.getText()
            );
            this.setVisible(false);
            Helper.showDone();
        } catch (Exception ex) {
            Object[] options = {"Ok", "Copy to Clipboard and close"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ex.getMessage(), "Exception message", JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0] );
            Helper.debug("error");

            Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(ex)), null);

            ex.printStackTrace();
        }

    }

    private void updateGuarantee() {
        try {
            db.updateGuarantee(guaranteeID,
                    (String)comboBoxCounterparty.getSelectedItem(),
                    (String)comboBoxGuarantor.getSelectedItem(),
                    (String)comboBoxGuaranteeType.getSelectedItem(),
                    (String)comboBoxCurrency.getSelectedItem(),
                    (comboBoxGuaranteeType.getSelectedIndex() == 0) ? -1 : ((Number) amountTextField.getValue()).longValue(),
                    datePicker.getDate(),
                    datePickerTo.getDate(),
                    guaranteeNumberField.getText()
            );
            this.setVisible(false);
            Helper.showDone();
        } catch (Exception ex) {
            Object[] options = {"Ok", "Copy to Clipboard and close"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ex.getMessage(), "Exception message", JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0] );
            Helper.debug("error");

            Clipboard clpbrd = Toolkit.getDefaultToolkit ().getSystemClipboard ();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(ex)), null);

            ex.printStackTrace();
        }

    }

    private void editMode() {
        setTitle("Edit Guarantee");
        try {
            Statrs srs = db.getGuarantyByID(guaranteeID);
            ResultSet rs = srs.getRs();
            rs.next();
            comboBoxCounterparty.setSelectedItem(rs.getString("Counterparty"));
            comboBoxGuarantor.setSelectedItem(rs.getString("Guarant"));
            guaranteeNumberField.setText(rs.getString("Number"));
            comboBoxGuaranteeType.setSelectedItem(rs.getString("Type"));
            amountTextField.setValue(rs.getFloat("Amount"));
            comboBoxCurrency.setSelectedItem(rs.getString("Currency"));
            String startDate = rs.getString("StartDate");
            String endDate = rs.getString("EndDate");
            if (startDate != null) {
                datePicker.setDate(LocalDate.parse(startDate));
            } else datePicker.setDateToToday();
            if (endDate != null) {
                datePickerTo.setDate(LocalDate.parse(endDate));
            } else datePickerTo.setDateToToday();
            srs.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        amountTextField = new JFormattedTextField(new DecimalFormat("###,###,##0"));
        //amountTextField.setEnabled(false);
        amountTextField.setValue(0.00F);

        datePicker = Helper.createDatePicker();

        datePickerTo = Helper.createDatePicker();

        //datePickerX = Helper.createDatePicker();

    }
}
