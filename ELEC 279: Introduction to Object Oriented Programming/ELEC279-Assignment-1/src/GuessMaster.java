//Alex Perrin 20056483 February 12, 2019
//APSC279 Assignment 1

import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class GuessMaster {
    private int numberOfCandidateEntities;
    private Entity[] entities;

    public GuessMaster() {  //Constructor, create an instance of object without parameters
        numberOfCandidateEntities = 0;
        entities = new Entity[numberOfCandidateEntities];
    }

    public GuessMaster(int numberOfCandidateEntities) { //Constructor, overload to include initialize with numberOfCandidateEntities parameter
        this.numberOfCandidateEntities = numberOfCandidateEntities;
        this.entities = new Entity[numberOfCandidateEntities];
    }

    public GuessMaster(GuessMaster toCopy) { //Copy Constructor, uses accessor methods that avoid privacy leak
        this.numberOfCandidateEntities = toCopy.getNumberOfCandidateEntities();
        this.entities = toCopy.getEntities();
    }

    public void setGuessMaster(int numberOfCandidateEntities) {  //Mutator, set parameters after an object has been initialized
        this.numberOfCandidateEntities = numberOfCandidateEntities;
        this.entities = new Entity[numberOfCandidateEntities];
    }

    public int getNumberOfCandidateEntities() {     //Accessor, returns numberOfCandidateEntities parameter
        return numberOfCandidateEntities;           //returns an integer which is an immutable data type, no privacy leak
    }

    public Entity[] getEntities() {     //Accessor, returns array of entities parameter
        Entity[] newEntities = new Entity[numberOfCandidateEntities];   //Avoids privacy leak by creating a new entities array
        for(int i= 0; i < entities.length; i++) {                       //Copies the contents of the existing entities array to the new array,
            newEntities[i] = entities[i];                               //and returns the new array, Entity is a mutable type so a copy of entities
        }                                                               //must be return instead of the original to avoid a privacy leak.
        return newEntities;
    }

    public void addEntity(Entity entity) {      // This method adds a new entity into entities
        Entity[] newEntities = Arrays.copyOf(entities, numberOfCandidateEntities + 1);  //Creates a new array which is a copy of the original but +1 in length (Cannot change array length after initialized must create a new one)
        this.entities = newEntities; //Sets original array to our copy array +1 length
        this.entities[numberOfCandidateEntities] = entity; //Set last index (empty spot) to our new entity
        this.numberOfCandidateEntities++;  //Increment number of entities
    }

    public void playGame(Entity entity) {   //PlayGame method, uses entity parameter passed as to be guessed
        System.out.println("~~ Hello, welcome to GuessMaster! ~~");  //Prints greeting
        System.out.println(String.format("Guess the birthday of %s", entity.getName())); //Prints to be guessed
        System.out.println("Input in format month/day/year (Numbers Only!)");  //Prints expected input
        Scanner keyboard = new Scanner(System.in); //Creates scanner object for reading user inpu
        while(true) {   //Infinite loop, game will break itself when needed
            String input = keyboard.next();     //get user input
            if(input.equals("quit"))    //check for "quit"
                System.exit(0);
            Date guess = new Date(input);   //create a new date object for the date the user inputs
            if(entity.getBorn().equals(guess)) {    //use equals method to check if date guessed matches the date parameter of the entity
                System.out.println("BINGO. You got it!");
                playGame();         //Once correctly guessed start a new instance of the game with a random entity
            } else if(entity.getBorn().precedes(guess)) { //Used precede method to check if date guessed before or after entity date
                System.out.println("Incorrect. Try an earlier date.");
            } else if(guess.precedes(entity.getBorn())) {  //Reverse order of precedes to check later instead of earlier
                System.out.println("Incorrect. Try a later date.");
            }
        }
    }

    public void playGame(int entityInd) {   //playGame method overload to include passing int parameter
        playGame(entities[entityInd]);      //entityInd picks the entity at index of entities to be guessed
    }

    public void playGame() {    //playGame method overload to include passing no parameters
        playGame(genRandomEntityInd()); //Picks a random entity in entities to be guessed
    }

    int genRandomEntityInd() {  //generated a random integer from 0 to the objects numberOfCandidateEntities value
        Random rnd = new Random();
        return rnd.nextInt(numberOfCandidateEntities);
    }

    public static void main(String[] args) {
        Entity trudeau = new Entity("Justin Trudeau", new Date("December",25,1971));
        Entity dion = new Entity("Celine Dion", new Date("March", 30, 1968));
        Entity usa = new Entity("United States", new Date("July", 4, 1776));

        GuessMaster gm = new GuessMaster();
        gm.addEntity(trudeau);
        gm.addEntity(dion);
        gm.addEntity(usa);
        gm.playGame();
    }
}