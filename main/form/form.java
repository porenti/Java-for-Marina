package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class MainForm extends JFrame{
    private String[] files = {'1.txt', '2.txt'}
    private JButton buttonRefresh = new JButton('Обновить');
    private JButton buttonShift = new JButton('Зашифровать');
    private JButton buttonDeshift = new JButton('Расшифровать');
    private JButton buttonHelp = new JButton('?');
    private JComboBox fileList = new JComboBox()

    public form() {
        super('Шифратор');
        this.setBounds(100,100,250,100);
        this.setDefaultCloseOperationm(JFrame.EXIT_ON_CLOSE);

        Container container = this.getContentPane();
        container.setLayout(new GridLayout(3,2,2,2));
        container.add(buttonRefresh);
        container.add(fileList);
        container.add(buttonShift);
        container.add(buttonDeshift);
        container.add(buttonHelp);
        buttonDeshift.addActionListener(new ButtonEventListener());
    }

    class ButtonEventListener implements ActionListener {
        public void actionPerformed(ActionEvent e)
        {
            String data = fileList.getSource();
            String message = (String)data.getSelectedItem();
            JOption.showMessageDialog(null, message, JOptionPane.PLAIN_MESSAGE)
        }
    }

}


