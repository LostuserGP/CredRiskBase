package gui;

import com.github.lgooddatepicker.components.DatePicker;
import database.Database;
import database.Statrs;
import org.apache.commons.lang3.exception.ExceptionUtils;
import util.AutoCompleteJComboBoxer;
import util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

public class AddApprovedGuaranteeGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox counterpartyComboBox;
    private JComboBox bankComboBox;
    private JFormattedTextField amountTextField;
    private JComboBox currencyComboBox;
    private JButton deleteButton;
    private JComboBox guaranteeNameComboBox;
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;
    private DatePicker finishDatePicker;
    private int id;
    private Database db;
    private GUI gui;

    private static AddApprovedGuaranteeGUI ourInstance = new AddApprovedGuaranteeGUI();

    public static AddApprovedGuaranteeGUI getInstance() {
        return ourInstance;
    }

    public static void show(int id, GUI gui) {
        System.out.println("Call method AddApprovedGuaranteeGUI.show with id=" + id);
        AddApprovedGuaranteeGUI ourGui = AddApprovedGuaranteeGUI.getInstance();
        ourGui.gui = gui;
        ourGui.id = id;
        //ourGui.clear();
        ourGui.initialize();
        ourGui.approvedGuaranteeRefill();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private AddApprovedGuaranteeGUI() {
        drawGUI();
        //initialize();
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

        startDatePicker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                endDatePicker.setDate(startDatePicker.getDate().plusMonths(1));
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
        setTitle("New Approved Guarantee");
        try {
            this.db = Database.getInstance();
            counterpartyComboBox.removeAllItems();
            counterpartyComboBox.addItem("");
            bankComboBox.removeAllItems();
            bankComboBox.addItem("");
            currencyComboBox.removeAllItems();
            currencyComboBox.addItem("");
            guaranteeNameComboBox.removeAllItems();
            guaranteeNameComboBox.addItem("");

            Statrs srs;
            ResultSet rs;
            srs = db.getCounterpartyRS();
            rs = srs.getRs();
            while (rs.next()) {
                counterpartyComboBox.addItem(rs.getString("Name"));
            }
            srs.close();
            new AutoCompleteJComboBoxer(counterpartyComboBox);

            srs = db.getBanks();
            rs = srs.getRs();
            while (rs.next()) {
                bankComboBox.addItem(rs.getString("Bank"));
            }
            srs.close();
            new AutoCompleteJComboBoxer(bankComboBox);

            srs = db.getCurrencies();
            rs = srs.getRs();
            while (rs.next()) {
                currencyComboBox.addItem(rs.getString("Name"));
            }
            srs.close();

            srs = db.getGuaranteeNames();
            rs = srs.getRs();
            while (rs.next()) {
                guaranteeNameComboBox.addItem(rs.getString("Name"));
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

    private void onOK() {
        // add your code here
        addApprovedGuarantee();
        setVisible(false);
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    private void onDelete() {
        // add your code here if necessary
        deleteApprovedGuarantee();
    }

    private void approvedGuaranteeRefill() {
        if (id != -1) {
            Statrs srs;
            ResultSet rs;
            ourInstance.setTitle("Edit Approved Guarantee");
            ourInstance.buttonOK.setText("Save");
            try {
                srs = db.getApprovedGuaranteeById(id);
                rs = srs.getRs();
                rs.next();
                Helper.debug("Reading inf for approved guarantee with id: " + id);
                System.out.println("Reading inf for approved guarantee with id: " + id);
                counterpartyComboBox.setSelectedItem(rs.getString("Counterparty"));
                bankComboBox.setSelectedItem(rs.getString("Bank"));
                startDatePicker.setDate(LocalDate.parse(rs.getString("AgreeFirstDate")));
                endDatePicker.setDate(LocalDate.parse(rs.getString("AgreeEndDate")));
                finishDatePicker.setDate(LocalDate.parse(rs.getString("GuaranteeEndDate")));
                amountTextField.setValue(rs.getDouble("Amount"));
                currencyComboBox.setSelectedItem(rs.getString("Currency"));
                guaranteeNameComboBox.setSelectedItem(rs.getString("GuaranteeName"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ourInstance.setTitle("New Approved Guarantee");
            ourInstance.buttonOK.setText("Create");
            counterpartyComboBox.setSelectedIndex(0);
            bankComboBox.setSelectedIndex(0);
            startDatePicker.setDateToToday();
            finishDatePicker.setDate(LocalDate.now().with(TemporalAdjusters.lastDayOfYear()));
            currencyComboBox.setSelectedIndex(0);
            guaranteeNameComboBox.setSelectedIndex(0);
        }
    }

    private void addApprovedGuarantee() {
            try {
                String counterpartyName = counterpartyComboBox.getSelectedItem().toString();
                String bankName = bankComboBox.getSelectedItem().toString();
                LocalDate agreeFirstDate = startDatePicker.getDate();
                LocalDate agreeEndDate = endDatePicker.getDate();
                LocalDate guaranteeEndDate = finishDatePicker.getDate();
                Double damount = Double.parseDouble(amountTextField.getValue().toString()) * 100d;
                long amount = damount.longValue();
                String currency = currencyComboBox.getSelectedItem().toString();
                String guaranteeName = guaranteeNameComboBox.getSelectedItem().toString();
                if (id == -1) {
                    db.createApprovedGuarantee(counterpartyName, bankName, agreeFirstDate, agreeEndDate, guaranteeEndDate, amount, currency, guaranteeName);
                } else {
                    db.updateApprovedGuarantee(id, counterpartyName, bankName, agreeFirstDate, agreeEndDate, guaranteeEndDate, amount, currency, guaranteeName);
                }
                gui.reload();
                this.setVisible(false);
                //JOptionPane.showMessageDialog(new JFrame(), "Done!");
                Helper.showDone();
            } catch (Exception ex) {
                Object[] options = {"Ok", "Copy to Clipboard and close"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ex.getMessage(), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null,     //do not use a custom Icon
                        options,  //the titles of buttons
                        options[0]);
                DebugGUI.getInstance("Error in Module addApprovedGuarantee");

                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(ex)), null);

                ex.printStackTrace();
            }
    }

    private void deleteApprovedGuarantee() {
        try {
            db.deleteApprovedGuarantee(id);
            this.setVisible(false);
            gui.reload();
            Helper.showDone();
        } catch (Exception ex) {
            Object[] options = {"Ok", "Copy to Clipboard and close"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ex.getMessage(), "Exception message", JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]);
            DebugGUI.getInstance("Error in Module deleteApprovedGuarantee");

            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(ex)), null);

            ex.printStackTrace();
        }
    }

    private void createUIComponents() {
        startDatePicker = Helper.createDatePicker();
        endDatePicker = Helper.createDatePicker();
        finishDatePicker = Helper.createDatePicker();
        finishDatePicker.setDate(startDatePicker.getDate().
                with(TemporalAdjusters.lastDayOfYear()));
        amountTextField = new JFormattedTextField(Helper.getNumberFormat());
    }
}
