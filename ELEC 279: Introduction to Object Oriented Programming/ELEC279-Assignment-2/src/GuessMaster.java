//Alex Perrin 20056483 APSC279 Assignment #2 GuessMaster2.0

import java.util.Random;
import java.util.Scanner;

public class GuessMaster {				//GuessMaster is a concrete class, which is used to hold an instance of the game
	private int numOfEntities;			//Private instance variables, int of number of entities added
	private Entity[] entities;			//list of Entities, used to store entities to be guessed
	private int tickets;				//int of tickets, holds tickets awarded from successful guesses
	
	public GuessMaster() {				//Constructor
		numOfEntities = 0;				//No entities yet
		entities = new Entity[100]; 	//max number of entities 100
	}

	public void addEntity(Entity entity) {		//Adds an Entity to entities list and increments numOfEntities
		entities[numOfEntities++] = entity;
	}
	
	public void playGame(int entityId) {		//Starts game by picking an entity in the list given by an index
		Entity entity = entities[entityId];
		playGame(entity);
	}
	
	public void playGame(Entity entity) {		//Starts game by passing an entity object to be guessed
		System.out.printf("***************************\n");
		System.out.printf(entity.welcomeMessage());
		System.out.printf("\nGuess %s's birthday\n", entity.getName());
		System.out.println("(mm/dd/yyyy)");

		Scanner scanner = new Scanner(System.in);

		while (true) {
			String answer = scanner.nextLine();
			answer = answer.replace("\n", "").replace("\r", "");

			if (answer.equals("quit")) {
				System.exit(0);
			}

			Date date = new Date(answer);

			if (date.precedes(entity.getBorn())) {
				System.out.println("Incorrect. Try a later date.");
			} else if (entity.getBorn().precedes(date)) {
				System.out.println("Incorrect. Try an earlier date.");
			} else {
				this.tickets += entity.getAwardedTicketNumber();		//On successful guess increment tickets instance variable by Tickets awarded
				System.out.println("**********Bingo!***********");
				System.out.println("You won " + entity.getAwardedTicketNumber() + " tickets in this round");	//Print tickets awarded
				System.out.println("The total number of your tickets is " + this.tickets);					//Print total amount of tickets
				System.out.println("***************************");
				System.out.println(entity.closingMessage());													//Prints closing message of Entity guessed
				break;
			}
		}
	}
	
	public void playGame() {							//Starts game by picking a random entity, primary way to initialize GameMaster
		while (true) {  								//infinite loop, game will keep playing until 'quit' is given
			int entityId = genRandomEntityId();
			playGame(entityId);
		}
	}

	public int genRandomEntityId() {			//Generates a random integer between 0-numOfEntities
		Random randomNumber = new Random();
		return randomNumber.nextInt(numOfEntities);
	}

	public static void main(String[] args) {
		System.out.println("=========================\n");
		System.out.println("     GuessMaster 2.0 \n");
		System.out.println("=========================\n");

		//Entity jTrudeau = new Entity("Justin Trudeau", new Date("December", 25, 1971));
		//Entity cDion = new Entity("Celine Dion", new Date("March", 30, 1968));
		//Entity usa = new Entity("United States", new Date("July", 4, 1776));

		Politician trudeau = new Politician("Justin Trudeau", new Date("December", 25,1971), "Male", "Liberal", 0.25);
		Singer dion = new Singer("Celine Dion", new Date("March", 30, 1961), "Female","La voix du bon Dieu", new Date("November", 6, 1981), 0.5);
		Person myCreator = new Person("myCreator", new Date("September", 1, 2000),"Female", 1);
		Country usa = new Country("United States", new Date("July", 4, 1776), "Washington D.C.", 0.1);

		GuessMaster gm = new GuessMaster();
		gm.addEntity(trudeau);
		gm.addEntity(dion);
		gm.addEntity(myCreator);
		gm.addEntity(usa);
		
		gm.playGame();
	}
}
