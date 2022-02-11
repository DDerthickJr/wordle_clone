package wordle;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WordleFrame extends JFrame {

    private JTextField[][] wordMap;
    private JTextField inputField;
    private JButton submitButton;
    private JButton restartButton;
    private JButton copyButton;
    private int line;
    private String word;
    private List<String> words;
    private List<String> usedWords;
    private boolean gameWon;

    public WordleFrame(){
        initComponents();
        line = 0;
        usedWords = new ArrayList<>();
        gameWon = false;
        copyButton.setEnabled(false);

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

        copyButton.addActionListener((event)->{
            copyToClipBoard();
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
        if(line < 6 && !gameWon){
            if(words.contains(input) && !usedWords.contains(input)){
                usedWords.add(input);
                List<Character> usedLetters = new ArrayList<>();
                int counter = 0;
                for(int i = 0; i < input.length(); i++){
                    Character character = input.charAt(i);
                    if(character == word.charAt(i)){
                        insertCharacter(character.toString(), line, i, Color.GREEN);
                        usedLetters.add(character);
                        counter++;
                    }
                    else if(word.contains(character.toString())){
                        if(!usedLetters.contains(character)){
                            usedLetters.add(character);
                            insertCharacter(character.toString(), line, i, Color.YELLOW);
                        }
                        else if(word.lastIndexOf(character.toString()) != word.indexOf(character.toString())){
                            insertCharacter(character.toString(), line, i, Color.YELLOW);
                        }
                        else{
                            insertCharacter(character.toString(), line, i, Color.LIGHT_GRAY);
                        }
                    }
                    else{
                        usedLetters.add(character);
                        insertCharacter(character.toString(), line, i, Color.LIGHT_GRAY);
                    }
                }
                inputField.setText("");
                if(counter == 5){
                    gameWon = true;
                    copyButton.setEnabled(true);
                }
                else{
                    line++;
                }
            }
        }
        else if(gameWon){
            copyButton.setEnabled(true);
        }
        else{
            inputField.setText(word);
        }
        inputField.requestFocus();
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
        copyButton.setEnabled(false);
        gameWon = false;
        usedWords = new ArrayList<>();
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
        return words.get(random.nextInt(words.size()));
    }

    private void copyToClipBoard(){
        StringBuilder message = new StringBuilder();
        message.append("Wordle ").append(line + 1).append("/6\n\n");
        for(int i = 0; i < line + 1; i++){
            for(int j = 0; j < 5; j++){
                JTextField cell = wordMap[i][j];
                Color cellColor = cell.getBackground();
                if(cellColor == Color.GREEN){
                    message.append("\uD83D\uDFE9");
                }
                else if(cellColor == Color.YELLOW){
                    message.append("\uD83D\uDFE8");
                }
                else{
                    message.append("⬛");
                }
            }
            message.append("\n");
        }
        StringSelection selection = new StringSelection(message.toString());
        Clipboard board = Toolkit.getDefaultToolkit().getSystemClipboard();
        board.setContents(selection, selection);
    }

    private void initComponents(){
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setVisible(true);
        this.setLayout(new BorderLayout());
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BorderLayout());
        JPanel wordPanel = new JPanel();
        wordPanel.setLayout(new GridLayout(6, 5));
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(0,3));
        JPanel clipboardPanel = new JPanel();
        clipboardPanel.setLayout(new GridLayout(0,1));
        inputField = new JTextField();
        inputField.setFont(inputField.getFont().deriveFont(72F));
        submitButton = new JButton("Submit");
        submitButton.setFont(submitButton.getFont().deriveFont(72F));
        restartButton = new JButton("Restart");
        restartButton.setFont(restartButton.getFont().deriveFont(72F));
        copyButton = new JButton("Copy To Clipboard");
        copyButton.setFont(copyButton.getFont().deriveFont(72F));
        clipboardPanel.add(copyButton);
        inputPanel.add(restartButton);
        inputPanel.add(inputField);
        inputPanel.add(submitButton);
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
        controlPanel.add(inputPanel, BorderLayout.NORTH);
        controlPanel.add(clipboardPanel, BorderLayout.SOUTH);

        this.add(wordPanel, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);
        this.pack();
    }
    //endregion
}
