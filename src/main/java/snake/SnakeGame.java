package snake;

import java.util.LinkedList;
import java.awt.Point;
import java.util.Random;

public class SnakeGame {
    public static final int TILE_SIZE = 20;
    public static final int COLS = 30;
    public static final int ROWS = 25;

    private final LinkedList<Point> snake;
    private final Random random;

    private Point food;
    private Direction direction;
    private boolean gameOver;
    private int score;

    public SnakeGame(){
        snake = new LinkedList<>();
        random = new Random();

        reset();
    }

    public void reset(){
        snake.clear();

        snake.add(new Point(COLS / 2, ROWS / 2));
        snake.add(new Point(COLS / 2 - 1, ROWS / 2));
        snake.add(new Point(COLS / 2 - 2, ROWS / 2));
        
        direction = Direction.RIGHT;
        score = 0;
        gameOver = false;
        
        spawnFood();
    }

    public void update(){
        if(gameOver){
            return;
        }

        Point head = snake.getFirst();
        Point newHead = new Point(head);

        if (direction == Direction.UP) {
            newHead.y--;
        } else if (direction == Direction.DOWN) {
            newHead.y++;
        } else if (direction == Direction.LEFT) {
            newHead.x--;
        } else if (direction == Direction.RIGHT) {
            newHead.x++;
        }

        if(hitsWall(newHead) || hitsSelf(newHead)){
            gameOver = true;
            return;
        }

        snake.addFirst(newHead);

        if(newHead.equals(food)){
            score+=1;
            spawnFood();
        }else{
            snake.removeLast();
        }
    }

    public void changeDirection(Direction newDirection){
        if(newDirection == null){
            return;
        }

        if(!newDirection.isOpposite(direction)){
            direction = newDirection;
        }
    }

    private boolean hitsWall(Point point){
        return point.x < 0
                || point.x >= COLS
                || point.y < 0
                || point.y >= ROWS; 
    }

    private boolean hitsSelf(Point point){
        return snake.contains(point);
    }

    private void spawnFood() {
        do {
            int x = random.nextInt(COLS);
            int y = random.nextInt(ROWS);
            food = new Point(x, y);
        } while (snake.contains(food));
    }

    public LinkedList<Point> getSnake() {
        return snake;
    }
    public Point getFood() {
        return food;
    }
    public boolean isGameOver() {
        return gameOver;
    }
    public int getScore() {
        return score;
    }
}
