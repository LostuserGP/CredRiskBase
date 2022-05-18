package gui;

import database.Database;
import database.Statrs;
import org.apache.commons.lang3.exception.ExceptionUtils;
import util.Helper;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SrkRatingLimit extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField textFieldDep;
    private JTextField textFieldDen;
    private JTextField textFieldDto;
    private JTextField textFieldRating;
    private JTextField textFieldId;
    private int id;
    private Database db;
    private GUI gui;

    private static SrkRatingLimit ourInstance = new SrkRatingLimit();

    public static SrkRatingLimit getInstance() {
        return ourInstance;
    }

    public static void show(int id, GUI gui) {
        SrkRatingLimit ourGui = SrkRatingLimit.getInstance();
        ourGui.gui = gui;
        ourGui.id = id;
        ourGui.initialize();
        ourGui.ratingRefill();
        ourGui.setLocationRelativeTo(gui);
        ourGui.setModal(true);
        ourGui.setVisible(true);
    }

    private SrkRatingLimit() {
        drawGUI();
        pack();
        setLocationRelativeTo(gui);
    }

    private void  drawGUI() {
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
        setTitle("Update Rating Limit for SRK");
        try {
            db = Database.getInstance();
            Statrs srs = db.getSrkRatingLimitById(id);
            ResultSet rs = srs.getRs();
            rs.next();
            textFieldId.setText(id + "" );
            textFieldRating.setText(rs.getString("Name"));
            textFieldDep.setText(rs.getString("DepLimit"));
            textFieldDen.setText(rs.getString("DenLimit"));
            textFieldDto.setText(rs.getString("DtoLimit"));
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
        saveLimit();
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

    private void saveLimit() {
        try {
            int depLimit = Integer.parseInt(textFieldDep.getText());
            int denLimit = Integer.parseInt(textFieldDen.getText());
            int dtoLimit = Integer.parseInt(textFieldDto.getText());
            db.updateSrkRatingLimit(id, depLimit, denLimit, dtoLimit);
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

    private void ratingRefill() {

    }
}
