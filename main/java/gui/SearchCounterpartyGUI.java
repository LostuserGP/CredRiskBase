package gui;

import database.Database;

import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;

public class SearchCounterpartyGUI extends GUI {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField searchTextField;
    private JButton searchButton;
    private JList counterpartiesList;
    private JButton refreshButton;
    private DefaultListModel listModel;
    private Database db;
    private GUI gui;

    private static SearchCounterpartyGUI ourInstance = new SearchCounterpartyGUI();

    public static SearchCounterpartyGUI getInstance() {
        ourInstance.setVisible(true);
        return ourInstance;
    }

    public static void show(GUI gui) {
        SearchCounterpartyGUI ourGui = SearchCounterpartyGUI.getInstance();
        ourGui.gui = gui;
        ourGui.reload();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setVisible(true);
    }

    @Override
    public void reload() {
        fillTheList();
    }

    private SearchCounterpartyGUI() {
        drawGUI();
        pack();
    }

    private void drawGUI() {
        db = Database.getInstance();
        setContentPane(contentPane);
        //setModal(true);
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

        //listModel = counterpartiesList.getModel();
        listModel = new DefaultListModel();
        counterpartiesList.setModel(listModel);

        counterpartiesList.addMouseListener(new MouseAdapter() {
             public void mouseClicked(MouseEvent evt) {
                chooseByClick(evt);
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillTheList();
            }
        });

        searchTextField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fillTheList();
            }
        });

        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                refresh();
            }
        });

        searchTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (searchTextField.getText().length() > 2) fillTheList();
            }
        });

        refresh();
    }

    private void chooseByClick(MouseEvent evt) {
        if (evt.getClickCount() == 2) {
            onOK();
        }
    }

    private void callCounterpartyProfile(String counterpartyName) throws Exception {
        //this.setVisible(false);
        int id = db.getCounterpartyIDbyName(counterpartyName);
        CounterpartyProfile.show(id, ourInstance);
    }

    private void fillTheList() {
        try {
            listModel.removeAllElements();
            ArrayList<String> values = db.searchForCounterparties(searchTextField.getText());
            for (String string : values) {
                listModel.addElement(string);
            }
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    private void onOK() {
        // add your code here
        String counterpartyName = (String) listModel.get(counterpartiesList.getSelectedIndex());
        setVisible(false);
        try {
            callCounterpartyProfile(counterpartyName);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void onCancel() {
        setVisible(false);
    }

    public void refresh() {
        fillTheList();
    }

}
