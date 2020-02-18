# ELEC 279 - Winter 2019 Introduction to Object Oriented Programming Assignment II
**Due Date:Tuesday March 12th, 2019 at 9:00 PM.**

**Objective**
Inheritance, abstract classes, and polymorphism (late binding).

**Problem: GuessMaster Version 2**
Assignment II is based on assignment I. You can continue with your assignment I
source code or use the starting code we provide together with this assignment. In the
former case, you may consider saving a copy of your assignment I code somewhere else
for future reference.
This new version of game, GuessMaster 2.0, is played with the same rule as in as-
signment I. However, you will need to restructure the classes you wrote in assignment I
and make them a little bit more complicated to practise OOP programing with abstract
methods, abstract classes, inheritance, polymorphism (late binding), andclonemethods.
Specifically, you need to provide a welcome message and a closing message to your user.
In addition, your user will be awarded some tickets if his or her guess is correct. When
you are working on the assignment, think about the basic concepts of inheritance and
polymorphism that you learn in the course and how they are implemented in the your
code.

**A Recap of Assignment I**
In assignment I, you designed a simple game that allows users to guess the birthday of
a named entity, e.g., a celebrity, country, or prominent leader. It requires a player to guess
the correct birthday held secretly in the computer program (e.g. Justin Trudeau was born
on December 25, 1971). To start, the program welcomes the player and inform him/her
whose birthday to guess. More specifically, from a set of entities saved or hard-coded in
the program, the program randomly picks one entity (e.g., a celebrity or country) and
print the name on the screen and ask the user to guess the birth date. After reading
the answer from console, the program responds with“Incorrect. Try an earlier date.”or
“Incorrect. Try a later date.”, if the user’s guess is incorrect, or“BINGO. You got it!”if
the user is right. The user can type “quit” to exit the game.

**Assignment II Instructions**
- Modify yourEntityclass to be anabstractclass. (Think about why this class is
    defined as an abstract class.)

(i) Add an attribute (i.e., instance variable)difficultyof typedouble. It is used to
keep the difficulty of guessing an entity, which will be initialized with the code
provided at the end of this handout. Difficulty will also be used to calculate the
number of tickets a user will receive. The range ofdifficulty’s value is between
0 and 1, inclusively. (Note that all instance variables defined in this assignment
are recommended to beprivateas we discussed in the course, unless otherwise
specified.)

(ii) Implement proper constructors, including a copy constructor. (Note that when
implementing constructors in the assignment and in your future programming,
you should avoid privacy leak wherever applicable.)

(iii) Implement a public methodgetAwardedTicketNumber that returns the number
of tickets (of typeinteger) if a user correctly guesses an entity’s birth date. The
ticket number is computed as difficulty*100; i.e., if the difficulty of an entity
is 1 and if a user correctly guesses its birth date, she receives 100 tickets. If
the difficulty is 0.5, the user receives 50 tickets. The value of difficulty*100 is
of type double, so you need to type-cast it to be an integer since ticket number
is an integer. Note tha tget AwardedTicketNumber will be inherited (but not
overridden) in Entity’s subclasses and it should return different ticket number
depending on the specific type of entity.

(iv) Implement a proper toString method.

(v) Declare an abstract method entityType. The method takes no parameter
and returns a String. It will be implemented in the derived concrete classes to
return a short description of the entity type, as described later in this handout.

(vi) Declare an abstract method clone. The method takes no parameter and its
return type isEntity.

(vii) Define a welcomeMessage method. The method takes no parameter and it
should return a string of welcome message: “Welcome! Let’s start the game!
This entity is a XXX”, in which XXX is a entity type. (An example of output
is shown in exampleOutput.pdf that is provided together with the assignment.)
The welcomeMessage method needs to invoke entityType to generate a proper
welcome message.

(viii) Define a closingMessage method. The method takes no parameter and it should
return a string of closing message for each round of correct guess: “Congratu-
dations! The detailed information of the entity you guess is: XXX”, in which
XXX is the detailed information of the entity being guessed. The closingMes-
sage should invokes toString. An example of output is shown in example Out-
put.pdf.

**- Design a class,Country, which is a concrete subclass ofEntity.**

(i) The Country lass has an instance variable,capital, of typeString.

(ii) Implement proper constructors, including a copy constructor, that can initialize

instance variables inCountry and those in its parent class. To do so, you need
to use a super constructor. The copy construct inCountry should invoke the
copy construct in its parent class.
(iii) Define a simple clone method using the copy constructor. The return type is
Country.

(iv) Implement the proper toStringmethod, which should invoke toString in the
parent class.

(v) Since Country is a concrete subclass, you need to implement the entityType
method in it, which returns: “This entity is a country!”. (Please ensure your
returned string, including the punctuation mark “!”, is exactly same as what
is shown here, which will help TAs mark your code efficiently.)


**- Design a class,Person, which is another concrete subclass ofEntity.**

(i) ThePersonclass has an instance variablegender, which is aString.

(ii) Follow step (ii)–(v) in defining theCountryclass to define the corresponding
methods appropriate forPerson.
- Create two derived classesSinger andPolitician; both are subclasses ofPerson

(i) Singer has two instance variables: String debutAlbum andDate debutAlbum-
ReleaseDate. debutAlbumholds the title of the debut album of the singer and
debutAlbumReleaseDatesaves the date when the debut album was released.

(ii)Politicianhas one attribute:party, which is of typeString, saving the political
party of a politician.

(iii) Follow step (ii)–(v) in defining theCountryclass to define the corresponding
methods appropriate for the two classes here.

- User will win tickets if his or her guess is correctly. For every round of guess if
    the user is correct, print the number of tickets she received in this round as well as
    the total number of tickets she received so far (including whatever she received in
    this round) since the game started, e.g., “You won 50 tickets in this round. The
    total number of your tickets is 250.” The number of tickets awarded in each round
    depends on the entity and is initialized when the entity is created, as described
    in the code given at the end of this handout. Modify theGuessMaster class to
    implement this—you need to callgetAwardedTicketNumber. Also you need to invoke
    welcomeMessageandclosingMessageto print welcome and closing messages before
    and after each round of correct guess, respectively. An example of output is shown
    in the file exampleOutput.pdf provided together with this assignment One hint:
    you also need to modify theaddEntity method a bit, in which you need to invoke
    a clone method. (Think about whether the originaladdEntity method provided in
    the starting code works for GuessMaster 2.0 or not. And Why?)
- We will use the following code to test assignment II:

```
// The last parameter of the follow line is the difficulty score described above.
Politician trudeau = new Politician(”Justin Trudeau”, new Date(”December”, 25,
1971), ”Male”, ”Liberal”, 0.25);

/*The firstDate parameter in the parameter in the following line is
the birth date of the singer and the secondDate parameter
is the release date of the debut album.*/
Singer dion = new Singer(”Celine Dion”, new Date(”March”, 30, 1961), ”Female”,
”La voix du bon Dieu”, new Date(”November”, 6, 1981), 0.5);

Person myCreator = new Person(”myCreator”, new Date(”September”, 1, 2000),”Fe-
male”, 1);
Country usa = new Country(”United States”, new Date(”July”, 4, 1776), ”Wash-
inton D.C.”, 0.1);

GuessMaster gm = new GuessMaster();
gm.addEntity(trudeau);
gm.addEntity(dion);
gm.addEntity(myCreator);
gm.addEntity(usa);

gm.playGame();
```

- An example of output is shown in the file exampleOutput.pdf provided with this
    assignment. Your output does not need to be exactly same as that in example-
    Output.pdf. But the information in the welcome message, closing message, and the
    ticket information should be correct.


**THIS SHOULD BE AN INTERESTING ASSIGNMENT. ENJOY IT :)**


