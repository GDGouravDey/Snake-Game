import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener{
    private class Tile {
        int x;
        int y;

        Tile(int x, int y) {
            this.x=x;
            this.y=y;
        }
    }
    int boardWidth, boardHeight;
    int tileSize=25;
    int highest=0;
    // Snake
    Tile snakeHead;
    ArrayList<Tile> snakeBody;

    // Food
    Tile food;
    Random random;

    //Game Logic
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean gameOver = false;
    boolean keyProcessed = false;

    SnakeGame(int boardWidth, int boardHeight){
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(this.boardWidth, this.boardHeight));
        setBackground(Color.BLACK);
        addKeyListener(this);
        setFocusable(true);

        snakeHead = new Tile(5,5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10,10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(90, this); // Speed Of Snake
        gameLoop.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        //Grid
        // for(int i = 0; i < boardWidth/tileSize; i++) {
        //     g.drawLine(i*tileSize, 0, i*tileSize, boardHeight);
        //     g.drawLine(0, i*tileSize, boardWidth, i*tileSize);
        // }

        // Food
        g.setColor(Color.RED);
        g.fill3DRect(food.x*tileSize, food.y*tileSize, tileSize, tileSize, true);

        // Snake Head
        g.setColor(Color.GREEN);
        g.fill3DRect(snakeHead.x*tileSize, snakeHead.y*tileSize, tileSize, tileSize, true);

        // Snake Body
        for(int i=0;i<snakeBody.size();i++) {
            Tile snakePart = snakeBody.get(i);
            g.fill3DRect(snakePart.x*tileSize, snakePart.y*tileSize, tileSize, tileSize, true);
        }

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 17));
        if(snakeBody.size()>highest)
            highest=snakeBody.size();
        if(gameOver) {
            g.setColor(Color.WHITE);
            g.drawString("Highest: " + highest, 510, 20);
            g.setColor(Color.YELLOW);
            g.drawString("Game Over! Press Space to Restart", 10, 20);
            g.drawString("Your Score is " + snakeBody.size(), 10, 40);
        }
        else {
            g.setColor(Color.WHITE);
            g.drawString("Score: " + snakeBody.size(), 10, 20);
            g.drawString("Highest: " + highest, 510, 20);
        }
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth/tileSize);
        food.y = random.nextInt(boardHeight/tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {
        // Eat Food
        if(collision(snakeHead, food)) {
            snakeBody.add(new Tile(snakeHead.x, snakeHead.y));
            placeFood();
        }

        // Snake Body
        for(int i=snakeBody.size()-1;i>=0;i--) {
            Tile snakePart = snakeBody.get(i);
            if(i==0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            }
            else {
                Tile prevSnakepart = snakeBody.get(i-1);
                snakePart.x = prevSnakepart.x;
                snakePart.y = prevSnakepart.y;
            }
        }

        // Snake Head
        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        // Game Over Condition
        for(int i=0;i<snakeBody.size();i++) {
            Tile snakePart = snakeBody.get(i);
            if(collision(snakeHead, snakePart)) {
                gameOver = true;
            }
        }

        if(snakeHead.x*tileSize < 0 || snakeHead.x*tileSize > boardWidth || snakeHead.y*tileSize < 0 || snakeHead.y*tileSize > boardHeight) {
            gameOver = true;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if(gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (keyProcessed) {
            // Ignore the Second Key Event for Simultaneous Presses
            return;
        }
        if(e.getKeyCode() == KeyEvent.VK_SPACE && gameOver) {
            snakeHead = new Tile(5,5);
            snakeBody = new ArrayList<Tile>();
            velocityX = 0;
            velocityY = 0;
            gameOver = false;
            gameLoop.start();
        }
        if(e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        }
        else if(e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        }
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
        keyProcessed = true;
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyProcessed = false;
    }
}
