package gsl;

import gsl.ui.MainFrame;
import javax.swing.SwingUtilities;

public class Main {

    private MainFrame ui;

    public Main() throws Exception {
        startUserInterface();
    }

    public static void main(String... args) throws Exception {
        Main main = new Main();
    }

    private void startUserInterface() throws Exception {
        SwingUtilities.invokeAndWait(new Runnable() {
            @Override
            public void run() {
                ui = new MainFrame();
            }
        });
    }
}
