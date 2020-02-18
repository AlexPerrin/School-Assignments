# ELEC 279 - Winter 2019 Introduction to Object-Oriented Programming Assignment I
**Due Date: Tuesday February 12th, 2019 at 9:00 PM.**

**Objective:** Practice designing classes including instance variables, constructors, and
methods; practice basic Java programming such as flow of control and Java I/O.

**Problem:** GuessMaster—A Date Guessing Game
Let’s create a game that allows users to guess the birthday of a named entity, e.g.,
a celebrity, country, or prominent leader. It requires ahuman playertrying to guess
the correct birthday held secretly in the computer program (e.g. Justin Trudeau was
born on December 25, 1971). To start, the program welcomes thehuman playerand
inform him/her whose birthday to guess. More specifically, from a set of entities saved
or hard-coded in the program, the program randomly picks one entity (e.g., a celebrity
or country) and print the name on the screen and ask the user to guess the birth date.
After reading the answer from console (no need for graphic user interface (GUI) at this
point), the program responds with“Incorrect. Try an earlier date.”or“Incorrect. Try a
later date.”, if the user’s guess is incorrect, or“BINGO. You got it!”if the user is right.
The user can type “quit” to exit the game.


**Working Flow**
- You may start with creating a folder, e.g., GuessMaster, in your computer to host
    this project.
- You will need to useDate.javathat we discussed in the last lecture of week 3; the file
    will be delivered together with this assignment handout. CopyDate.javato your
    GuessMaster folder. In Date.java, add one more constructor public Date(String
    strDate), which can initialize the class by taking into a string like “12/25/1971”.
    The constructor may use thesplit method inString, the static methodparseInt in
    the wrapper classInteger, and thesetDate()mutator in the Date Class, but you
    should feel free to use whatever classes you would like to implement this constructor.
- Define the classEntity, which can hold the information of a named entity such as a
    person or a country. The class has two instance variables as shown below:
       - private String name;
       - private Date born;

Create all needed constructors, mutators, and accessors. Create acopy constructor
for Entity that does not have a privacy leak issue. Define a toString() method
that return the content of an entity as a string, e.g.,“Celine Dion, born on March
30, 1968” or“Eiffel Tower, born on March 31, 1889”. Define a methodboolean
equals(Entity entity)to compare content of two entities—refer to our lectures or
textbook for examples.

- Define a classGuessMaster, which has two instance variables:
    - private int numberOfCandidateEntities;
    - private Entity[ ] entities;

The first variable indicates how many candidate entities are used in this game,
andentitiesholds these entities. Although one may use other data types such as
ArrayList to hold entities, let’s useEntity[ ] entitiesin this assignment.

- InGuessMaster, create proper constructors, accessors, and methods. Feel free
    to define GuessMaster as you would like. The following methods are just some
    suggestions you may consider.
       - public void addEntity(Entity entity): This method add a new entity intoenti-
          ties;
       - public void playGame(Entity entity): Given an entity, play the guessing game
          with regard to that entity. Note that you may want to use theprecedes(Date
          otherDate)method.
       - public void playGame(int entityInd): Given an entity index (e.g., 1), use the in-
          dex to fetch the (e.g., 2nd) entity fromentitiesand play the guessing game with
          regard to that entity. This method may invoke thepublic void playGame(Entity
          entity)defined above.


- public void playGame(): This method will generate a random number first,
    which will be then used to randomly pick an entity fromentities. The method
    then invokespublic void playGame(int entityInd)to play a game. Note that
    the game will keep going until the user type “quit” as described above.
- int genRandomEntityInd():This is helping method used bypublic void playGame().
- Themainmethods. This is the entrance of the game. You can initialize the
    game and start to play with it. We will test your code with the follow initial-
    ization. But please feel free to add whatever entities you want your users to
    play with.
```
Entity trudeau = new Entity(”Justin Trudeau”, new Date(”December”, 25,
1971));
Entity dion = new Entity(”Celine Dion”, new Date(”March”, 30, 1968));
Entity usa = new Entity(”United States”, new Date(”July”, 4, 1776));
GuessMaster gm = new GuessMaster();
gm.addEntity(trudeau);
gm.addEntity(dion);
gm.addEntity(usa);
```
After the initialization, you can start your game.

**GAME RULES**
- To start, greet thehuman playerand inform him/her whose birthday to guess;
    e.g., you can print the name of a celebrity or country on the screen. The user tries
    to guess by typing his/her answer in the following format: 12/25/1971. Feel free to
    allow users to type different formats of dates but we will evaluate your code using
    the above format.
- Game continues to accept user’s guesses until the player gets the correct birthday.
    If a guess is incorrect, the system should output either“Incorrect. Try an earlier
    date.”or“Incorrect. Try a later date”. If a user’s guess is correct, you can output
    “BINGO. You got it!!”. Then, the system randomly pick the next question from
    the entity array. The user can type “quit” to exit the game anytime when he was
    asked a question.

**HAPPY CODING WITH JAVA USING OOP :)**
