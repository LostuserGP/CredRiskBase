package gui;

import util.Helper;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

public class DebugGUI extends JFrame {
    private JPanel rootPanel;
    private JTextArea textArea;
    private static DebugGUI ourInstance = new DebugGUI();
    public boolean isShowed;

    public static DebugGUI getInstance(String str) {
        if (ourInstance == null) ourInstance = new DebugGUI();
        ourInstance.printText(str);
        //ourInstance.setVisible(true);
        return ourInstance;
    }

    public static void showDebug() {
        if (ourInstance == null) ourInstance = new DebugGUI();
        ourInstance.setVisible(true);
        Helper.debugmode = true;
    }

    private DebugGUI() {
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(Helper.outputFont);
        drawGUI();
        pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = getSize();
        Insets scnMax = Toolkit.getDefaultToolkit().getScreenInsets(getGraphicsConfiguration());
        int taskBarSize = scnMax.bottom;
        Point p = new Point();
        p.setLocation(screenSize.getWidth() - size.getWidth(), screenSize.getHeight() - size.getHeight() - taskBarSize);
        setLocation(p);
    }

    private void drawGUI() {
        setContentPane(rootPanel);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        DefaultCaret caret = (DefaultCaret) textArea.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        addWindowListener(new WindowListener() {

            public void windowActivated(WindowEvent event) {

            }

            public void windowClosed(WindowEvent event) {

            }

            public void windowClosing(WindowEvent event) {
                setVisible(false);
                Helper.debugmode = false;
            }

            public void windowDeactivated(WindowEvent event) {

            }

            public void windowDeiconified(WindowEvent event) {

            }

            public void windowIconified(WindowEvent event) {

            }

            public void windowOpened(WindowEvent event) {

            }

        });

    }

    private void printText(String str) {
        textArea.append(str);
        textArea.append("\n\n");
    }

}
