public class Entity {
    private String name;
    private Date born;

    public Entity(String name, Date born) { //Constructor, with name string and date object passed as parameters
        this.name = name;
        this.born = born;
    }

    public Entity(Entity toCopy) {  //Copy constructor, with Entity to be copied passed as parameter
        this.name = toCopy.name;    //String is immutable, no privacy leak
        this.born = new Date(toCopy.born);     //Copy of Date object created to avoid privacy leak
    }

    public void setEntity(String name, Date born) { //Mutator, change parameters after object initialized
        this.name = name;
        this.born = born;
    }

    public String getName() {   //Accessor, returns the name string
        return name;
    }

    public Date getBorn() {     //Accessor, returns a copy of Date object to avoid privacy leak
        return new Date(born);
    }

    public String toString() {  //Returns a formatted string of the contents of an entity
        return String.format("%s, born on %s", name, born.toString());
    }

    public boolean equals(Entity otherEntity) {  //returns boolean true if an entity has equal parameters
        if (otherEntity == null)    //based of example from lectures
            return false;
        else
            return (name == otherEntity.getName() && born == otherEntity.getBorn());
    }
}