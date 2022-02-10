package wordle;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;

public class WordleFrame extends JFrame {

    private JTextField[][] wordMap;
    private JPanel wordPanel;
    private JPanel controlPanel;
    private JTextField inputField;
    private JButton submitButton;
    private JButton restartButton;
    private int line;
    private String word;
    private List<String> words;

    public WordleFrame(){
        initComponents();
        line = 0;

        inputField.setDocument(new PlainDocument(){
            @Override
            public void insertString( int offset, String  str, AttributeSet attr ) throws BadLocationException {
                if (str == null) return;

                if ((getLength() + str.length()) <= 5) {
                    super.insertString(offset, str, attr);
                }
            }
        });

        submitButton.addActionListener((event)->{
            submit(inputField.getText());
        });

        inputField.addActionListener((event)->{
            submit(inputField.getText());
        });

        restartButton.addActionListener((event)->{
            restart();
        });
        try{
            words = Files.readAllLines(Path.of("resources/words.txt"));
            word = newWord();
        }
        catch(IOException ext){
            ext.printStackTrace();
        }
    }

    private void submit(String input){

    }
    //region SetupCode
    private void insertCharacter(String character, int row, int col, Color color){
        JTextField field = wordMap[row][col];
        field.setBackground(color);
        field.setText(character);
    }

    private void restart(){
        word = newWord();
        inputField.setText("");
        line = 0;
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 5; j++){
                JTextField field = wordMap[i][j];
                field.setText("");
                field.setBackground(Color.WHITE);
            }
        }
    }

    private String newWord(){
        Random random = new Random();
        String nextWord = words.get(random.nextInt(words.size()));
        return nextWord;
    }

    private void initComponents(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        wordPanel = new JPanel();
        wordPanel.setLayout(new GridLayout(6, 5));
        controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(0,3));
        inputField = new JTextField();
        inputField.setFont(inputField.getFont().deriveFont(72F));
        submitButton = new JButton("Submit");
        submitButton.setFont(submitButton.getFont().deriveFont(72F));
        restartButton = new JButton("Restart");
        restartButton.setFont(restartButton.getFont().deriveFont(72F));
        controlPanel.add(restartButton);
        controlPanel.add(inputField);
        controlPanel.add(submitButton);
        wordMap = new JTextField[6][5];
        for(int i = 0; i < 6; i++){
            for(int j = 0; j < 5; j++){
                JTextField letterField = new JTextField();
                letterField.setColumns(1);
                letterField.setEditable(false);
                letterField.setHorizontalAlignment(SwingConstants.CENTER);
                Font font = letterField.getFont();
                letterField.setFont(font.deriveFont(72F));
                letterField.setBorder(new LineBorder(Color.BLACK));
                wordPanel.add(letterField);
                wordMap[i][j] = letterField;
            }
        }
        this.add(wordPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);
        this.pack();
    }
    //endregion
}
