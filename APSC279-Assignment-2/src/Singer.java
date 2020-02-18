public class Singer extends Person {        //Singer is a concrete class, which is a subclass of Person
    private String debutAlbum;              //Private instance variables, String of the Singer's debut album title
    private Date debutAlbumReleaseDate;     //Date of the Singer's debut album release date

    public Singer(String name, Date birthDate, String gender, String debutAlbum, Date debutAlbumReleaseDate, double difficulty) {   //Constructor, all instance variables as parameters
        super(name, birthDate, gender, difficulty);                                                                                 //Super constructor, calls the constructor of parent Person
        this.debutAlbum = debutAlbum;                                                                                               //Set parameters unique to Politician
        this.debutAlbumReleaseDate = new Date(debutAlbumReleaseDate);                                                               //no privacy leak, date is mutable return a copy
    }

    public Singer(Singer copy) {                                                //Copy Constructor, overload of constructor with Politician parameter
        super(copy);                                                            //Invokes copy constructor of parent class Person
        this.debutAlbum = copy.debutAlbum;
        this.debutAlbumReleaseDate = new Date(copy.debutAlbumReleaseDate);      //no privacy leak, date is mutable return a copy
    }

    public Entity clone() {                         //Returns a clone of an object, implemented for Singer
        return new Singer(this);              //Invokes copy constructor of Singer
    }

    public String toString() {                      //Returns all parameters of an entity as a formatted string
        return super.toString() + "\nDebut Album: " + debutAlbum + "\nRelease Date: " + debutAlbumReleaseDate;
    }

    public String entityType() {                    //Returns formatted string of which subclass of entity the object is, implemented for Politician
        return "This entitiy is a singer!";
    }
}
