import javax.swing.*;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        JFrame matricula = new JFrame("Principal - Matriculacion");
        matricula.setContentPane(new principal().matricula);
        matricula.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        matricula.pack();
        matricula.setSize(950,650);
        matricula.setVisible(true);
    }
}

