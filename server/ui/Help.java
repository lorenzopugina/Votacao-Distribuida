package server.ui;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;

public class Help {

    private static final String FILE_PATH = "common/resources/serverHelp.txt";

    public static void show() {
        try {
            String content = Files.readString(Path.of(FILE_PATH));
            JOptionPane.showMessageDialog(null, content, "Ajuda", JOptionPane.INFORMATION_MESSAGE);
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
