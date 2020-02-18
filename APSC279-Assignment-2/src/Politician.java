public class Politician extends Person {        //Politician is a concrete class, which is a subclass of Person
    private String party;                       //Private instance variables, String of the Politician's political party

    public Politician(String name, Date birthDate, String gender, String party, double difficulty) {    //Constructor, all instance variables as parameters
        super(name, birthDate, gender, difficulty);                                                     //Super constructor, calls the constructor of parent Person
        this.party = party;                                                                             //Set parameters unique to Politician
    }

    public Politician(Politician copy) {            //Copy Constructor, overload of constructor with Politician parameter
        super(copy);                                //Invokes copy constructor of parent class Person
        this.party = copy.party;
    }

    public Entity clone() {                         //Returns a clone of an object, implemented for Politician
        return new Politician(this);          //Invokes copy constructor of Politician
    }

    public String toString() {                      //Returns all parameters of an entity as a formatted string
        return super.toString() + "\nParty: " + party;
    }

    public String entityType() {                    //Returns formatted string of which subclass of entity the object is, implemented for Politician
        return "This entitiy is a politician!";
    }
}
