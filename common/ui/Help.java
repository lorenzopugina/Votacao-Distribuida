package common.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;

public class Help {

    public static void show(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            JOptionPane.showMessageDialog(
                null,
                content,
                "Ajuda",
                JOptionPane.INFORMATION_MESSAGE
            );
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                "Erro ao carregar arquivo de ajuda.\n\n" + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE
            );
        }
    }
}
