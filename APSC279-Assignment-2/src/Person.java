public class Person extends Entity {        //Person is a concrete class, which is a subclass of Entity
    private String gender;                  //Private instance variables, String of the Person's gender

    public Person(String name, Date birthDate, String gender, double difficulty) {      //Constructor, all instance variables as parameters
        super(name, birthDate, difficulty);                                             //Super constructor, calls the constructor of parent Entity
        this.gender = gender;                                                           //Set parameters unique to Person
    }

    public Person(Person copy) {            //Copy Constructor, overload of constructor with Person parameter
        super(copy);                        //Invokes copy constructor of parent class Entity
        this.gender = copy.gender;
    }

    public Entity clone() {                 //Returns a clone of an object, implemented for Person
        return new Person(this);      //Invokes copy constructor of Person
    }

    public String toString() {              //Returns all parameters of an entity as a formatted string
        return super.toString() + "Gender: " + gender;
    }

    public String entityType() {            //Returns formatted string of which subclass of entity the object is, implemented for Person
        return "This entitiy is a person!";
    }
}
