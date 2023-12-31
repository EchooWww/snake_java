import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.JPanel;

public class GamePanel extends JPanel implements ActionListener {
    // Screen size
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    // Object size
    static final int UNIT_SIZE = 25;
    // Number of objects
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_WIDTH)/UNIT_SIZE;
    // The delay of our game
    static final int DELAY = 75;

    // Coordinates of our snake
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int [GAME_UNITS];
    // Initial snake
    int bodyParts = 6;
    // num and coordinates of the apple
    int applesEaten;
    int appleX;
    int appleY;
    // Direction of the snake
    char direction = 'R'; // L, U, D
    // Status of the snake
    boolean running = false;
    boolean gameOver = false;
    // Timer of the game
    Timer timer;
    // A random object for generating apples
    Random random;

    GamePanel(){
        random = new Random();
        // Set our size for the panel instance
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
//            // Draw the grids
//            g.setColor(new Color(255, 255, 255, 30));
//            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
//                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
//                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
//            }

            // Draw the apple
            g.setColor(new Color(0xC7372F));
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw the snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45, 180, 0));
                    g.setColor(new Color(random.nextInt(255), random.nextInt(255),random.nextInt(255)));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }
            g.setColor(Color.red);
            g.setFont(new Font("Game Over", Font.BOLD, 60));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+applesEaten))/2,  g.getFont().getSize());
        } else {
            gameOver(g);
        }
    }

    public void newApple() {
        appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    public void move(){
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i-1];
            y[i] = y[i-1];
        }

        switch (direction) {
            case 'U' -> y[0] = y[0] - UNIT_SIZE;
            case 'D' -> y[0] = y[0] + UNIT_SIZE;
            case 'L' -> x[0] = x[0] - UNIT_SIZE;
            case 'R' -> x[0] = x[0] + UNIT_SIZE;
        }
    }

    public void checkApple() {
        if ((x[0]==appleX) && (y[0] == appleY)){
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }
    public void checkCollisions() {
        // Check if head collides with body
        for (int i = bodyParts; i > 0 ; i--) {
            if ((x[0] == x[i]) && y[0] == y[i]) {
                running = false;
            }
        }

        // Check if head touches borders
        if (x[0] < 0 || x[0] > SCREEN_WIDTH || y[0] < 0 || y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    public void gameOver(Graphics g){
        // Game Over text
        g.setColor(Color.red);
        g.setFont(new Font("Game Over", Font.BOLD, 90));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over"))/2, SCREEN_HEIGHT / 2);
        g.setColor(Color.red);
        g.setFont(new Font("Game Over", Font.BOLD, 60));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: "+applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: "+applesEaten))/2,  g.getFont().getSize());
        g.drawString("Hit ENTER to replay", (SCREEN_WIDTH - metrics2.stringWidth("Hit ENTER to replay"))/2,  SCREEN_HEIGHT *2 / 3);
        gameOver = true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running && !gameOver) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    public void resetGame() {
        x[0] = SCREEN_WIDTH / 2;
        y[0] = SCREEN_HEIGHT / 2;
        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;
        newApple();
        gameOver = false;
        timer.start();
    }

    public class MyKeyAdapter extends KeyAdapter{
        @Override
        public void keyPressed(KeyEvent e) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                if (gameOver) {
                    resetGame();
                }
            } else {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT -> {
                        if (direction != 'R') direction = 'L';
                    }
                    case KeyEvent.VK_RIGHT -> {
                        if (direction != 'L') direction = 'R';
                    }
                    case KeyEvent.VK_UP -> {
                        if (direction != 'D') direction = 'U';
                    }
                    case KeyEvent.VK_DOWN -> {
                        if (direction != 'U') direction = 'D';
                    }
                }
            }
        }
    }
}
