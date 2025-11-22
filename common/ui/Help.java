package common.ui;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.swing.*;

public class Help {

    public static void show(String filePath) {
        try {
            InputStream is = Help.class.getResourceAsStream("/" + filePath);

            if (is == null){
                throw new IOException("Arquivo de ajuda não encontrado: " + filePath);
            }

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
