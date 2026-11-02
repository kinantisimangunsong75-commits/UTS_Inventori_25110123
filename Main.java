
package view;

import javax.swing.SwingUtilities;

public class Main {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            FormBarang form = new FormBarang();

            form.setVisible(true);

        });
    }
}
