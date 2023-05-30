public class Country extends Entity {       //Country is a concrete class, which is a subclass of Entity
    private String capital;                 //Private instance variables, String of the country's capital

    public Country(String name, Date birthDate, String capital, double difficulty) {    //Constructor, all instance variables as parameters
        super(name, birthDate, difficulty);                                             //Super constructor, calls the constructor of parent Entity
        this.capital = capital;                                                         //Set parameters unique to Country
    }

    public Country(Country copy) {          //Copy Constructor, overload of constructor with Country parameter
        super(copy);                        //Invokes copy constructor of parent class Entity
        this.capital = copy.capital;
    }

    public Entity clone() {                 //Returns a clone of an object, implemented for Country
        return new Country(this);     //Invokes copy constructor of Country
    }

    public String toString() {              //Returns all parameters of an entity as a formatted string
        return super.toString() + "Capital: " + capital;
    }

    public String entityType() {            //Returns formatted string of which subclass of entity the object is, implemented for Country
        return "This entitiy is a country!";
    }
}
