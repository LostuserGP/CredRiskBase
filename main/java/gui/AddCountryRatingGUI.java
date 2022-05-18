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

public class AddCountryRatingGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JComboBox comboBoxCountry;
    private JComboBox comboBoxRatingValues;
    private JComboBox comboBoxRatingAgency;
    private DatePicker datePicker;
    private Database db;
    private String country;
    private GUI gui;

    private static AddCountryRatingGUI ourInstance = new AddCountryRatingGUI();

    public static AddCountryRatingGUI getInstance() {
        return ourInstance;
    }

    public static void show(String country) {
        show(country, null);
    }

    public static void show(String country, GUI gui) {
        AddCountryRatingGUI ourGui = AddCountryRatingGUI.getInstance();
        ourGui.gui = gui;
        ourGui.country = country;
        if (country != null) {
            try {
                ourGui.comboBoxCountry.setSelectedItem(country);
                ourGui.comboBoxCountry.setEnabled(false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ourGui.comboBoxCountry.setSelectedIndex(0);
            ourGui.comboBoxCountry.setEnabled(true);
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

    private AddCountryRatingGUI() {
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

    private void initialize() {
        setTitle("New Country Rating");

        try {
            Statrs srs;
            ResultSet rs;
            this.db = Database.getInstance();
            comboBoxCountry.addItem("");
            comboBoxRatingValues.addItem("");
            comboBoxRatingAgency.addItem("");

            srs = db.getCountries();
            rs = srs.getRs();
            while (rs.next()) {
                comboBoxCountry.addItem(rs.getString("Name"));
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

    private void onOK() {
        addCountryRating();
        if (gui != null) {
            try {
                gui.reload();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    private void onCancel() {
        setVisible(false);
    }

    private void addCountryRating() {
        try {
            db.createCountryRating(
                    (String) comboBoxRatingValues.getSelectedItem(),
                    ((String) comboBoxRatingAgency.getSelectedItem()).replace("'", "''"),
                    datePicker.getDate(),
                    (String) comboBoxCountry.getSelectedItem());
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
