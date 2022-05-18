package gui;

import com.github.lgooddatepicker.components.DatePicker;
import database.Database;
import org.apache.commons.lang3.exception.ExceptionUtils;
import util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.time.LocalDate;

public class AddCurrencyRates extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField usdTextField;
    private JTextField eurTextField;
    private JTextField gbpTextField;
    private JButton deleteButton;
    private DatePicker datePicker;
    private Database db;
    private GUI gui;

    private static AddCurrencyRates ourInstance = new AddCurrencyRates();

    public static AddCurrencyRates getInstance() {
        return ourInstance;
    }

    public static void show(GUI gui) {
        AddCurrencyRates ourGui = AddCurrencyRates.getInstance();
        ourGui.gui = gui;
        ourGui.initialize();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private AddCurrencyRates() {
        drawGUI();
        pack();
        setLocationRelativeTo(gui);
    }

    private void drawGUI() {
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
        setTitle("New Exchange Rates");
        eurTextField.setText("0");
        gbpTextField.setText("0");
        usdTextField.setText("0");
    }

    private void onOK() {
        // add your code here
        addRates();
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

    private void addRates() {
        try {
            float usd = Float.parseFloat(usdTextField.getText().replace(",","."));
            float eur = Float.parseFloat(eurTextField.getText().replace(",","."));
            float gbp = Float.parseFloat(gbpTextField.getText().replace(",","."));
            LocalDate reportDate = datePicker.getDate();

            db = Database.getInstance();
            db.createRate("USD", "RUB", reportDate, usd);
            db.createRate("EUR", "RUB", reportDate, eur);
            db.createRate("GBP", "RUB", reportDate, gbp);
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

    private void createUIComponents() {
        datePicker = Helper.createDatePicker();
    }

}
