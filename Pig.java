import java.util.Random;
import java.util.Scanner;

import static java.lang.System.*;

/*
 * The Pig game
 * See http://en.wikipedia.org/wiki/Pig_%28dice_game%29
 *
 */
public class Pig {

    public static void main(String[] args) {
        new Pig().program();
    }

    // The only allowed instance variables (i.e. declared outside any method)
    // Accessible from any method
    final Scanner sc = new Scanner(in);
    final Random rand = new Random();

    void program() {
        test();                 // <-------------- Uncomment to run tests!

        final int winPts = 20;    // Points to win (decrease if testing)
        Player[] players;         // The players (array of Player objects)
        Player current;            // Current player for round (must use)
        boolean aborted = false;   // Game aborted by player?


        players = getPlayers();  // ... this (method to read in all players)

        welcomeMsg(winPts);
        statusMsg(players);
        current = getCurrentPlayer(players);   // TODO Set random player to start - Done by me

        // TODO Game logic, using small step, functional decomposition - Done by me
        while (!aborted){
            String choice = getPlayerChoice(current);

            if (choice.equals("r")){
                int roll = rollDice();
                current.roundPts += roll;
                roundMsg(roll, current);

                if (roll == 1){
                    current.roundPts = 0;
                    statusMsg(players);
                    current = getNextPlayer(players, current);
                } else if (current.totalPts + current.roundPts >= winPts) {
                    current.totalPts += current.roundPts;
                    break;
                }

            } else if (choice.equals("n")) {
                current.totalPts += current.roundPts;
                current.roundPts = 0;
                statusMsg(players);
                if (current.totalPts >= winPts){
                    break;
                }
                current = getNextPlayer(players, current);
            } else if (choice.equals("q")) {
                aborted = true;
                break;
            }


        }


        gameOverMsg(current, aborted);
    }

    // ---- Game logic methods --------------

    // TODO - Done by me
    Player getCurrentPlayer(Player[] players){
        int random = rand.nextInt(players.length);
        return players[random];
    }
    int rollDice(){
        return rand.nextInt(6) + 1;
    }

    Player getNextPlayer(Player[] players, Player current){
        int i = 0;
        while (players[i] != current){
            i++;
        }
        if ((i+1) < players.length){
            current = players[i+1];
        } else {
            current = players[0];
        }
        return current;
    }
    boolean isWinner(Player[] players, int winPts){
        for (Player p : players){
            if (p.totalPts >= winPts){
                return true;
            }
        }
        return false;
    }



    // ---- IO methods ------------------

    void welcomeMsg(int winPoints) {
        out.println("Welcome to PIG!");
        out.println("First player to get " + winPoints + " points will win!");
        out.println("Commands are: r = roll , n = next, q = quit");
        out.println();
    }

    void statusMsg(Player[] players) {
        out.print("Points: ");
        for (int i = 0; i < players.length; i++) {
            out.print(players[i].name + " = " + players[i].totalPts + " ");
        }
        out.println();
    }

    void roundMsg(int result, Player current) {
        if (result > 1) {
            out.println("Got " + result + " running total are " + current.roundPts);
        } else {
            out.println("Got 1 lost it all!");
        }
    }

    void gameOverMsg(Player player, boolean aborted) {
        if (aborted) {
            out.println("Aborted");
        } else {
            out.println("Game over! Winner is player " + player.name + " with "
                    + player.totalPts  + " points");
        }
    }

    String getPlayerChoice(Player player) {
        out.print("Player is " + player.name + " > ");
        return sc.nextLine();
    }

    Player[] getPlayers() {
         // TODO - Done by me
        out.println("How many players? >");
        int n = sc.nextInt();
        sc.nextLine();

        Player[] players = new Player[n];
        for (int i = 0; i < n; i++){
            players[i] = new Player();
            out.println("Whats the name for player " + (i+1)+ " " + "?" + " " + " >");
            players[i].name = sc.nextLine();
        }

        return players;
    }

    // ---------- Class -------------
    // Class representing the concept of a player
    // Use the class to create (instantiate) Player objects
    class Player {
        String name;     // Default null
        int totalPts;    // Total points for all rounds, default 0
        int roundPts;    // Points for a single round, default 0
    }

    // ----- Testing -----------------
    // Here you run your tests i.e. call your game logic methods
    // to see that they really work (IO methods not tested here)
    void test() {
        // This is hard coded test data
        // An array of (no name) Players (probably don't need any name to test)
        Player[] players = {new Player(), new Player(), new Player()};

        // TODO Use for testing of logcial methods (i.e. non-IO methods) - Done by me

        //getCurrentPlayer
        players[0].name = "A";
        players[1].name = "B";
        players[2].name = "C";

        out.println(getCurrentPlayer(players).name);

        // rollDice
        int r_test = rollDice();
        out.println(r_test > 0 && r_test < 7);

        // getNextPlayer
        Player current = players[0];
        out.println(getNextPlayer(players,current) == players[1]);

        current = players[2];
        out.println(getNextPlayer(players,current) == players[0]);

        // isWinner
        players[0].totalPts = 10;
        players[1].totalPts = 15;
        players[2].totalPts = 20;

        out.println(isWinner(players, 20));





        exit(0);   // End program
    }
}



