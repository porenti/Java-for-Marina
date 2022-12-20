import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Scanner;

import static java.time.LocalTime.now;

public class MainForm extends JFrame {
    static File dir = new File(System.getProperty("user.dir")); //path указывает на директорию
    private JButton buttonShifr = new JButton("Шифрование");
    private JFileChooser fileList = new JFileChooser();

    public MainForm() {
        super("Шифратор");
        this.setBounds(100, 100, 500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container container = this.getContentPane();
        container.setLayout(new GridLayout(2, 1, 1, 1));
        container.add(buttonShifr);
        fileList.setFileFilter(new MyCustomFilter());
        container.add(fileList);
        buttonShifr.addActionListener(new ButtonShifrListener());
    }


    class ButtonShifrListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File file = (File) fileList.getSelectedFile();

            Scanner scanner = getScanner(file);

            String dataForShifr = "";
            while (true) {
                try {
                    dataForShifr += scanner.nextLine() + "\n";
                } catch (NoSuchElementException ex) {
                    break;
                }
            }

            int key = this.getKey();
            String codedText = this.shifruem(dataForShifr, key);

            if (this.last(codedText) == '\u0006')
            {
                codedText = codedText.substring(0,codedText.length()-1);
            }

            try {
                createNewFile(codedText);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }


            JOptionPane.showMessageDialog(null, "Готово");

        }

        public char last(String str) {
            return str.toCharArray()[str.length() - 1];
        }

        public static void createNewFile(String text) throws FileNotFoundException {
            String newPath = "result_" + now().getHour() + "_" + now().getMinute() + "_" + now().getSecond() + ".txt";
            File file = new File(newPath);
            PrintWriter pw = new PrintWriter(file);
            pw.print(text);
            pw.close();

        }


        public String shifruem(String text, int KEY) {
            // Получить массив символов
            char[] array = text.toCharArray();

            // Выполнить операцию XOR для каждого элемента массива
            for (int i = 0; i < array.length; i++) {
                array[i] = (char) (array[i] ^ KEY);
            }

            return new String(array);
        }


        public int getKey() {
            String keyFilePath = dir + "\\key.txt";
            File keyFile = new File(keyFilePath);

            return Integer.parseInt(this.getScanner(keyFile).nextLine());

        }

        public static Scanner getScanner(File file) {
            Scanner scanner = null;
            try {
                scanner = new Scanner(Objects.requireNonNull(file));
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            return scanner;
        }
    }

    class MyCustomFilter extends javax.swing.filechooser.FileFilter {
        @Override
        public boolean accept(File file) {
            // Allow only directories, or files with ".txt" extension
            return file.isDirectory() || file.getAbsolutePath().endsWith(".txt");
        }
        @Override
        public String getDescription() {
            // This description will be displayed in the dialog,
            // hard-coded = ugly, should be done via I18N
            return "Text documents (*.txt)";
        }
    }
}


