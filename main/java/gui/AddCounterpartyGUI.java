package gui;

import com.github.lgooddatepicker.components.DatePicker;
import database.Database;
import database.Statrs;
import org.apache.commons.lang3.exception.ExceptionUtils;
import util.Helper;

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class AddCounterpartyGUI extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldCPName;
    private JTextField textFieldShortName;
    private JComboBox comboBoxDonor;
    private JComboBox comboBoxCountryDom;
    private JComboBox comboBoxCountryRisk;
    private JComboBox comboBoxSector;
    private JTextField textFieldBloomberg;
    private JComboBox comboBoxPortfolio;
    private DatePicker datePicker;
    private JCheckBox intraGroupCheckBox;
    private JCheckBox monitoredCheckBox;
    private JTextPane textPane;
    private JCheckBox longTermCheckBox;
    private JCheckBox etpCheckBox;
    private JCheckBox efetCheckBox;
    private JComboBox gtcGroupComboBox;
    private JTextField textFieldDuns;
    private JCheckBox srkCheckBox;
    private JTextArea textAreaCauses;
    private JTextField textFieldSubsidiaryLimit;
    private JTextField textFieldUKZ;
    private int id;
    private Database db;
    private GUI gui;

    private static AddCounterpartyGUI ourInstance = new AddCounterpartyGUI();

    private AddCounterpartyGUI() {
        drawGUI();
        initialize();
        pack();
    }

    public static AddCounterpartyGUI getInstance() {
        return ourInstance;
    }

    public static void show(String name, GUI gui) {
        AddCounterpartyGUI ourGui = AddCounterpartyGUI.getInstance();
        ourGui.gui = gui;
        ourGui.id = -1;
        ourGui.counterpartyRefill();
        ourGui.textFieldCPName.setText(name);
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    public static void show(int id) {
        show(id, null);
    }

    public static void show(int id, GUI gui) {
        AddCounterpartyGUI ourGui = AddCounterpartyGUI.getInstance();
        ourGui.gui = gui;
        ourGui.id = id;
        ourGui.counterpartyRefill();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private void drawGUI() {
        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonOK);
        textPane.setFont(Helper.outputFont);

        textFieldShortName.setDocument(new JTextFieldLimit(15));

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

        setTitle(id == -1 ? "New Counterparty" : "Edit Counterparty");

        comboBoxCountryDom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    comboBoxCountryRisk.setSelectedIndex(comboBoxCountryDom.getSelectedIndex());
                } catch (Exception ex) {
                    //do nothing
                }
            }
        });

        buttonOK.setText(id == -1 ? "Create" : "Save");

    }

    private void onOK() {
        // add your code here
        if (textFieldCPName.getText().equals("") || textFieldShortName.getText().equals(""))
            JOptionPane.showMessageDialog(new JFrame(), "All mandatory fields (*) must be filled out");
        else {
            if (id == -1) {
                addCounterparty();
            } else {
                editCounterparty();
            }
        }
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

    private void initialize() {

        try {
            Statrs srs;
            ResultSet rs;
            this.db = Database.getInstance();
            srs = db.getCountries();
            rs = srs.getRs();
            comboBoxCountryDom.addItem("");
            comboBoxCountryRisk.addItem("");
            while (rs.next()) {
                String country;
                country = rs.getString("Name");
                comboBoxCountryDom.addItem(country);
                comboBoxCountryRisk.addItem(country);
            }
            srs.close();
            //comboBoxCountryDom.setSelectedItem("Germany");

            srs = db.getCounterparties();
            rs = srs.getRs();
            comboBoxDonor.addItem("");
            while (rs.next()) {
                String counterparty;
                counterparty = rs.getString("Name");
                comboBoxDonor.addItem(counterparty);
            }
            srs.close();

            srs = db.getSectors();
            rs = srs.getRs();
            comboBoxSector.addItem("");
            while (rs.next()) {
                String sector;
                sector = rs.getString("SectorName");
                comboBoxSector.addItem(sector);
            }
            srs.close();

            srs = db.getPortfolios();
            rs = srs.getRs();
            comboBoxPortfolio.addItem("");
            while (rs.next()) {
                String portfolio;
                portfolio = rs.getString("Name");
                comboBoxPortfolio.addItem(portfolio);
            }
            srs.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        gtcGroupComboBox.addItem("");
        gtcGroupComboBox.addItem("aaa");
        gtcGroupComboBox.addItem("bbb");
        gtcGroupComboBox.addItem("ccc");
    }

    private void counterpartyRefill() {
        if (id != -1) {
            Statrs srs;
            ResultSet rs;
            ourInstance.setTitle("Edit Counterparty");
            ourInstance.buttonOK.setText("Save");
            try {
                srs = db.getCounterpartyById(id);
                rs = srs.getRs();
                rs.next();
                Helper.debug("Reading inf for counterparty with id: " + id);
                textFieldCPName.setText(rs.getString("Name"));
                textFieldShortName.setText(rs.getString("ShortName"));
                comboBoxDonor.setSelectedItem(rs.getString("Donor"));
                comboBoxCountryDom.setSelectedItem(rs.getString("CountryOfDomicile"));
                comboBoxCountryRisk.setSelectedItem(rs.getString("CountryOfRisk"));
                comboBoxSector.setSelectedItem(rs.getString("Sector"));
                textFieldBloomberg.setText(rs.getString("BloombergTicker"));
                datePicker.setDate(LocalDate.parse(rs.getString("StartDate")));
                comboBoxPortfolio.setSelectedItem(rs.getString("Portfolio"));
                intraGroupCheckBox.setSelected(rs.getBoolean("IntraGroup"));
                monitoredCheckBox.setSelected(rs.getBoolean("ToBeMonitored"));
                longTermCheckBox.setSelected(rs.getBoolean("Longterm"));
                etpCheckBox.setSelected(rs.getBoolean("Etp"));
                efetCheckBox.setSelected(rs.getBoolean("Efet"));
                gtcGroupComboBox.setSelectedItem(rs.getString("Gtc"));
                textPane.setText(rs.getString("Comments"));
                textFieldDuns.setText(rs.getString("Duns"));
                textAreaCauses.setText(rs.getString("Causes"));
                textFieldSubsidiaryLimit.setText(rs.getString("SubsidiaryLimit"));
                textFieldUKZ.setText(rs.getString("UKZ"));
                srkCheckBox.setSelected(rs.getBoolean("isSRK"));
                srs.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            ourInstance.setTitle("New Counterparty");
            ourInstance.buttonOK.setText("Create");
            textFieldCPName.setText("");
            textFieldShortName.setText("");
            comboBoxDonor.setSelectedItem("");
            comboBoxCountryDom.setSelectedItem("");
            comboBoxCountryRisk.setSelectedItem("");
            comboBoxSector.setSelectedItem("");
            textFieldBloomberg.setText("");
            datePicker.setDateToToday();
            comboBoxPortfolio.setSelectedItem("");
            intraGroupCheckBox.setSelected(false);
            monitoredCheckBox.setSelected(false);
            textFieldDuns.setText("");
            textAreaCauses.setText("");
            srkCheckBox.setSelected(false);
            textFieldUKZ.setText("");
            textPane.setText("");
        }
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
        datePicker = Helper.createDatePicker();
    }

    private void addCounterparty() {
        try {
            db.createCounterparty(
                    "'" + textFieldCPName.getText() + "'",
                    "'" + textFieldShortName.getText() + "'",
                    textFieldBloomberg.getText().equals("") ? null : "'" + textFieldBloomberg.getText() + "'",
                    db.getSectorIDbyName((String) comboBoxSector.getSelectedItem()),
                    db.getPortfolioIDbyName((String) comboBoxPortfolio.getSelectedItem()),
                    db.getCountryIDbyName((String) comboBoxCountryDom.getSelectedItem()),
                    db.getCountryIDbyName((String) comboBoxCountryRisk.getSelectedItem()),
                    intraGroupCheckBox.isSelected() ? "'true'" : "'false'",
                    monitoredCheckBox.isSelected() ? "'true'" : "'false'",
                    datePicker.getDate(),
                    textPane.getText().equals("") ? null : "'" + textPane.getText() + "'",
                    longTermCheckBox.isSelected() ? "'true'" : "'false'",
                    etpCheckBox.isSelected() ? "'true'" : "'false'",
                    efetCheckBox.isSelected() ? "'true'" : "'false'",
                    gtcGroupComboBox.getSelectedItem() == null ? null : "'" + gtcGroupComboBox.getSelectedItem() + "'",
                    srkCheckBox.isSelected() ? "'true'" : "'false'",
                    textFieldDuns.getText().equals("") ? null : "'" + textFieldDuns.getText() + "'",
                    textFieldSubsidiaryLimit.getText().equals("") ? 0 : Integer.parseInt(textFieldSubsidiaryLimit.getText()),
                    textFieldUKZ.getText().equals("") ? null : "'" + textFieldUKZ.getText() + "'",
                    textAreaCauses.getText().equals("") ? null : "'" + textAreaCauses.getText() + "'");

            Helper.showDone();
            //id = db.getCounterpartyIDbyName(textFieldCPName.getText());
            if (!comboBoxDonor.getSelectedItem().equals("")) {
                id = db.getCounterpartyIDbyName(textFieldCPName.getText());
                db.createDonor(id, (String) comboBoxDonor.getSelectedItem());
            }
            this.setVisible(false);
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

    private void editCounterparty() {
        try {
            int portfolioID = db.getPortfolioIDbyName((String) comboBoxPortfolio.getSelectedItem());
            db.updateCounterparty(
                    id,
                    "'" + textFieldCPName.getText() + "'",
                    "'" + textFieldShortName.getText() + "'",
                    textFieldBloomberg.getText().equals("") ? null : "'" + textFieldBloomberg.getText() + "'",
                    db.getSectorIDbyName((String) comboBoxSector.getSelectedItem()),
                    portfolioID == -1 ? null : String.valueOf(portfolioID),
                    db.getCountryIDbyName((String) comboBoxCountryDom.getSelectedItem()),
                    db.getCountryIDbyName((String) comboBoxCountryRisk.getSelectedItem()),
                    intraGroupCheckBox.isSelected() ? "'true'" : "'false'",
                    monitoredCheckBox.isSelected() ? "'true'" : "'false'",
                    datePicker.getDate(),
                    textPane.getText().equals("") ? null : "'" + textPane.getText() + "'",
                    longTermCheckBox.isSelected() ? "'true'" : "'false'",
                    etpCheckBox.isSelected() ? "'true'" : "'false'",
                    efetCheckBox.isSelected() ? "'true'" : "'false'",
                    gtcGroupComboBox.getSelectedItem() == null ? null : "'" + gtcGroupComboBox.getSelectedItem() + "'",
                    srkCheckBox.isSelected() ? "'true'" : "'false'",
                    textFieldDuns.getText().equals("") ? null : "'" + textFieldDuns.getText() + "'",
                    textFieldSubsidiaryLimit.getText().equals("") ? 0 : Integer.parseInt(textFieldSubsidiaryLimit.getText()),
                    textFieldUKZ.getText().equals("") ? null : "'" + textFieldUKZ.getText() + "'",
                    textAreaCauses.getText().equals("") ? null : "'" + textAreaCauses.getText() + "'");
            db.removeAllDonors(id);
            if (comboBoxDonor.getSelectedItem() != null && !comboBoxDonor.getSelectedItem().equals("")) {
                db.createDonor(id, (String) comboBoxDonor.getSelectedItem());
            }
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

    private class JTextFieldLimit extends PlainDocument {

        private int limit;

        JTextFieldLimit(int limit) {
            super();
            this.limit = limit;
        }

        public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
            if (str == null) return;

            if ((getLength() + str.length()) <= limit) {
                super.insertString(offset, str, attr);
            }
        }
    }

}
