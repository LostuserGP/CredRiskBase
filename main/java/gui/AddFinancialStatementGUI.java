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

public class AddFinancialStatementGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea commentsTextArea;
    private JComboBox comboBoxCounterparty;
    private DatePicker datePicker;
    private JComboBox comboBoxStandards;
    private Database db;
    private String counterparty;
    private GUI gui;

    private static AddFinancialStatementGUI ourInstance = new AddFinancialStatementGUI();

    public static AddFinancialStatementGUI getInstance() {
        return ourInstance;
    }

    public static void show(String counterparty) {
        show(counterparty, null);
    }

    public static void show(String counterparty, GUI gui) {
        AddFinancialStatementGUI ourGui = AddFinancialStatementGUI.getInstance();
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
        comboBoxStandards.setSelectedIndex(0);
        commentsTextArea.setText("");
    }

    private AddFinancialStatementGUI() {
        drawGUI();
        initialize();
        pack();
    }

    private void drawGUI() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        commentsTextArea.setFont(Helper.outputFont);

        comboBoxStandards.setModel(new DefaultComboBoxModel<String>(new String[] {"", "IFRS", "National"}));

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

    private void initialize() {
        setTitle("New Financial Statement");
        try {
            this.db = Database.getInstance();
            Statrs srs = db.getCounterparties();
            ResultSet rs = srs.getRs();
            comboBoxCounterparty.addItem("");
            while (rs.next()) {
                comboBoxCounterparty.addItem(rs.getString("Name"));
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
        addCounterparty();
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

    private void addCounterparty() {
        try {
            db.createFS(
                    (String) comboBoxCounterparty.getSelectedItem(),
                    datePicker.getDate(),
                    comboBoxStandards.getSelectedItem().toString(),
                    commentsTextArea.getText());
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

    private void createUIComponents() {
        // TODO: place custom component creation code here
        datePicker = Helper.createDatePicker();
    }
}
