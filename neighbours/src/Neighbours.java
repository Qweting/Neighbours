import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.awt.*;
import java.util.*;

import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import static java.lang.System.exit;
import static java.lang.System.out;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then method start() far below.
 * - To test methods uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {

    class Actor {
        final Color color;        // Color an existing JavaFX class
        boolean isSatisfied;      // false by default

        Actor(Color color) {      // Constructor to initialize
            this.color = color;
        }  // Constructor, used to initialize
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used directly in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        double threshold = 0.7;
        int size = getSize(world);
        Actor[] getNeighbours;
        Actor[] notSatisfied = new Actor[size / 2];
        int increment = 0;

        for (int row = 0; row < world.length; row++) {
            for (int col = 0; col < world.length; col++) {
                if (world[row][col] != null) {
                    {getNeighbours = searchNeighbours(world, row, col);
                        if (threshold <= getThreshhold(getNeighbours, world[row][col]))
                            world[row][col].isSatisfied = true;
                        else {
                            world[row][col].isSatisfied = false;
                            notSatisfied[increment++] = world[row][col];
                        }
                    }
                }
            }
        }

        Actor[][] satisfied = notSatisfied(world);
    isSatisfied(notSatisfied, satisfied);

    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime
    // That's why we must have "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        //test();    // <---------------- Uncomment to TEST, see below!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (must be a square)
        int nLocations = 90_000;   // Should also try 90 000
        Actor[] arr = creation(nLocations, dist);
        shuffle(arr);
        world = origMatrix(arr);

        // Should be last
        fixScreenSize(nLocations);
    }


    public Actor[][] isSatisfied(Actor[] unSatisfied, Actor[][] satisfied) {
        Random rand = new Random();

        Actor[] noNull = Arrays.stream(unSatisfied).filter(Objects::nonNull).toArray(Actor[]::new);

        if (noNull.length != 0) {
            for (Actor nonSatisfied : noNull) {
                while(true){
                int randRow = rand.nextInt(satisfied.length);
                int randCol = rand.nextInt(satisfied.length);
                if (satisfied[randRow][randCol] == null) {
                    satisfied[randRow][randCol] = nonSatisfied;
                    break;
                }}
            }

        }
        return satisfied;
    }


    public Actor[][] notSatisfied(Actor[][] satisfied) {

        for (int i = 0; i < satisfied.length; i++) {
            for (int j = 0; j < satisfied.length; j++) {
                if (satisfied[i][j] != null && !satisfied[i][j].isSatisfied) {
                    satisfied[i][j] = null;
                }
            }
        }
        return satisfied;
    }

    public double getThreshhold(Actor[] neighbours, Actor color) {
        int sameColor = 0;
        int oppositeColor = 0;

        for (Actor object : neighbours) {
            if (object == null) {
            }
            else if (object.color == color.color)
                sameColor += 1;
            else
                oppositeColor += 1;
        }

        return (double) sameColor / (sameColor + oppositeColor);

    }

    public Actor[] searchNeighbours(Actor[][] actor, int row, int col) {

        int increment = 0;
        Actor[] neighbours = new Actor[8];
        int size = getSize(actor);

        for (int i = row-1; i < row+2; i++) {
            for (int j = col-1; j < col+2; j++) {
                if (!isValidLocation(getSize(actor), i, j) || j == col && i == row || j > sqrt(size) - 1
                        || j < 0 || i > sqrt(size) - 1 || i < 0) {
                }else
                    neighbours[increment++] = actor[i][j];
            }
        }
        return neighbours;
    }

    public int getSize(Actor[][] arr) {
        Actor[] array = Arrays.stream(arr)
                .flatMap(Arrays::stream)
                .toArray(Actor[]::new);
        return array.length;
    }


    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size && 0 <= col && col < size;
    }

    // ----------- Utility methods -----------------

    /**
     * converts a 2-D array to one dimensions.
     **/
    public Actor[] creation(int nLocations, double[] dist) {
        Actor[] arr = new Actor[nLocations];

        for (int i = 0; i < nLocations; i++) {
            if (i < nLocations * dist[0])
                arr[i] = new Actor(Color.PURPLE);
            else if (i < nLocations * (dist[0] + dist[1]))
                arr[i] = new Actor(Color.BLACK);
            else
                arr[i] = null;
        }
        return arr;
    }

    public Actor[] shuffle(Actor[] actors) {
        Random random = new Random();
        for (int i = 0; i < actors.length; i++) {
            int j = random.nextInt(i + 1);
            Actor temp = actors[i];
            actors[i] = actors[j];
            actors[j] = temp;
        }
        return actors;
    }

    public Actor[][] origMatrix(Actor[] arr) {
        int oneDSize = (int)sqrt(arr.length);
        Actor[][] arr2 = new Actor[oneDSize][oneDSize];
        int index = 0;
        for (int i = 0; i < oneDSize; i++) {
            for (int j = 0; j < oneDSize; j++) {
                arr2[i][j] = arr[index++];
            }
        }
        return arr2;
    }




    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work. Important!!!!
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {new Actor(Color.RED), new Actor(Color.RED), null},
                {null, new Actor(Color.BLUE), null},
                {new Actor(Color.RED), null, new Actor(Color.BLUE)}
        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));   // This is a single test
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));




        exit(0);
    }

    // ******************** NOTHING to do below this row, it's JavaFX stuff  **************

    double width = 500;   // Size for window
    double height = 500;
    final double margin = 50;
    double dotSize;

    void fixScreenSize(int nLocations) {
        // Adjust screen window
        dotSize = (double) 9000 / nLocations;
        if (dotSize < 1) {
            dotSize = 2;
        }
        width = sqrt(nLocations) * dotSize + 2 * margin;
        height = width;
    }

    long lastUpdateTime;
    final long INTERVAL = 10_000_000;


    @Override
    public void start(Stage primaryStage) throws Exception {

        // Build a scene graph
        Group root = new Group();
        Canvas canvas = new Canvas(width, height);
        root.getChildren().addAll(canvas);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Create a timer
        AnimationTimer timer = new AnimationTimer() {
            // This method called by FX, parameter is the current time
            public void handle(long now) {
                long elapsedNanos = now - lastUpdateTime;
                if (elapsedNanos > INTERVAL) {
                    updateWorld();
                    renderWorld(gc);
                    lastUpdateTime = now;
                }
            }
        };


        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Simulation");
        primaryStage.show();
        primaryStage.setResizable(false);

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = (int) (dotSize * col + margin);
                int y = (int) (dotSize * row + margin);
                if (world[row][col] != null) {
                    g.setFill(world[row][col].color);
                    g.fillOval(x, y, dotSize, dotSize);
                }
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
