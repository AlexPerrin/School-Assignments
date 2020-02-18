public abstract class Entity {		//Entity is an abstract class, this gives all subclasses common methods and variables
	private String name;			//Private instance variables, String of entity's full name
	private Date born;				//Date of the entities date of birth
	private double difficulty;		//Double used to calculate tickets awarded, values 0-1 inclusively

	public Entity(String name, Date birthDate, double difficulty) {		//Constructor, all instance variables as parameters
		this.name = name;
		this.born = new Date(birthDate);	//no privacy leak, date is mutable return a copy
		this.difficulty = difficulty;
	}

	public Entity(Entity entity) {			//Copy Constructor, overload of constructor with Entity parameter
		this.name = entity.name;
		this.born = new Date(entity.born); //no privacy leak, date is mutable return a copy
	}

	public String getName() {		//Accessor, returns string name
		return name;
	}

	public Date getBorn() {			//Accessor, returns Date born
		return new Date(born);		//no privacy leak, date is mutable return a copy
	}

	public String toString() {		//Returns all parameters of an entity as a formatted string
		return "Name: " + name + "\n" + "Born at: " + born.toString() + "\n";
	}

	public int getAwardedTicketNumber() {		//Returns the amount of tickets awarded for a correct guess
		return (int)(difficulty*100);			//Difficulty is a double so must be type casted to an int type (removes the .0)
	}

	public abstract String entityType();		//Returns formatted string of which subclass of entity the object is, abstract to be implemented by the subclass

	public abstract Entity clone();				//Returns a clone of an object which is a subclass of entity, abstract to be implemented by the subclass

	public String welcomeMessage() {			//Returns a formatted string to be displayed at beginning of the game, includes entity type
		return "Welcome! Let's start the game! " + entityType() + "\n";
	}

	public String closingMessage() {			//Returns a formatted string to be displayed at end of the game, includes entity type
		return "Congratulations!  The detailed information of the entity you guess is:\n" + toString();
	}
}

