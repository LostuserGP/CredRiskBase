package gui;

import database.Database;
import database.Statrs;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import util.Helper;
import version.VersionControl;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import static gui.MainGUI.reportType.*;

public class MainGUI extends GUI {
    private JLabel labelLogo;
    private JLabel labelReportBuilder;
    private JLabel labelAddEntity;
    private JLabel labelSelectReport;
    private JComboBox comboBoxSelectReport;
    private JButton buttonRunReport;
    private JComboBox comboBoxSelectEntity;
    private JButton buttonAddEntity;
    private JLabel labelSelectEntity;
    private JLabel labelVersion;
    private JLabel labelBuild;
    private JPanel rootPanel;
    private JTable outputTable;
    private JLabel lblrowsCount;
    private JScrollPane scrollPane;
    private JTextField textFieldFilter;
    private JComboBox comboBoxColumnNames;
    private JButton buttonUnfilter;
    private JCheckBox testServerCheckBox;
    private JButton btnExportToExcel;
    private JButton debugButton;
    private TableRowSorter tableRowSorter;
    private Database db;
    private reportType rt = NONE;

    private void createUIComponents() {
        // TODO: place custom component creation code here

    }

    public MainGUI() {

        setTitle("Database Tool" + VersionControl.getVersion());
        setSize(640, 480);
        setContentPane(rootPanel);
        setLocationRelativeTo(null);
        addSelectReportItems();
        addSelectEntityItems();
        labelVersion.setText("Version " + VersionControl.getVersion());
        labelBuild.setText("Built on " + VersionControl.getDateasof());
        setVisible(true);

        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        buttonRunReport.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                runReport();
            }
        });
        buttonAddEntity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity();
            }
        });
        textFieldFilter.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filter();
            }
        });
        buttonUnfilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearFilter();
            }
        });
        btnExportToExcel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportToExcel();
            }
        });
        debugButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDebug();
            }
        });
        comboBoxSelectEntity.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addEntity();
            }
        });
        editListeners();
        db = Database.getInstance();
        testServerCheckBox.setSelected(db.getZone());

    }

    private void close() {
        db.close();
        System.exit(0);
    }

    private void runReport() {
        switch (comboBoxSelectReport.getSelectedItem().toString()) {
            case "List of Counterparties":
                rt = LISTOFCOUNTERPARTY;
                break;
            case "SRK":
                rt = SRK;
                break;
            case "SRK for Excel":
                rt = SRKEXCEL;
                break;
            case "List of Internal Ratings":
                rt = INTERNALRATINGS;
                break;
            case "List of External Ratings":
                rt = EXTERNALRATINGS;
                break;
            case "List of Country Ratings":
                rt = COUNTRYRATINGS;
                break;
            case "List of Active Ratings":
                rt = ACTIVERATINGS;
                break;
            case "List of approved counterparties":
                rt = APPROVEDCOUNTERPARTIES;
                break;
            case "Committee results":
                rt = COMMITTEERESULTS;
                break;
            case "Financial statements":
                rt = FINANCIALSTATEMENTS;
                break;
            case "Watch list":
                rt = WATCHLIST;
                break;
            case "List of Guarantees":
                rt = LISTOFGUARANTEES;
                break;
            case "Counterparty profile":
                rt = COUNTERPARTYPROFILE;
                break;
            case "Upload Bloomberg ratings":
                rt = BLOOMBERG;
                break;
            case "Check Guaranties":
                rt = CHECKGUARANTIES;
                break;
            case "List of Approved Guarantees":
                rt = APPROVEDGUARANTEES;
                break;
            case "List of Rating Group Limits":
                rt = RATINGLIMITS;
                break;
            case "Show Guarantees Report":
                rt = GUARANTEESREPORT;
                break;
            case "Show Bank Limit Usage":
                rt = BANKLIMITUSAGE;
                break;
            case "Show Group Limit Usage":
                rt = GROUPLIMITUSAGE;
                break;
            case "Contracts":
                rt = CONTRACTS;
                break;
            case "Rating Limits for SRK":
                rt = SRKLIMIT;
                break;
        }
        reload();
    }

    private void addEntity() {
        if (comboBoxSelectEntity.getSelectedItem().toString().equals("Counterparty")) {
            AddCounterpartyGUI.show(-1, this);
            if (rt == LISTOFCOUNTERPARTY) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Counterparty internal rating")) {
            AddInternalRatingGUI.show(null, this);
            if (rt == INTERNALRATINGS) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Counterparty external rating")) {
            AddExternalRatingGUI.show(null, this);
            if (rt == EXTERNALRATINGS) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Country external rating")) {
            AddCountryRatingGUI.show(null, this);
            if (rt == COUNTRYRATINGS) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Committee result")) {
            AddCommitteeGUI.show(null, this);
            if (rt == COMMITTEERESULTS) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Guarantee")) {
            AddGuaranteeGUI.show(null, this);
            if (rt == LISTOFGUARANTEES) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Financial statement")) {
            AddFinancialStatementGUI.show(null, this);
            if (rt == FINANCIALSTATEMENTS) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Approved Guarantee")) {
            AddApprovedGuaranteeGUI.show(-1, this);
            if (rt == APPROVEDGUARANTEES) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Rating Limit")) {
            AddRatingLimit.show(-1, this);
            if (rt == RATINGLIMITS) reload();
        } else if (comboBoxSelectEntity.getSelectedItem().toString().equals("Exchange Rating")) {
            AddCurrencyRates.show(this);
        }
    }

    private void filter() {
        List<RowFilter<Object, Object>> filters = new ArrayList<RowFilter<Object, Object>>(2);

        String squery = textFieldFilter.getText();

        if (comboBoxColumnNames.getSelectedIndex() == 0) {
            filters.add(RowFilter.regexFilter("(?i)" + squery));
            filters.add(RowFilter.regexFilter(squery.toUpperCase()));
            filters.add(RowFilter.regexFilter(squery.toLowerCase()));
            String[] strings = Helper.getStringsCapitalized(squery.toLowerCase());
            for (int i = 0; i < strings.length; i++) {
                filters.add(RowFilter.regexFilter(strings[i]));
            }
            tableRowSorter.setRowFilter(RowFilter.orFilter(filters));
        } else {
            filters.add(RowFilter.regexFilter("(?i)" + squery, comboBoxColumnNames.getSelectedIndex() - 1));
            filters.add(RowFilter.regexFilter(squery.toUpperCase(), comboBoxColumnNames.getSelectedIndex() - 1));
            filters.add(RowFilter.regexFilter(squery.toLowerCase(), comboBoxColumnNames.getSelectedIndex() - 1));
            String[] strings = Helper.getStringsCapitalized(squery.toLowerCase());
            for (int i = 0; i < strings.length; i++) {
                filters.add(RowFilter.regexFilter(strings[i], comboBoxColumnNames.getSelectedIndex() - 1));
            }
            tableRowSorter.setRowFilter(RowFilter.orFilter(filters));
        }

        lblrowsCount.setText(tableRowSorter.getViewRowCount() + " rows found");
    }

    private void clearFilter() {
        textFieldFilter.setText("");
        comboBoxColumnNames.setSelectedIndex(0);
        tableRowSorter.setRowFilter(RowFilter.regexFilter(""));
        lblrowsCount.setText(tableRowSorter.getViewRowCount() + " rows found");
    }

    private void addSelectReportItems() {
        comboBoxSelectReport.addItem("Counterparty profile");
        comboBoxSelectReport.addItem("List of Counterparties");
        comboBoxSelectReport.addItem("SRK");
        comboBoxSelectReport.addItem("SRK for Excel");
        comboBoxSelectReport.addItem("List of Internal Ratings");
        comboBoxSelectReport.addItem("List of External Ratings");
        comboBoxSelectReport.addItem("List of Country Ratings");
        comboBoxSelectReport.addItem("List of Active Ratings");
        comboBoxSelectReport.addItem("List of approved counterparties");
        comboBoxSelectReport.addItem("Committee results");
        comboBoxSelectReport.addItem("Financial statements");
        comboBoxSelectReport.addItem("Watch list");
        comboBoxSelectReport.addItem("List of Guarantees");
        comboBoxSelectReport.addItem("Upload Bloomberg ratings");
        comboBoxSelectReport.addItem("Check Guaranties");
        comboBoxSelectReport.addItem("List of Approved Guarantees");
        comboBoxSelectReport.addItem("List of Rating Group Limits");
        comboBoxSelectReport.addItem("Show Guarantees Report");
        comboBoxSelectReport.addItem("Show Bank Limit Usage");
        comboBoxSelectReport.addItem("Show Group Limit Usage");
        comboBoxSelectReport.addItem("Contracts");
        comboBoxSelectReport.addItem("Rating Limits for SRK");
    }

    private void addSelectEntityItems() {
        comboBoxSelectEntity.addItem("Counterparty");
        comboBoxSelectEntity.addItem("Counterparty internal rating");
        comboBoxSelectEntity.addItem("Counterparty external rating");
        comboBoxSelectEntity.addItem("Country external rating");
        comboBoxSelectEntity.addItem("Committee result");
        comboBoxSelectEntity.addItem("Guarantee");
        comboBoxSelectEntity.addItem("Financial statement");
        comboBoxSelectEntity.addItem("Approved Guarantee");
        comboBoxSelectEntity.addItem("Exchange Rating");
        //comboBoxSelectEntity.addItem("Rating Limit");
    }

    private void exportToExcel() {
        final JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter xlsxfilter = new FileNameExtensionFilter("excel files (*.xls, xlsx)", "xls", "xlsx");
        chooser.setFileFilter(xlsxfilter);
        chooser.setCurrentDirectory(db.getDir());
        chooser.setDialogTitle("Save as");
        chooser.setCurrentDirectory(new File("example.xlsx"));
        int returnVal = chooser.showSaveDialog(new JFrame());

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    File file;
                    if (FilenameUtils.getExtension(chooser.getSelectedFile().getPath()).equals("xlsx"))
                        file = chooser.getSelectedFile();
                    else file = new File(chooser.getSelectedFile() + ".xlsx");
                    Helper.exportTable(outputTable, file);
                }
            });
            thread.start();
        }
    }

    private void showDebug() {
        DebugGUI.showDebug();
    }

    private void editListeners() {
        outputTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2 && !(rt == NONE)) {
                    editTable();
                }
            }
        });
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(WindowEvent winEvt) {
                close();
            }
        });
    }

    private void editTable() {
        try {
            String[] values = extractValuesFromTable(outputTable);
            switch (rt) {
                case LISTOFCOUNTERPARTY:
                    CounterpartyProfile.show(Integer.parseInt(values[0]), this);
                    break;
                case SRKLIMIT:
                    SrkRatingLimit.show(Integer.parseInt(values[0]), this);
                    break;
                case INTERNALRATINGS:
                    //AddCounterpartyGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case EXTERNALRATINGS:
                    //tableRowSorter = Helper.updateTable(outputTable, db.getExternalRatings((Date) datePicker.getModel().getValue()));
                    break;
                case COUNTRYRATINGS:
                    //AddCountryRatingGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case ACTIVERATINGS:
                    //AddCountryRatingGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case APPROVEDCOUNTERPARTIES:
                    //AddCountryRatingGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case COMMITTEERESULTS:
                    //AddCommitteeGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case FINANCIALSTATEMENTS:
                    //AddCountryRatingGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case WATCHLIST:
                    //AddCountryRatingGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case LISTOFGUARANTEES:
                    AddGuaranteeGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case COUNTERPARTYPROFILE:
                    //AddCountryRatingGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case BLOOMBERG:
                    //AddCountryRatingGUI.show(Integer.parseInt(values[0]), this);
                    break;
                case APPROVEDGUARANTEES:
                    AddApprovedGuaranteeGUI.show(Integer.parseInt(values[0]),this);
            }

        } catch (Exception e1) {

            Object[] options = {"Ok", "Copy to Clipboard"};
            int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE,
                    null,     //do not use a custom Icon
                    options,  //the titles of buttons
                    options[0]);
            Helper.debug("error");

            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);

            e1.printStackTrace();
        }
    }

    enum reportType {
        NONE, LISTOFCOUNTERPARTY,
        INTERNALRATINGS,
        EXTERNALRATINGS,
        COUNTRYRATINGS,
        ACTIVERATINGS,
        APPROVEDCOUNTERPARTIES,
        COMMITTEERESULTS,
        FINANCIALSTATEMENTS,
        WATCHLIST,
        LISTOFGUARANTEES,
        COUNTERPARTYPROFILE,
        BLOOMBERG,
        CHECKGUARANTIES,
        APPROVEDGUARANTEES,
        RATINGLIMITS,
        GUARANTEESREPORT,
        BANKLIMITUSAGE,
        GROUPLIMITUSAGE,
        CONTRACTS,
        SRK,
        SRKLIMIT,
        SRKEXCEL
    }

    private String[] extractValuesFromTable(JTable table) {
        int selectedRow = table.getSelectedRow();
        String[] args = new String[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            args[i] = "" + table.getValueAt(selectedRow, i);
        }
        args[0] = args[0].trim();
        return args;
    }

    public void reloadTest() {
        Thread thread = new Thread() {
            public void run() {
                reload();
            }
        };
        thread.start();


    }

    public void reload() {
        if (!(rt == NONE)) {
            try {
                switch (rt) {
                    case LISTOFCOUNTERPARTY:
//                        Helper.updateTable(outputTable, db.getCounterparties((Date) datePicker.getModel().getValue()));
                        tableRowSorter = Helper.updateTable(outputTable, db.getCounterparties());
                        break;
                    case INTERNALRATINGS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getInternalRatings());
                        break;
                    case EXTERNALRATINGS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getExternalRatings());
                        break;
                    case COUNTRYRATINGS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getCountryRatings());
                        break;
                    case ACTIVERATINGS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getActiveRatings());
                        break;
                    case APPROVEDCOUNTERPARTIES:
                        tableRowSorter = Helper.updateTable(outputTable, db.getSRK());
                        break;
                    case COMMITTEERESULTS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getCommitteeResults());
                        break;
                    case FINANCIALSTATEMENTS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getFinancialStatements());
                        break;
                    case WATCHLIST:
                        tableRowSorter = Helper.updateTable(outputTable, db.getWatchList());
                        break;
                    case LISTOFGUARANTEES:
                        tableRowSorter = Helper.updateTable(outputTable, db.getGuarantees());
                        break;
                    case COUNTERPARTYPROFILE:
                        SearchCounterpartyGUI.show(this);
                        break;
                    case BLOOMBERG:
                        BloombergRatingsGUI.getInstance();
                        break;
                    case CHECKGUARANTIES:
                        CheckGuaranteeGUI.getInstance();
                        break;
                    case APPROVEDGUARANTEES:
//                        tableRowSorter = Helper.updateTable(outputTable, db.getApprovedGuarantees());
                        tableRowSorter = Helper.updateTable(outputTable, db.getApprovedGuarantees());
                        break;
                    case RATINGLIMITS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getRatingLimits());
                        break;
                    case GUARANTEESREPORT:
//                        tableRowSorter = Helper.updateTable(outputTable, db.getGuaranteesReport());
                        tableRowSorter = Helper.updateTable(outputTable, db.getGuaranteesReport());
                        break;
                    case BANKLIMITUSAGE:
                        tableRowSorter = Helper.updateTable(outputTable, db.getBankLimitUsage());
                        break;
                    case GROUPLIMITUSAGE:
                        tableRowSorter = Helper.updateTable(outputTable, db.getGroupLimitUsage());
                        break;
                    case CONTRACTS:
                        tableRowSorter = Helper.updateTable(outputTable, db.getContracts());
                        break;
                    case SRK:
                        tableRowSorter = Helper.updateTable(outputTable, db.getSRKList());
                        break;
                    case SRKLIMIT:
                        tableRowSorter = Helper.updateTable(outputTable, db.getSrkRatingLimits());
                        break;
                    case SRKEXCEL:
                        tableRowSorter = Helper.updateTableSRK(outputTable);
                        break;
                }

                lblrowsCount.setText(outputTable.getModel().getRowCount() + " rows found");
                comboBoxColumnNames.removeAllItems();
                comboBoxColumnNames.addItem("All columns");
                for (int i = 0; i < outputTable.getModel().getColumnCount(); i++) {
                    comboBoxColumnNames.addItem(outputTable.getModel().getColumnName(i));
                }
            } catch (Exception e1) {

                Object[] options = {"Ok", "Copy to Clipboard"};
                int n = JOptionPane.showOptionDialog(new JFrame(), ExceptionUtils.getStackTrace(e1), "Exception message", JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE,
                        null,     //do not use a custom Icon
                        options,  //the titles of buttons
                        options[0]);
                Helper.debug("error");

                Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
                if (n == 1) clpbrd.setContents(new StringSelection(ExceptionUtils.getStackTrace(e1)), null);

                e1.printStackTrace();
            }
        }
    }

    private TableRowSorter getBankLimitUsage() {
        JTable table = this.outputTable;
        try {
            Statrs statrs = db.getBankLimitUsage();
            ResultSet rs = statrs.getRs();
            if (!rs.isBeforeFirst()) {
                DefaultTableModel model = new DefaultTableModel();
                model.setRowCount(0);
                table.setModel(model);
                return null;
            }

            ResultSetMetaData metaData = rs.getMetaData();
            int numberOfColumns = metaData.getColumnCount();
            String[] columnNames = new String[numberOfColumns];

            // Get the column names
            for (int column = 0; column < numberOfColumns; column++) {
                columnNames[column] = metaData.getColumnLabel(column + 1);
            }

            // Get all rows.
            ArrayList<ArrayList> rows = new ArrayList();

            while (rs.next()) {
                ArrayList newRow = new ArrayList();
                for (int i = 1; i <= numberOfColumns; i++) {
                    newRow.add(rs.getObject(i));
                }
                rows.add(newRow);
            }

            String[][] dataArray = new String[rows.size()][rows.get(0).size()];
            Object object;
            NumberFormat formatterDouble = new DecimalFormat("#,###.00");
            NumberFormat formatterInt = new DecimalFormat("#,###");

            for (int i = 0; i < dataArray.length; i++) {
                for (int j = 0; j < dataArray[0].length; j++) {
                    object = rows.get(i).get(j);
                    dataArray[i][j] = (object == null) ? "" : (NumberUtils.isCreatable(object.toString()) ?
                            (object instanceof Integer) ? formatterInt.format(Integer.parseInt(object.toString())) : formatterDouble.format(Double.parseDouble(object.toString()))
                            : object.toString());
                }
            }

            TableModel tm =  new DefaultTableModel(dataArray, columnNames) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    //all cells false
                    return false;
                }

            };
            table.setModel(tm);
            for (int i = 0; i < numberOfColumns; i++) {
                if (metaData.getColumnType(i+1) <= 5) {
                    table.getColumnModel().getColumn(i).setCellRenderer(new Helper.MyTableCellRenderer());
                }
            }
            TableRowSorter<TableModel> sorter =
                    new TableRowSorter<TableModel>(table.getModel());
            table.setRowSorter(sorter);

            statrs.close();

            return sorter;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}