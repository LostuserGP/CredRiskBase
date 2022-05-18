package util;

import database.Database;
import gui.MainGUI;

import javax.swing.*;

public class Starter {

    public static void main(String[] args) {
        //System.setProperty("java.library.path", "C:\\Projects\\Libs");

        try {
            System.out.println("Make windows like UI");
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
                | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        Thread threadConn = new Thread() {
            public void run() {
                Database db = Database.getInstance();
            }
        };
        Thread threadUI = new Thread() {
            public void run() {
                MainGUI window = new MainGUI();
            }
        };
        threadConn.start();
        threadUI.start();


//        EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                try {
//                    MainGUI window = new MainGUI();
//                    //window.frmLoadRates.setVisible(true);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
    }
}
