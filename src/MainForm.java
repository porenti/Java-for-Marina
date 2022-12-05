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
    File[] arrFiles = dir.listFiles(new MyFileNameFilter(".txt"));
    private JButton buttonShifr = new JButton("Зашифровать");
    private JButton buttonDeshifr = new JButton("Расшифровать");
    private JButton buttonHelp = new JButton("?");
    private JComboBox fileList = new JComboBox(arrFiles);

    private String separator = "^";

    public MainForm() {
        super("Шифратор");
        this.setBounds(100, 100, 500, 500);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(3, 1, 1, 1));
        container.add(fileList);
        container.add(buttonShifr);
        container.add(buttonDeshifr);
        buttonShifr.addActionListener(new ButtonShifrListener());
        buttonDeshifr.addActionListener(new ButtonDeshiftListener());
    }

    class ButtonDeshiftListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File file = (File) fileList.getSelectedItem();
            int fileId = fileList.getSelectedIndex();
            String fileName = arrFiles[fileId].getName();

            Scanner scanner = ButtonShifrListener.getScanner(file);

            String dataForDeshifr = "";
            while (true) {
                try {
                    dataForDeshifr += separator + scanner.nextLine();
                } catch (NoSuchElementException ex) {
                    break;
                }

            }
            int lenghtData = dataForDeshifr.length();

            String[] dataForDecode = this.prepareDataForDecode(dataForDeshifr);


            String key = this.getKey();
            String fullKey = ButtonShifrListener.getKeyToAllLenght(key, lenghtData);

            String finallyText = this.decode(dataForDecode, fullKey);

            JOptionPane.showMessageDialog(null, finallyText);


        }

        public String[] prepareDataForDecode(String text) {
            String[] result = text.split(" ");

            return result;
        }

        public String decode(String[] text, String fullKey) {
            String result = "";

            int itterationCount = text.length;

            boolean first = true;

            int j = 0;
            for (int i = 0; i < itterationCount; i++) {
                String textItem = text[i];
                char keyItem = fullKey.charAt(j);
                String cacheString = "";
                String fail = "^";
                if (textItem != "^")
                {
                    try {
                        String decodeItemKey = Integer.toBinaryString(keyItem);
                        String decodeItemText = Integer.toBinaryString(Integer.parseInt(textItem));
                        int lenghtKey = decodeItemKey.length();
                        String string = "";
                        int lenghtText = decodeItemText.length();
                        int z;
                        for (z = 0; z < lenghtText; z++)
                        {
                            if (decodeItemText.charAt(z) == decodeItemKey.charAt(z))
                            {
                                string += 0;
                            } else
                            {
                                string += 1;
                            }
                        }
                        j++;
                        result += Integer.parseInt(string,2) + " ";
                    } catch (NumberFormatException ignored)
                    {
                    }
                }
            }

            return result;
        }

        public String getKey() {
            String keyFilePath = dir + "\\key.txt";
            File keyFile = new File(keyFilePath);

            return ButtonShifrListener.getScanner(keyFile).nextLine();

        }
    }

    class ButtonShifrListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            File file = (File) fileList.getSelectedItem();
            int fileId = fileList.getSelectedIndex();
            String fileName = arrFiles[fileId].getName();

            Scanner scanner = getScanner(file);

            String dataForShifr = "";
            while (true) {
                try {
                    dataForShifr += separator + scanner.nextLine();
                } catch (NoSuchElementException ex) {
                    break;
                }

            }

            int lenghtDataWithoutSeparator = dataForShifr.length();
//                        -this.getCountItemsInString(dataForShifr,separator);


            String key = this.getKey();
            String fullKey = getKeyToAllLenght(key, lenghtDataWithoutSeparator);

            String shifranytuiText = this.shifruem(dataForShifr, fullKey);

            String finallyText = this.finallyTextToFile(shifranytuiText);

            try {
                createNewFile(true, finallyText);
            } catch (FileNotFoundException ex) {
                throw new RuntimeException(ex);
            }

            JOptionPane.showMessageDialog(null, "Готово");

        }

        public static void createNewFile(boolean method, String text) throws FileNotFoundException {
            String newPath = "";
            if (method == true) {
                newPath = "code_" + now().getHour() + "_" + now().getMinute() + "_" + now().getSecond() + ".txt";
            } else {
                newPath = "decode_" + now().getHour() + "_" + now().getMinute() + "_" + now().getSecond() + ".txt";
            }
            File file = new File(newPath);
            PrintWriter pw = new PrintWriter(file);
            pw.print(text);
            pw.close();

        }

        public String finallyTextToFile(String str) {
            String result = "";

            boolean first = true;
            for (char element : str.toCharArray()) {
                if (element != '^') {
                    result += element;
                } else {
                    if (first == true) {
                        first = false;
                    } else {
                        result += "\n";
                    }
                }
            }

            return result;
        }

        public String shifruem(String text, String fullKey) {
            String result = "";

            int itterationCount = text.length();


            for (int i = 0; i < itterationCount; i++) {
                char textChar = text.charAt(i);
                if (textChar != '^') {
                    if (textChar != ' ') {
                        String shortResult = "";

                        char keyChar = fullKey.charAt(i);
                        String textCharCode = Integer.toBinaryString(textChar);
                        String keyCharCode = Integer.toBinaryString(keyChar);
                        for (int j = 0; j < textCharCode.length(); j++) {
                            if (textCharCode.charAt(j) == keyCharCode.charAt(j)) {
                                shortResult += "1";
                            } else {
                                shortResult += "0";
                            }
                        }
                        result += " " + Integer.parseInt(shortResult, 2);
                    } else {
                        result += " &";
                    }
                } else {
                    result += " ^";
                }
            }

            return result;
        }

        public int getCountItemsInString(String str, String item) {
            int count = 0;
            for (char element : str.toCharArray()) {
                if (element == item.toCharArray()[0]) count++;
            }
            return count;
        }

        public static String getKeyToAllLenght(String key, int lenght) {
            int lenghtInitialKey = key.length();
            String result = "";
            for (int i = 0; i < lenght; i++) {
                result += key.toCharArray()[i % lenghtInitialKey];
            }
            return result;
        }

        public String getKey() {
            String keyFilePath = dir + "\\key.txt";
            File keyFile = new File(keyFilePath);

            return this.getScanner(keyFile).nextLine();

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

    class MyFileNameFilter implements FilenameFilter {

        private String ext;

        public MyFileNameFilter(String ext) {
            this.ext = ext.toLowerCase();
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(ext);
        }
    }
}


