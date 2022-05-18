package gui;

import database.Database;
import database.Statrs;
import org.apache.poi.ss.formula.OperationEvaluationContext;
import org.apache.poi.ss.formula.eval.ErrorEval;
import org.apache.poi.ss.formula.eval.ValueEval;
import org.apache.poi.ss.formula.functions.FreeRefFunction;
import org.apache.poi.ss.formula.udf.DefaultUDFFinder;
import org.apache.poi.ss.formula.udf.UDFFinder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import util.BloombergRatingsReader;
import util.Helper;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.ResultSet;
import java.time.LocalDate;

public class BloombergRatingsGUI extends JFrame{
    private JPanel contentPane;
    private JTextField textFieldPathToFile;
    private JComboBox comboBoxIssuers;
    private JButton generateFileButton;
    private JButton selectFileButton;
    private JButton scanForUpdatesButton;
    private JButton updateButton;
    private JButton deleteRowsButton;
    private JTable table;
    private JProgressBar progressBar;
    private JTextArea outputTextArea;
    private Database db;
    private static final String DATE_FORMAT_NOW = "yyyy-MM-dd";

    private static BloombergRatingsGUI ourInstance = new BloombergRatingsGUI();

    public static BloombergRatingsGUI getInstance() {
        //ourInstance = new BloombergRatingsGUI();
        ourInstance.setVisible(true);
        return ourInstance;
    }

    private BloombergRatingsGUI() {
        drawGUI();
        pack();
        setLocationRelativeTo(null);
        this.db = Database.getInstance();
     }

    private void drawGUI() {
        setContentPane(contentPane);
        setTitle("Ratings update");
        comboBoxIssuers.addItem("Companies");
        comboBoxIssuers.addItem("Countries");
        outputTextArea.setFont(Helper.outputFont);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        generateFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateFile();
            }
        });
        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });
        deleteRowsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteRow();
            }
        });
        scanForUpdatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scanForUpdates();
            }
        });
        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                update();
            }
        });
    }

    private JFileChooser getSaveChooser() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Select folder");
        chooser.setCurrentDirectory(db.getDir());
        return chooser;
    }

    private void selectFile() {
        JFileChooser chooser = new JFileChooser(textFieldPathToFile.getText());
        File file;
        FileNameExtensionFilter xlsfilter = new FileNameExtensionFilter(
                "xls files (*.xls, *.xlsx)", "xls", "xlsx");
        chooser.setFileFilter(xlsfilter);
        chooser.setDialogTitle("Open file");
        chooser.setCurrentDirectory(db.getDir());
        int returnVal = chooser.showOpenDialog(new JFrame());
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            file = chooser.getSelectedFile();
            textFieldPathToFile.setText(file.getPath());
        }
    }

    private void createCompaniesUpdateFile(String path) throws Exception {

        XSSFWorkbook wb = new XSSFWorkbook();
        String[] functionNames = {"BDP"};
        FreeRefFunction[] functionImpls = {new FreeRefFunction() {
            @Override
            public ValueEval evaluate(ValueEval[] arg0,
                                      OperationEvaluationContext arg1) {
                // don't care about the returned result. we are not going to evaluate BDP
                return ErrorEval.NA;
            }
        }};

        UDFFinder udfToolpack = new DefaultUDFFinder(functionNames, functionImpls);

        // register the user-defined function in the workbook
        wb.addToolPack(udfToolpack);

        Sheet sheet = wb.createSheet("Ratings");
        Row firtstRow = sheet.createRow(0);
        Row secondRow = sheet.createRow(1);
        Row row;
        Cell cell;

        String[] headers = {"ticker", "name", "short_name", "sp_rating", "moody_rating", "fitch_rating", "sp_date", "moody_date", "fitch_date", "", "", "", "", ""};
        String[] tickersHeaders = {"","LONG_COMP_NAME","SHORT_NAME","RTG_SP_LT_FC_ISSUER_CREDIT","RTG_MOODY_LONG_TERM","Fitch Rating","RTG_SP_LT_FC_ISS_CRED_RTG_DT","RTG_MOODY_LONG_TERM_DATE","Fitch date","RTG_FITCH_LT_ISSUER_DFLT_RTG_DT","RTG_FITCH_LT_FC_ISS_DFLT_RTG_DT","COUNTRY_FULL_NAME","RTG_FITCH_LT_FC_ISSUER_DEFAULT","RTG_FITCH_LT_ISSUER_DEFAULT"};
        String[] columnNames = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N"};
        String naString = "\"#N/A N/A\"";

        for (int i = 0; i < tickersHeaders.length; i++) {
            cell = firtstRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell = secondRow.createCell(i);
            cell.setCellValue(tickersHeaders[i]);
        }

        try {

            Statrs srs = db.getTickers();
            ResultSet rs = srs.getRs();

            int index = 2;
            while (rs.next()) {
                row = sheet.createRow(index);
                cell = row.createCell(0);
                cell.setCellValue(rs.getString("BloombergTicker"));
                for (int j = 1; j < tickersHeaders.length; j++) {
                    cell = row.createCell(j);
                    switch (j) {
                        case 5:
                            cell.setCellFormula("IF(M" + (index + 1) + "=" + naString + ",N" + (index + 1) + ",M" + (index + 1) + ")");
                            break;
                        case 8:
                            cell.setCellFormula("IF(J" + (index + 1) + "=" + naString + ",K" + (index + 1) + ",J" + (index + 1) + ")");
                            break;
                        default:
                            cell.setCellFormula("BDP(" + "$A" + (index + 1) + "," + columnNames[j] + "$2)");
                            break;
                    }

                }
                index++;
            }
            srs.close();

//            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
//            String date = sdf.format(Calendar.getInstance().getTime());
            LocalDate date = LocalDate.now();
            FileOutputStream fileOut = new FileOutputStream(path + "\\Corporates Ratings " + date + ".xlsx");

            wb.write(fileOut);
            fileOut.close();

        } catch (Exception e1) {
            e1.printStackTrace();
            throw new Exception(e1.getMessage());
        }
    }

    private void createCountriesUpdateFile(String path) {

        XSSFWorkbook wb = new XSSFWorkbook();
        String[] functionNames = {"BDP"};
        FreeRefFunction[] functionImpls = {new FreeRefFunction() {
            @Override
            public ValueEval evaluate(ValueEval[] arg0,
                                      OperationEvaluationContext arg1) {
                // don't care about the returned result. we are not going to evaluate BDP
                return ErrorEval.NA;
            }
        }};

        UDFFinder udfToolpack = new DefaultUDFFinder(functionNames, functionImpls);

        // register the user-defined function in the workbook
        wb.addToolPack(udfToolpack);

        Sheet sheet = wb.createSheet("Ratings");
        Row firtstRow = sheet.createRow(0);
        Row secondRow = sheet.createRow(1);
        Row row;
        Cell cell;

        String[] headers = {"ticker", "name", "short_name", "sp_rating", "moody_rating", "fitch_rating", "sp_date", "moody_date", "fitch_date", "", "", "", "", ""};
        String[] tickersHeaders = {"","","","RTG_SP_LT_FC_ISSUER_CREDIT","RTG_MDY_LT_FC_DEBT_RATING","RTG_FITCH_LT_FC_DEBT","RTG_SP_LT_FC_ISS_CRED_RTG_DT","Max_Moody","RTG_FITCH_LT_FC_DEBT_RTG_DT","RTG_FITCH_LT_ISSUER_DFLT_RTG_DT","RTG_MDY_LT_FC_DEBT_RTG_DT","RTG_MOODY_LONG_TERM_DATE","RTG_MDY_FC_CURR_ISSUER_RTG_DT","RTG_FITCH_LT_ISSUER_DEFAULT"};
        String[] columnNames = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N"};

        for (int i = 0; i < tickersHeaders.length; i++) {
            cell = firtstRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell = secondRow.createCell(i);
            cell.setCellValue(tickersHeaders[i]);
        }

        try {

            Database db = Database.getInstance();
            Statrs srs = db.getCountryTickers();
            ResultSet rs = srs.getRs();

            int index = 2;
            while (rs.next()) {
                row = sheet.createRow(index);
                cell = row.createCell(0);
                cell.setCellValue(rs.getString("BloombergTicker"));
                cell = row.createCell(1);
                cell.setCellValue(rs.getString("Name"));
                cell = row.createCell(2);
                cell.setCellValue(rs.getString("ShortName"));
                for (int j = 3; j < tickersHeaders.length; j++) {
                    cell = row.createCell(j);
                    if (j == 7) cell.setCellFormula("TEXT(MAX(IFERROR(K" + (index + 1) + "*1,0),IFERROR(L" + (index + 1) + "*1,0),IFERROR(M" + (index + 1) + "*1,0)),\"ää.ìì.ÃÃÃÃ\")");
                    else cell.setCellFormula("BDP(" + "$A" + (index + 1) + "," + columnNames[j] + "$2)");

                }
                index++;
            }
            srs.close();

//            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
//            String date = sdf.format(Calendar.getInstance().getTime());
            LocalDate date = LocalDate.now();
            FileOutputStream fileOut = new FileOutputStream(path + "\\Sovereign Ratings " + date + ".xlsx");

            wb.write(fileOut);
            fileOut.close();

        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void update() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                if (comboBoxIssuers.getSelectedIndex() == 0) {

                    for (int i = 0; i < table.getRowCount(); i++) {
                        try {
                            db.createExternalRatingCounterparty((String) table.getValueAt(i, 5), (String) table.getValueAt(i, 2), LocalDate.parse(table.getValueAt(i, 7).toString()), (String) table.getValueAt(i, 0));
                            outputTextArea.setText(outputTextArea.getText() + "LOADED: " + table.getValueAt(i, 0) + "; " + table.getValueAt(i, 5) + "; " + table.getValueAt(i, 2) + "; " + table.getValueAt(i, 7) + "\n");
                        } catch (Exception e1) {
                            outputTextArea.setText(outputTextArea.getText() + "WARNING: " + e1.getMessage() + "\n");
                            e1.printStackTrace();
                        }
                    }

                } else {

                    for (int i = 0; i < table.getRowCount(); i++) {
                        try {
                            db.createCountryRating((String) table.getValueAt(i, 5), (String) table.getValueAt(i, 2), LocalDate.parse(table.getValueAt(i, 7).toString()), (String) table.getValueAt(i, 0));
                            outputTextArea.setText("LOADED: " + table.getValueAt(i, 0) + "; " + table.getValueAt(i, 5) + "; " + table.getValueAt(i, 2) + "; " + table.getValueAt(i, 7) + "\n");
                        } catch (Exception e1) {
                            outputTextArea.setText(outputTextArea.getText() + "WARNING: " + e1.getMessage() + "\n");
                            e1.printStackTrace();
                        }
                    }
                }

                Helper.showDone();
            }


        });

        t.start();
    }

    private void deleteRow() {
        while (table.getSelectedRows().length > 0) {
            int row = table.convertRowIndexToModel(table.getSelectedRows()[0]);
            ((DefaultTableModel) table.getModel()).removeRow(row);
        }
    }

    private void scanForUpdates() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    BloombergRatingsReader reader = new BloombergRatingsReader(textFieldPathToFile.getText(),
                            comboBoxIssuers.getSelectedIndex() == 0, progressBar);
                    table.setModel(reader.getTableModel());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    progressBar.setIndeterminate(false);
                    progressBar.setStringPainted(true);
                    progressBar.setForeground(Color.RED);
                    progressBar.setMaximum(1);
                    progressBar.setValue(1);
                    progressBar.setString("ERROR." + e1.getMessage());
                    e1.printStackTrace();
                }
            }

        });
        t.start();
    }

    private void generateFile() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    JFileChooser chooser = getSaveChooser();
                    chooser.setCurrentDirectory(db.getDir());
                    if (chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        createCompaniesUpdateFile(chooser.getSelectedFile().getPath());
                        createCountriesUpdateFile(chooser.getSelectedFile().getPath());
                    }
                    Helper.showDone();
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(new JFrame(), e.getMessage());
                }




            }
        });

        t.start();
    }

}
