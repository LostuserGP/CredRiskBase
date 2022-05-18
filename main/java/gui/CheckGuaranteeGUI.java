package gui;

import GuarantiesTool.Comparison;
import GuarantiesTool.GuaranteeEntity;
import GuarantiesTool.GuarantiesList;
import database.Database;
import util.Helper;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CheckGuaranteeGUI extends GUI {
    private JButton chooseFolderButton;
    private JTextField folderTextField;
    private JButton checkNamesButton;
    private JButton showGuaranteeButton;
    private JPanel contentPane;
    private JTable outputTable;
    private JPanel tablePanel;
    private JButton parseFilesButton;
    private JButton addToDBButton;
    private TableRowSorter tableRowSorter;
    private GUI gui;
    private Database db;

    public void reload() {

    }

    private static CheckGuaranteeGUI ourInstance = new CheckGuaranteeGUI();

    public static CheckGuaranteeGUI getInstance() {
        ourInstance.setVisible(true);
        return ourInstance;
    }

    public static void show(GUI gui) {
        CheckGuaranteeGUI ourGui = CheckGuaranteeGUI.getInstance();
        ourGui.gui = gui;
        ourGui.reload();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setVisible(true);
    }

    private CheckGuaranteeGUI() {
        drawGUI();
        pack();
    }

    private void drawGUI() {
        checkNamesButton.setEnabled(false);
        showGuaranteeButton.setEnabled(false);
        editListeners();
        db = Database.getInstance();
        setContentPane(contentPane);
        setDefaultCloseOperation(HIDE_ON_CLOSE);


        outputTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedColumn = outputTable.getSelectedColumn();
                    if (selectedColumn == 1) {
                        System.out.println("нажат второй столбец");
                    } else if (selectedColumn == 2) {
                        System.out.println("нажат третий столбец");
                    }
                }
            }
        });

        chooseFolderButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFolder();
            }
        });

        checkNamesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkNames();
            }
        });

        showGuaranteeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showGuaranties();
            }
        });

        parseFilesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parseFiles();
            }
        });

        addToDBButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addGuaranteesToSQL();
            }
        });

    }

    private void parseFiles() {
        GuarantiesList gl = GuarantiesList.getInstance();
        gl.clear();
        gl.create(folderTextField.getText());
        checkNamesButton.setEnabled(true);
        showGuaranteeButton.setEnabled(true);
    }

    private void chooseFolder() {
        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                JFileChooser chooser = new JFileChooser();
                File file;
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setDialogTitle("Choose folder");
                chooser.setCurrentDirectory(db.getDir());
                int returnVal = chooser.showOpenDialog(new JFrame());
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    file = chooser.getSelectedFile();
                    folderTextField.setText(file.getPath());
                    parseFilesButton.setEnabled(true);
                }
            }
        });
        t.start();
    }

    private void checkNames() {

        Comparison comparison = new Comparison();
        ArrayList<ArrayList<String>> compareResult = comparison.getResult();
        String[] columnNames = {"Контрагент", "Добавить", "Переименовать"};

        try {
            tableRowSorter = Helper.updateTable(outputTable, compareResult, columnNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showGuaranties() {

        GuarantiesList gl = GuarantiesList.getInstance();
        ArrayList<ArrayList<String>> garList = gl.getArray();
        String[] columnNames = {"Отчетный месяц", "Код дочернего общества", "Номер гарантии", "Дата начала действия", "Дата окончания действия",
                "Наименование банка", "ИНН банка", "Валюта", "Изначальная сумма", "На начало месяца", "Изменение",
        "На конец месяца", "Сумма операцией за месяц", "Вид гарантии", "Контрагент", "Бенифициар", "Код бенифициара",
                "Подтверждающий документ", "Дата", "Номер", "Примечание"};

        try {
            tableRowSorter = Helper.updateTable(outputTable, garList, columnNames);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void convertToRUB() {

    }

    private void addGuaranteesToSQL() {
        GuarantiesList gl = GuarantiesList.getInstance();
        ArrayList<GuaranteeEntity> garList = gl.getList();
        LocalDate date = gl.getReportDate();
        delOldGuaranteesReports(date);
        float usd = db.getExchangeRate("USD", date);
        float eur = db.getExchangeRate("EUR", date);
        float gbp = db.getExchangeRate("GBP", date);
        for (GuaranteeEntity garEntity : garList) {
            if (garEntity.getCurrency().equals("USD")) {
                garEntity.setCurrency("USD*");
                garEntity.setStartSum((long)(garEntity.getStartSum() * usd));
            } else if (garEntity.getCurrency().equals("EUR")) {
                garEntity.setCurrency("EUR*");
                garEntity.setStartSum((long)(garEntity.getStartSum() * eur));
            } else if (garEntity.getCurrency().equals("GBP")) {
                garEntity.setCurrency("GBP*");
                garEntity.setStartSum((long)(garEntity.getStartSum() * gbp));
            }
        }
        try {
            int results[] = db.addGuaranteesForReport(garList);
            int res = 0;
            for (int result : results) {
                res += result;
            }
            if (results.length == res) {
                Helper.showMsg("All done");
            } else {
                Helper.showMsg("Something may be wrong");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void delOldGuaranteesReports(LocalDate date) {

        try {
            db.removeCurrentGuaranteesReport(date);
//            date = date.plusMonths(-3);
//            db.removeOldGuaranteesReport(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void editListeners() {
        outputTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int selectedColumn = outputTable.getSelectedColumn();
                    if (selectedColumn == 1) {
                        System.out.println("Create new conuterparty");
                        createCounterparty();
                    } else if (selectedColumn == 2) {
                        System.out.println("Rename counterparty from Excel for DB matching");
                        renameCounterpartyInList();
                    }
                }
            }
        });
    }

    private void renameCounterpartyInList() {
        int selectedRow = outputTable.getSelectedRow();
        String name = (String) outputTable.getValueAt(selectedRow, 0);
        RenameGuaranteeCounterparty.show(name, this);
    }

    private void createCounterparty() {
        //int selectedColumn = outputTable.getSelectedColumn();
        int selectedRow = outputTable.getSelectedRow();
        String name = (String) outputTable.getValueAt(selectedRow, 0);
        AddCounterpartyGUI.show(name, this);
    }

    private String[] extractValuesFromTable(JTable table) {
        int selectedRow = table.getSelectedRow();
        String[] args = new String[table.getColumnCount()];
        for (int i = 0; i < table.getColumnCount(); i++) {
            args[i] = "" + table.getValueAt(selectedRow, i);
        }
        return args;
    }

}
