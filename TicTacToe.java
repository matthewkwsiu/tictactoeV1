import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.net.URL;
import javax.imageio.ImageIO;

/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 * 
 * Extensions/features added: Can play audio, has x and o images, and keeps score
 * 
 * @author Lynn Marshall
 * @version November 8, 2012
 * 
 * @author Matthew Siu
 * @version November 23, 2019
 */

public class TicTacToe implements ActionListener
{
    public static final String PLAYER_X = "X"; // player using "X"
    public static final String PLAYER_O = "O"; // player using "O"
    public static final String EMPTY = "";  // empty cell
    public static final String TIE = "T"; // game ended in a tie

    private String player;   // current player (PLAYER_X or PLAYER_O)

    private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress

    private int numFreeSquares; // number of squares still free

    private int x_wins;
    private int o_wins;
    private int ties;

    private JButton board[][];
    private JLabel stateLabel; // text area to print game status
    private JLabel scoreLabel;
    private JFrame frame;
    private JPanel panel1;
    private Container contentPane;
    private JMenuBar menuBar; 
    private JMenu fileMenu;
    private JMenuItem newItem;
    private JMenuItem quitItem;
    private JMenuItem clearScoreItem;
    private JMenuItem playMusicItem;
    private JMenuItem stopMusicItem;
    private AudioClip click;

    private static ImageIcon imgX;
    private static ImageIcon imgO;

    /** 
     * Constructs a new Tic-Tac-Toe board.
     */
    public TicTacToe()
    {
        //sets up JFrame and contentPane
        frame = new JFrame();
        contentPane = frame.getContentPane();
        contentPane.setLayout(new BorderLayout());

        //setting up the JPanel for the 3x3 grid
        panel1 = new JPanel();
        panel1.setLayout(new GridLayout(3,3));
        contentPane.add(panel1, BorderLayout.CENTER);

        //setting up the 3x3 grid of JButtons
        board = new JButton[3][3];
        for(int i = 0; i <= 2; i++){
            for(int j = 0; j <= 2; j++){
                board[i][j] = new JButton();
                board[i][j].addActionListener(this);
                panel1.add(board[i][j]);
            }
        }

        //adds the JLabel south of the contentPane
        stateLabel = new JLabel();
        stateLabel.setVisible(true);
        contentPane.add(stateLabel, BorderLayout.SOUTH);

        //adds the winLabel north of the contentPane        
        scoreLabel = new JLabel();
        scoreLabel.setVisible(true);
        contentPane.add(scoreLabel, BorderLayout.NORTH);
        //this sets the wins and ties to 0, as well as setting the text of the label
        resetScoreLabel();

        //set up the menu
        menuBar = new JMenuBar();   
        frame.setJMenuBar(menuBar);

        fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        //This initializes the menuitems, and adds the keyboard shortcuts and actionlisteners to them
        final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();

        newItem = new JMenuItem("Restart");
        fileMenu.add(newItem);
        newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, SHORTCUT_MASK));
        newItem.addActionListener(this);

        quitItem = new JMenuItem("Quit");
        fileMenu.add(quitItem);
        quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK));
        quitItem.addActionListener(this);

        clearScoreItem = new JMenuItem("Clear Score");
        fileMenu.add(clearScoreItem);
        clearScoreItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, SHORTCUT_MASK));
        clearScoreItem.addActionListener(this);

        playMusicItem = new JMenuItem("Play Music");
        fileMenu.add(playMusicItem);
        playMusicItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, SHORTCUT_MASK));
        playMusicItem.addActionListener(this);

        stopMusicItem = new JMenuItem("Stop Music");
        fileMenu.add(stopMusicItem);
        stopMusicItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, SHORTCUT_MASK));
        stopMusicItem.addActionListener(this);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit when we hit the "X"
        frame.pack(); // pack everthing into our frame

        frame.setSize(600, 600);
        frame.setResizable(false); // we cannot resize it
        frame.setVisible(true); // it's visible

        //set up two static images for comparing icons

        try{
            imgX = new ImageIcon (ImageIO.read(getClass().getResource("x.bmp")));
            imgO = new ImageIcon (ImageIO.read(getClass().getResource("o.bmp")));
        } catch (Exception ex){
            System.out.println(ex);
        }

        clearBoard();

    }

    /**
     * Sets everything up for a new game.  Marks all squares in the Tic Tac Toe board as empty,
     * and indicates no winner yet, 9 free squares and the current player is player X.
     */
    private void clearBoard()
    {
        //this resets each JButton to no label and clickable
        for (int i = 0; i <= 2; i++) {
            for (int j = 0; j <= 2; j++) {
                board[i][j].setText(EMPTY);
                board[i][j].setIcon(null);
                board[i][j].setEnabled(true);
            }
        }

        numFreeSquares = 9;
        //sets player as first player
        player = PLAYER_X;     // Player X always has the first turn.

        //Since a new game is in progress, 
        stateLabel.setText("Game in Progress; the current player is " + player);
    }

    /**
     * Returns true if filling the given square gives us a winner, and false
     * otherwise.
     *
     * @param int row of square just set
     * @param int col of square just set
     * 
     * @return true if we have a winner, false otherwise
     */
    private boolean haveWinner(int row, int col) 
    {
        // unless at least 5 squares have been filled, we don't need to go any further
        // (the earliest we can have a winner is after player X's 3rd move).

        if (numFreeSquares>4) return false;

        // Note: We don't need to check all rows, columns, and diagonals, only those
        // that contain the latest filled square.  We know that we have a winner 
        // if all 3 squares are the same, as they can't all be blank (as the latest
        // filled square is one of them).

        // Note #2: this method currently relies on the text in each JButton to check if there is a winner
        // A second possible implementation relies on the Icons, shown below. However, that 
        // implementation currently does not work. One possible solution would be to override
        // the .equals() method, and rewriting it with some conditions.

        // check row "row"
        if ( board[row][0].getLabel().equals(board[row][1].getLabel()) &&
        board[row][0].getLabel().equals(board[row][2].getLabel()) ) return true;

        // check column "col"
        if ( board[0][col].getLabel().equals(board[1][col].getLabel()) &&
        board[0][col].getLabel().equals(board[2][col].getLabel()) ) return true;

        // if row=col check one diagonal
        if (row==col)
            if ( board[0][0].getLabel().equals(board[1][1].getLabel()) &&
            board[0][0].getLabel().equals(board[2][2].getLabel()) ) return true;

        // if row=2-col check other diagonal
        if (row==2-col)
            if ( board[0][2].getLabel().equals(board[1][1].getLabel()) &&
            board[0][2].getLabel().equals(board[2][0].getLabel()) ) return true;

        //Note #3: This is the imcomplete implementation of the comparison for ImageIcons

        // // check row "row"
        // if ( board[row][0].getIcon().equals(board[row][1].getIcon()) &&
        // board[row][0].getIcon().equals(board[row][2].getIcon()) ) return true;

        // // check column "col"
        // if ( board[0][col].getIcon().equals(board[1][col].getIcon()) &&
        // board[0][col].getIcon().equals(board[2][col].getIcon()) ) return true;

        // // if row=col check one diagonal
        // if (row==col)
        // if ( board[0][0].getIcon().equals(board[1][1].getIcon()) &&
        // board[0][0].getIcon().equals(board[2][2].getIcon()) ) return true;

        // // if row=2-col check other diagonal
        // if (row==2-col)
        // if ( board[0][2].getIcon().equals(board[1][1].getIcon()) &&
        // board[0][2].getIcon().equals(board[2][0].getIcon()) ) return true;

        // no winner yet
        return false;
    }

    public void actionPerformed(ActionEvent e){
        //this gets the object that ActionEvent e was fired from
        Object o = e.getSource();

        // if the thing clicked is a button, this if statement will be entered
        if(o instanceof JButton){
            //cast it as a JButton to compare it directly to the buttons
            JButton button = (JButton)o;
            //now check which button it is
            for (int i = 0; i <= 2; i++) {
                for (int j = 0; j <= 2; j++) {
                    //if this button was clicked, enter into this if statement
                    if(button == board[i][j]){
                        //check if the button has already been clicked
                        //if it is empty, it probably has not been clicked
                        if(!board[i][j].getLabel().equals(EMPTY)){
                            //in that case, do nothing
                            System.out.println("this is not blank");

                        } else {

                            //otherwise, if it has not yet been clicked...
                            //set the jbutton icon as the current player            
                            //also set the text as current player
                            if(player.equals(PLAYER_X)){
                                board[i][j].setIcon(imgX);
                                board[i][j].setText(player);                                
                            } else {
                                board[i][j].setIcon(imgO);
                                board[i][j].setText(player);
                            }

                            //set the current button pressed as unpressable
                            board[i][j].setEnabled(false);
                            numFreeSquares--;

                            //after a button has been pressed, check if there has been a winner
                            //if there is no winner and all buttons are pressed, it is a tie
                            if(haveWinner(i, j)){
                                stateLabel.setText("The winner is " + player + "!");
                                //depending on who the winner is, change the score
                                if(player.equals(PLAYER_X)){
                                    x_wins++;
                                } else {
                                    o_wins++;
                                }
                                endGame();
                                return;
                            } else if(numFreeSquares == 0){
                                stateLabel.setText("It's a tie!");
                                ties++;
                                endGame();
                                return;
                            }

                            //switch player
                            switchPlayer();

                        }
                    }
                }
            }
        }
        //this means it is not a button, so it is a menu item
        //checking regardless (in case there was a third object type added later)
        if(o instanceof JMenuItem){
            //cast it as a JMenuItem
            JMenuItem menuItem = (JMenuItem)o;

            if(menuItem == newItem){
                //start a new game
                clearBoard();
                return;
            } else if(menuItem == quitItem){
                //quit the game               
                System.exit(0);
                return;
            } else if(menuItem == clearScoreItem) {
                resetScoreLabel();
            } else if(menuItem == playMusicItem) {

                //Note: Unfortunately, it doesn't seem that I can play my wav file which I included.
                //However, playing the beep-08b wav file works fine. So I will leave it there for now to meet
                //the music requirements
                //I tried the cargo_train.wav sound sample using the PlayAlarm class provided in class, but it also was
                //unable to play, even in that example class, not sure why. 

                // URL url = getClass().getClassLoader().getResource("Lost Sky - Dreams.wav");
                // /* I got this music from NCS (non-copyright-sounds)
                // Here is the credit:
                // Track: Lost Sky - Dreams [NCS Release]
                // Music provided by NoCopyrightSounds.
                // Watch: https://youtu.be/SHFTHDncw0g
                // Free Download / Stream: http://ncs.io/DreamsYO
                // */
                // click = Applet.newAudioClip(url);
                // click.play();
                // // or can use loop
                // // click.loop();

                URL urlClick = getClass().getClassLoader().getResource("beep-08b.wav"); // beep
                click = Applet.newAudioClip(urlClick);
                click.play(); // just plays clip once

            } else if(menuItem == stopMusicItem) {
                click.stop();
            }
        }
    }

    /**
     * Ends the current game, prevents buttons from being pressed.
     */
    private void endGame(){
        for (int a = 0; a <= 2; a++) {
            for (int b = 0; b <= 2; b++) {
                board[a][b].setEnabled(false);
            }
        }
        updateScoreLabel();
    }

    /**
     * Switches the player from X to O or O to X
     */
    private void switchPlayer(){
        if(player.equals(PLAYER_X)){
            //System.out.println("the current player is player x");
            player = PLAYER_O;
        }
        //just putting this if in case we set a third condition
        else if(player.equals(PLAYER_O)){
            player = PLAYER_X;
        }
        stateLabel.setText("Game in Progress; the current player is " + player);
    }

    /**
     * Updates the scoreLabel
     */
    private void updateScoreLabel(){
        scoreLabel.setText("X Wins: " + x_wins + " O Wins: " + o_wins + " Ties: " + ties);
    }

    /**
     * Resets score and updates label
     */
    private void resetScoreLabel(){
        x_wins = 0;
        o_wins = 0;
        ties = 0;
        updateScoreLabel();
    }

}
