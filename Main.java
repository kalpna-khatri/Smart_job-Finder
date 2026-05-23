import ui.WelcomeScreen;
import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            WelcomeScreen welcome = new WelcomeScreen();
            welcome.setVisible(true);
        });
    }
}