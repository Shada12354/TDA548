import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.*;

import static java.lang.Math.*;
import static java.lang.System.*;

/*
 *  Program to simulate segregation.
 *  See : http://nifty.stanford.edu/2014/mccown-schelling-model-segregation/
 *
 * NOTE:
 * - JavaFX first calls method init() and then the method start() far below.
 * - The method updateWorld() is called periodically by a Java timer.
 * - To test uncomment call to test() first in init() method!
 *
 */
// Extends Application because of JavaFX (just accept for now)
public class Neighbours extends Application {

    // Enumeration type for the Actors
    enum Actor {
        BLUE, RED, NONE   // NONE used for empty locations
    }

    // Enumeration type for the state of an Actor
    enum State {
        UNSATISFIED,
        SATISFIED,
        NA     // Not applicable (NA), used for NONEs
    }

    // Below is the *only* accepted instance variable (i.e. variables outside any method)
    // This variable may *only* be used in methods init() and updateWorld()
    Actor[][] world;              // The world is a square matrix of Actors

    // This is the method called by the timer to update the world
    // (i.e move unsatisfied) approx each 1/60 sec.
    void updateWorld() {
        // % of surrounding neighbours that are like me
        final double threshold = 0.7;
        // TODO Update logical state of world - Done by me
        List<int []> unsatisfied = new ArrayList<>();

        for(int row = 0; row < world.length; row++){
            for (int col = 0; col < world[row].length; col++){
                Actor actor = world[row][col];
                if (actor == Actor.NONE){
                    continue;
                }
               if (!isSatisfied(world, row, col, threshold)){
                   unsatisfied.add(new int[]{row,col});
               }
            }
        }
        moveActors(world,unsatisfied);

    }

    // This method initializes the world variable with a random distribution of Actors
    // Method automatically called by JavaFX runtime (before graphics appear)
    // Don't care about "@Override" and "public" (just accept for now)
    @Override
    public void init() {
        test();    // <---------------- Uncomment to TEST!

        // %-distribution of RED, BLUE and NONE
        double[] dist = {0.25, 0.25, 0.50};
        // Number of locations (places) in world (square)
        int nLocations = 900;

        // TODO Create and populate world - Done by me
        world = createWorld(dist,nLocations);
        updateWorld();


        // Should be last
        fixScreenSize(nLocations);
    }


    //---------------- Methods ----------------------------

    // Check if inside world
    boolean isValidLocation(int size, int row, int col) {
        return 0 <= row && row < size &&
                0 <= col && col < size;
    }

    //Done by me:
    Actor[][] createWorld(double[] dist, int nLocations){
        int nRedDots = (int)(dist[0]*nLocations);
        int nBlueDots = (int)(dist[1]*nLocations);
        int nNone = nLocations - nBlueDots - nRedDots;
        int dimensions = (int) Math.sqrt(nLocations);
        Actor[][] world = new Actor[dimensions][dimensions];


        List<Actor> actors = new ArrayList<>();
        for (int i = 0; i < nRedDots; i++){
            actors.add(Actor.RED);
        }
        for (int i = 0; i < nBlueDots; i++){
            actors.add(Actor.BLUE);
        }
        for (int i = 0; i < nNone; i++){
            actors.add(Actor.NONE);
        }

        Collections.shuffle(actors);
        int index = 0;
        for (int row = 0; row < dimensions; row++){
            for (int col = 0; col < dimensions; col++){
                world[row][col] = actors.get(index);
                index++;
            }
        }
        return  world;

    }
    Boolean isSatisfied(Actor[][] world, int row, int col, double threshold){
        Actor actor = world[row][col];
        if (actor == Actor.NONE){
            return true;
        }

        int validNeighbours = 0, neighbours = 0;

        for (int dr = -1; dr <= 1; dr++){
            for (int dc = -1; dc <= 1; dc++){
                if (dr == 0 && dc == 0){
                    continue;
                }
                int r = row + dr;
                int c = col + dc;

                if (!isValidLocation(world.length, r,c)){
                    continue;
                }
                Actor neighbour = world[r][c];
                if (neighbour != Actor.NONE){
                    neighbours++;
                    if (neighbour == actor){
                        validNeighbours++;
                    }
                }
            }
        }
        return neighbours == 0 || (validNeighbours / (double) neighbours >= threshold);
    }



    void moveActors(Actor[][] world, List<int[]> unsatisfied){
        List<int[]> emptyLocations = new ArrayList<>();
        for (int r = 0; r < world.length; r++){
            for (int c = 0; c < world[r].length; c++){
                if (world[r][c] == Actor.NONE){
                    emptyLocations.add(new int []{r,c});
                }
            }
        }
        Collections.shuffle(emptyLocations);
        Random rand = new Random();

        for (int i = 0; i < unsatisfied.size() && !emptyLocations.isEmpty(); i++){
            int[] oldPosition = unsatisfied.get(i);
            int[] newPosition = emptyLocations.remove(rand.nextInt(emptyLocations.size()));

            Actor actor  = world[oldPosition[0]][oldPosition[1]];
            world[newPosition[0]][newPosition[1]] = actor;
            world[oldPosition[0]][oldPosition[1]] = Actor.NONE;
        }
    }



    // ------- Testing -------------------------------------

    // Here you run your tests i.e. call your logic methods
    // to see that they really work
    void test() {
        // A small hard coded world for testing
        Actor[][] testWorld = new Actor[][]{
                {Actor.RED, Actor.RED, Actor.NONE},
                {Actor.NONE, Actor.BLUE, Actor.NONE},
                {Actor.RED, Actor.NONE, Actor.BLUE}
        };
        double th = 0.5;   // Simple threshold used for testing

        int size = testWorld.length;
        out.println(isValidLocation(size, 0, 0));
        out.println(!isValidLocation(size, -1, 0));
        out.println(!isValidLocation(size, 0, 3));
        out.println(isValidLocation(size, 2, 2));

        // TODO More tests - Done by me
        //isSatisfied
        out.println(isSatisfied(testWorld,0,0,th));
        out.println(isSatisfied(testWorld,2,2,th));
        out.println(!isSatisfied(testWorld,0,0,0.7));
        out.println(!isSatisfied(testWorld,0,0, 0.8));

        //moveActors
        List<int[]> testList = new ArrayList<>();
        testList.add(new int[]{0,0});
        testList.add(new int[]{0,1});
        testList.add(new int[]{1,0});
        moveActors(testWorld,testList);

        for (int r = 0; r < testWorld.length; r++){
            for (int c = 0; c < testWorld[r].length; c++){
                out.println(isValidLocation(size,r,c));
            }
        }
        exit(0);
    }

    // Helper method for testing (NOTE: reference equality)
    <T> int count(T[] arr, T toFind) {
        int count = 0;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == toFind) {
                count++;
            }
        }
        return count;
    }

    // ###########  NOTHING to do below this row, it's JavaFX stuff  ###########

    double width = 400;   // Size for window
    double height = 400;
    long previousTime = nanoTime();
    final long interval = 450000000;
    double dotSize;
    final double margin = 50;

    void fixScreenSize(int nLocations) {
        // Adjust screen window depending on nLocations
        dotSize = (width - 2 * margin) / sqrt(nLocations);
        if (dotSize < 1) {
            dotSize = 2;
        }
    }

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
            public void handle(long currentNanoTime) {
                long elapsedNanos = currentNanoTime - previousTime;
                if (elapsedNanos > interval) {
                    updateWorld();
                    renderWorld(gc, world);
                    previousTime = currentNanoTime;
                }
            }
        };

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Segregation Simulation");
        primaryStage.show();

        timer.start();  // Start simulation
    }


    // Render the state of the world to the screen
    public void renderWorld(GraphicsContext g, Actor[][] world) {
        g.clearRect(0, 0, width, height);
        int size = world.length;
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                double x = dotSize * col + margin;
                double y = dotSize * row + margin;

                if (world[row][col] == Actor.RED) {
                    g.setFill(Color.RED);
                } else if (world[row][col] == Actor.BLUE) {
                    g.setFill(Color.BLUE);
                } else {
                    g.setFill(Color.WHITE);
                }
                g.fillOval(x, y, dotSize, dotSize);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
