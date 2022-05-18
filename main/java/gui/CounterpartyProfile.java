package gui;

import database.Database;
import database.Statrs;
import org.apache.commons.lang3.exception.ExceptionUtils;
import util.Helper;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.sql.ResultSet;

public class CounterpartyProfile extends GUI {
    private JTextField textFieldName;
    private JTextField textFieldDate;
    private JTextField textFieldBloombergTicker;
    private JTextField textFieldDonor;
    private JTextField textFieldShortName;
    private JTextField textFieldSector;
    private JTextField textFieldID;
    private JButton btnEditCounterpartyProfile;
    private JButton btnDeleteCounterparty;
    private JTable internalRatingsTable;
    private JButton btnAddInternalRating;
    private JButton btnDeleteSelectedInternalRating;
    private JTable externalRatingsTable;
    private JTable tableFS;
    private JButton btnAddExternalRating;
    private JButton btnDeleteSelectedExternalRating;
    private JButton btnAddFS;
    private JButton btnDeleteSelectedFS;
    private JTable committeesTable;
    private JTable countryOfDomicileRatingsTable;
    private JTable countryOfRiskRatingsTable;
    private JTable guaranteesTable;
    private JButton btnAddCommittee;
    private JButton btnDeleteSelectedCommittee;
    private JButton btnAddCountryOfDomicileRating;
    private JButton btnDeleteSelectedCountryOfDomicileRating;
    private JButton btnAddCountryOfRiskRating;
    private JButton btnDeleteSelectedCountryOfRiskRating;
    private JButton btnAddGuarantee;
    private JButton btnDeleteSelectedGuarantee;
    private JTextField textFieldCountryOfDomicile;
    private JTextField textFieldCountryOfRisk;
    private JScrollPane scrollPaneInternalRatings;
    private JScrollPane scrollPaneExternalRatings;
    private JScrollPane scrollPaneGuarantees;
    private JScrollPane scrollPaneCountryOfRiskRatings;
    private JScrollPane scrollPaneCountryOfDomicileRatings;
    private JScrollPane scrollPaneCommittees;
    private JScrollPane scrollPaneFS;
    private JPanel contentPane;
    private JButton editGuaranteeButton;
    private JCheckBox longTermCheckBox;
    private JCheckBox etpCheckBox;
    private JCheckBox efetCheckBox;
    private JTextField gtcTextField;
    private JTextField textFieldDuns;
    private JTextField textFieldCauses;
    private JCheckBox srkCheckBox;
    private JScrollPane scrollPaneIndividualLimits;
    private JButton btnAddIndividualLimit;
    private JButton btnDeleteIndividualLimit;
    private JTable individualLimitTable;
    private JTabbedPane tabbedPane1;
    private JTextField textFieldSubsidiaryLimit;
    private JTextField textFieldUKZ;
    private JCheckBox checkBoxIntraGroup;
    private int id;
    private int countryOfRiskId;
    private int countryOfDomicileId;
    private Database db;
    private GUI gui;

    private static CounterpartyProfile ourInstance = new CounterpartyProfile();

    public static CounterpartyProfile getInstance() {
        return ourInstance;
    }

    public static void show(int id, GUI gui) {
        CounterpartyProfile ourGui = CounterpartyProfile.getInstance();
        ourGui.gui = gui;
        ourGui.id = id;
        ourGui.updateData();
        ourGui.setVisible(true);
    }

    private CounterpartyProfile() {
        drawGUI();
        setSize(1024, 768);
        setLocationRelativeTo(gui);
    }

    private void drawGUI() {

        setContentPane(contentPane);
        getRootPane().setDefaultButton(btnEditCounterpartyProfile);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(HIDE_ON_CLOSE);

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        btnEditCounterpartyProfile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                //AddCounterpartyGUI.getInstance(id, CounterpartyProfile.this);
                AddCounterpartyGUI.show(id, ourInstance);
            }
        });

        btnDeleteCounterparty.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCounterparty();
            }
        });

        btnAddInternalRating.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddInternalRatingGUI.show(textFieldName.getText(),ourInstance);
            }
        });
        btnDeleteSelectedInternalRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteInternalRating();
            }
        });


        btnAddExternalRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddExternalRatingGUI.show(textFieldName.getText(), ourInstance);
            }
        });
        btnDeleteSelectedExternalRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteExternalRating();
            }
        });
        btnAddFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddFinancialStatementGUI.show(textFieldName.getText(),ourInstance);
            }
        });
        btnDeleteSelectedFS.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteFS();
            }
        });
        btnAddCommittee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //AddCommitteeGUI.getInstance(textFieldName.getText(),CounterpartyProfile.this);
                AddCommitteeGUI.show(textFieldName.getText(),ourInstance);
            }
        });
        btnDeleteSelectedCommittee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCommittee();
            }
        });
        btnAddCountryOfDomicileRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCountryRatingGUI.show(textFieldCountryOfDomicile.getText(),ourInstance);
            }
        });
        btnDeleteSelectedCountryOfDomicileRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCountryDomRating();
            }
        });
        btnAddCountryOfRiskRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddCountryRatingGUI.show(textFieldCountryOfRisk.getText(),ourInstance);
            }
        });
        btnDeleteSelectedCountryOfRiskRating.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteCountryRating();
            }
        });
        btnAddGuarantee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddGuaranteeGUI.show(textFieldName.getText(),ourInstance);
            }
        });
        editGuaranteeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editGarantee();
                //AddGuaranteeGUI.getInstance(textFieldName.getText(), CounterpartyProfile.this, guaranteeID);
            }
        });
        btnDeleteSelectedGuarantee.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteGarantee();
            }
        });

        btnAddIndividualLimit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddIndividualLimit.show(id,ourInstance);
            }
        });
        btnDeleteIndividualLimit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteIndividualLimit();
            }
        });

        editListeners();

    }

    private void onCancel() {
        setVisible(false);
        if (gui != null) {
            try {
                gui.reload();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

    public void updateData(){
        setGeneralData();
        setInternalRatingsData();
        setExternalRatingsData();
        setCountryOfDomicileData();
        setCountryOfRiskData();
        setFSData();
        setCommitteesData();
        setGuaranteesData();
        setIndividualLimitsData();
    }

    private void setFSData() {
        try {
            tableFS.setModel(Helper.buildTableModel(db.getFSbyCounterpartyId(id)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCountryOfRiskData() {
        try {
            textFieldCountryOfRisk.setText(db.getCountryNameById(countryOfRiskId));
            countryOfRiskRatingsTable.setModel(Helper.buildTableModel(db.getExternalRatingsCountry(countryOfRiskId)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setCountryOfDomicileData() {
        try {
            textFieldCountryOfDomicile.setText(db.getCountryNameById(countryOfDomicileId));
            countryOfDomicileRatingsTable.setModel(Helper.buildTableModel(db.getExternalRatingsCountry(countryOfDomicileId)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setGuaranteesData() {
        try {
            guaranteesTable.setModel(Helper.buildTableModel(db.getGuarantees(id), true));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setExternalRatingsData() {
        try {
            externalRatingsTable.setModel(Helper.buildTableModel(db.getExternalRatingsByID(id)));
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void setCommitteesData() {
        try {
            committeesTable.setModel(Helper.buildTableModel(db.getCommittees(id)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setInternalRatingsData() {
        try {
            internalRatingsTable.setModel(Helper.buildTableModel(db.getInternalRatingsByID(id),false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setIndividualLimitsData() {
        try {
            individualLimitTable.setModel(Helper.buildTableModel(db.getIndividualLimitsByID(id),false));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] extractValuesFromTable(JTable table) {
        int selectedRow = table.getSelectedRow();
        TableModel model = table.getModel();
        String[] args = new String[model.getColumnCount()];
        for (int i = 0; i < model.getColumnCount(); i++) {
            args[i] = "" + model.getValueAt(selectedRow, i);
        }
        return args;
    }

    private int confirmDelete(JTable table) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1)
            JOptionPane.showMessageDialog(null, "No row was selected");
        else {
            StringBuilder confirmMessage = new StringBuilder(
                    "You're trying to delete this entry: \r\n\n");
            for (int i = 0; i < table.getColumnCount(); i++) {
                confirmMessage.append(table.getColumnName(i)).append(": ").append(table.getValueAt(selectedRow, i)).append("\r\n");
            }
            confirmMessage
                    .append("\r\nAre you sure you want do delete this entry?\r\n");
            return JOptionPane.showConfirmDialog(null,
                    confirmMessage.toString(), "Warning",
                    JOptionPane.YES_NO_OPTION);
        }
        return JOptionPane.NO_OPTION;
    }

    private void setGeneralData() {
        try {
            db = Database.getInstance();
            Statrs srs = db.getCounterpartyById(id);
            ResultSet rs = srs.getRs();
            rs.next();
            textFieldID.setText(id + "" );
            textFieldName.setText(rs.getString("Name"));
            textFieldShortName.setText(rs.getString("ShortName"));
            textFieldBloombergTicker.setText(rs.getString("BloombergTicker"));
            textFieldSector.setText(rs.getString("Sector"));
            textFieldDate.setText(rs.getString("StartDate"));
            textFieldDonor.setText(rs.getString("Donor"));
            longTermCheckBox.setSelected(rs.getBoolean("Longterm"));
            etpCheckBox.setSelected(rs.getBoolean("Etp"));
            efetCheckBox.setSelected(rs.getBoolean("Efet"));
            checkBoxIntraGroup.setSelected(rs.getBoolean("IntraGroup"));
            gtcTextField.setText(rs.getString("Gtc"));
            countryOfDomicileId = db.getCountryIDbyName(rs.getString("CountryOfDomicile"));
            countryOfRiskId = db.getCountryIDbyName(rs.getString("CountryOfRisk"));
            textFieldDuns.setText(rs.getString("Duns"));
            textFieldCauses.setText(rs.getString("Causes"));
            textFieldSubsidiaryLimit.setText(rs.getString("SubsidiaryLimit"));
            textFieldUKZ.setText(rs.getString("UKZ"));
            srkCheckBox.setSelected(rs.getBoolean("isSRK"));
            srs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteCounterparty() {
        String confirmMessage = "Are you sure you want do delete this counterparty?\r\n";
        int answer = JOptionPane.showConfirmDialog(null,
                confirmMessage, "Warning",
                JOptionPane.YES_NO_OPTION);

        if (answer == JOptionPane.YES_OPTION) {
            try {
                db.removeAllDonors(id);
                db.deleteCounterparty(id);
                gui.reload();
                Helper.showDone();
                CounterpartyProfile.this.dispose();

            } catch (Exception e1) {
                Object[] options = { "Ok", "Copy to Clipboard" };
                int n = JOptionPane.showOptionDialog(new JFrame(),
                        ExceptionUtils.getStackTrace(e1),
                        "Exception message", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");

                Clipboard clpbrd = Toolkit.getDefaultToolkit()
                        .getSystemClipboard();
                if (n == 1)
                    clpbrd.setContents(new StringSelection(
                            ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteInternalRating() {
        if (confirmDelete(internalRatingsTable) == JOptionPane.YES_OPTION) {
            try {
                String[] args = extractValuesFromTable(internalRatingsTable);
                db.deleteInternalRating(id, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
                //db.deleteInternalRating2(id, args[0], args[1], args[2], args[3], args[4], args[5], args[6], args[7], args[8]);
                Helper.showDone();
                setInternalRatingsData();
            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteExternalRating() {
        if (confirmDelete(externalRatingsTable) == JOptionPane.YES_OPTION) {
            try {
                String[] args = extractValuesFromTable(externalRatingsTable);
                db.deleteExternalRating(id, args[0], args[1], args[2]);
                Helper.showDone();
                setExternalRatingsData();
            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteFS() {
        if (confirmDelete(tableFS) == JOptionPane.YES_OPTION) {
            try {

                String[] values = extractValuesFromTable(tableFS);

                db.deleteFS(Integer.parseInt(values[0]));

                Helper.showDone();
                setFSData();

            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");

                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteCommittee() {
        if (confirmDelete(committeesTable) == JOptionPane.YES_OPTION) {
            try {
                String[] values = extractValuesFromTable(committeesTable);
                db.deleteCommittee(Integer.parseInt(values[0]));
                Helper.showDone();
                setCommitteesData();
            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteCountryDomRating() {
        if (confirmDelete(countryOfDomicileRatingsTable) == JOptionPane.YES_OPTION) {
            try {
                String[] args = extractValuesFromTable(countryOfDomicileRatingsTable);
                db.deleteCountryRating(textFieldCountryOfDomicile.getText(), args[0], args[1], args[2]);
                Helper.showDone();
                setCountryOfDomicileData();
            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteCountryRating() {
        if (confirmDelete(countryOfRiskRatingsTable) == JOptionPane.YES_OPTION) {
            try {
                String[] args = extractValuesFromTable(countryOfRiskRatingsTable);
                db.deleteCountryRating(textFieldCountryOfRisk.getText(), args[0], args[1], args[2]);
                Helper.showDone();
                setCountryOfRiskData();
            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteGarantee() {
        if (confirmDelete(guaranteesTable) == JOptionPane.YES_OPTION) {
            try {
                String[] values = extractValuesFromTable(guaranteesTable);
                db.deleteGuarantee(Integer.parseInt(values[0]));
                Helper.showDone();
                setGuaranteesData();
            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void deleteIndividualLimit() {
        if (confirmDelete(individualLimitTable) == JOptionPane.YES_OPTION) {
            try {
                String[] values = extractValuesFromTable(individualLimitTable);
                db.deleteIndividualLimit(Integer.parseInt(values[0]));
                Helper.showDone();
                setIndividualLimitsData();
            } catch (Exception e1) {
                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                        // custom
                        // Icon
                        options, // the titles of buttons
                        options[0]);
                Helper.debug("error");
                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
                e1.printStackTrace();
            }
        }
    }

    private void editGarantee() {
        try {
            String[] values = extractValuesFromTable(guaranteesTable);
            AddGuaranteeGUI.show(Integer.parseInt(values[0]), ourInstance);
        } catch (Exception e1) {
            Object[] options = {"Ok", "Copy to Clipboard"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE, null, // do not use a
                    options, // the titles of buttons
                    options[0]);
            Helper.debug("error");
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);
            e1.printStackTrace();
        }
    }

    private void editListeners() {
        guaranteesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    editGarantee();
                }
            }
        });
    }

    @Override
    public void reload() {
        updateData();
        if (gui != null) {
            try {
                gui.reload();
            } catch (Exception e) {
                e.getStackTrace();
            }
        }
    }

}


