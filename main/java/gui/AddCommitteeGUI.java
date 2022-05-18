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

public class AddCommitteeGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxCounterparty;
    private JComboBox comboBoxStatus;
    private JComboBox comboBoxLimitations;
    private JComboBox comboBoxGroup;
    private DatePicker datePicker;
    private JTextArea textArea;
    private Database db;
    private String counterparty;
    private GUI gui;

    private static AddCommitteeGUI ourInstance = new AddCommitteeGUI();

    public static AddCommitteeGUI getInstance() {
        return ourInstance;
    }

    public static void show(String counterparty) {
        show(counterparty, null);
    }

    public static void show(String counterparty, GUI gui) {
        AddCommitteeGUI ourGui = AddCommitteeGUI.getInstance();
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

    private AddCommitteeGUI() {
        drawGUI();
        initialize();
        pack();
        setLocationRelativeTo(gui);
    }

    private void clear() {
        comboBoxLimitations.setSelectedIndex(0);
        comboBoxStatus.setSelectedIndex(0);
        comboBoxGroup.setSelectedIndex(0);
        datePicker.setDateToToday();
        textArea.setText("");
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

        setDefaultCloseOperation(HIDE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void initialize() {
        setTitle("New Committee");
        try {
            Statrs srs;
            ResultSet rs;
            this.db = Database.getInstance();
            comboBoxLimitations.addItem("");
            comboBoxCounterparty.addItem("");
            comboBoxStatus.addItem("");
            comboBoxGroup.addItem("");

            srs = db.getCounterparties();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxCounterparty.addItem(rs.getString("Name"));
            }
            srs.close();

            srs = db.getCommitteeStatuses();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxStatus.addItem(rs.getString("Name"));
            }
            srs.close();

            srs = db.getCommitteeLimitations();
            rs = srs.getRs();
            while (rs.next()) {
                String value;
                value = rs.getString("Name");
                comboBoxLimitations.addItem(value);
            }
            srs.close();

            srs = db.getCounterpartyGroups();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxGroup.addItem(rs.getString("Name"));
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
        addCommittee();
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

    private void addCommittee() {
        try {
            db.createCommittee(
                    (String) comboBoxStatus.getSelectedItem(),
                    (String) comboBoxLimitations.getSelectedItem(),
                    (String) comboBoxGroup.getSelectedItem(),
                    datePicker.getDate(),
                    (String) comboBoxCounterparty.getSelectedItem(),
                    textArea.getText());
            this.setVisible(false);
            //JOptionPane.showMessageDialog(new JFrame(), "Done!");
            Helper.showDone();
        } catch (Exception ex) {
            Object[] options = {"Ok", "Copy to Clipboard and close"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ex.getMessage(), "Exception message", JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]);
            DebugGUI.getInstance("Error in Module addCommittee");

            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(ex)), null);

            ex.printStackTrace();
        }
    }

    private void createUIComponents() {
        datePicker = Helper.createDatePicker();
    }

}
