# ELEC 279 - Winter 2019 Introduction to Object Oriented Programming Assignment III
**Due Date:Tuesday April 9th, 2019 at 9:00 PM.**

**Objective**
Practice Android programming by implementing a game with GUI.

**Problem: GuessMaster Version 3**
Assignment III is based on assignments I and II. You can continue with your assign-
ment II source code or use the starting code we provide together with this assignment. In
the former case, you may consider saving a copy of your assignment II code somewhere
else for future reference.
This new version of game, GuessMaster 3.0, is played with the same rule as in as-
signment I and II. However, you will build a graphical user interface (GUI) in Android
Studio. You doNOTneed to modify the Date, Entity, Person, Politician, Country and
Singer classes. You will basically be reusing these classes in this assignment and modifying
GuessMaster.java class.
Moving from a Command Line program to GUI, you need to useAlertDialogin
Android to print welcome message and a closing message to your user. As in Assignment
II, your user will be awarded some tickets if his or her guess is correct. When a user’s
guess is correct or incorrect, you will useAlertDialogto inform the user. While working
on this assignment, keep in mind that all initial actions or instances of variables should
be implemented in theonCreate()method as discussed in class.

**Assignment III Instructions**
- Create a new project in Android studio call GuessMaster, keep the default SDK
    version and choose Phone and Tablet as the platforms. Name the activity java class
    as GuessMaster.java.
- When you create your project, specifyactivityguessactivityas the.xmllayout
    file name and modify it as follows (Create these items in this order to obtain layout
    similar to Fig. 1):

(i) Create a TextView withandroid:id=“@+id/ticket”to store user ticket later
in the game. Remember from our Android lectures thatidallows us to refer-
ence each item in our .xml file in the Java or Activity class file

(ii) Create an ImageView with an identityImageto display the image of an entity
later on

(iii) To print the name of current entity, add a TextView with identityName

(iv) To accept input from user, add an EditText with an idguessinput.

(v) Now, we need two Buttons to control user interaction with our game app. For
the layout in Fig. 1, addTableLayoutand inside theTableLayouttags, add
TableRow. Inside theTableRowtags, add one Button with idbtnGuess
and below it, add another Button with idbtnClear.

(vi) We are now done with the layout file. Make sure the main container of your
layout file isLinearLayout. If you get stuck, please, see sample code (Android
In-class Example) on onQ under Week 9.

- Before you modify the GuessMaster class, copy your .java classes (Date, Entity,
    Person, Politician, Country and Singer) from Assignment II into the current Android
    project directory, paste them in the src folder where you have GuessMaster.java

class under your package directory.Note:You might need to remove assignment II
package name and add the current package name for Android project in each class
you just copied.

- Next, modify yourGuessMaster class to be anactivity.java class thatextends
    AppCompatActivity specify it as the main acitivity class in the AndroidMan-
    ifest.xml file if Android studio hasn’t done that for you when you created the
    project.

(i) Specify the GuessMaster as an activity class using the AppCompatActivity as
thus: public class GuessMaster extends AppCompatActivity. If you
follow the new project creation dialog when creating your project, Android stu-
dio does this for you by default. If not, let your GuessMaster class extends App-
CompatActivity and addimport android.support.v7.app.AppCompatActivity;

(ii) Import the widget class object since we will be usingTextView,EditText,
ButtonandImageView. addimport android.widget.*;

(iii) In the class-level of your GuessMaster class, define the following view compo-
nents:
```
private TextView entityName;
private TextView ticketsum;
private Button guessButton;
private EditText userIn;
private Button btnclearContent;
private String userinput;
private ImageView entityImage;
String answer;
```

(iv) Copy your instances and variables from GuessMaster file in Assignment II and
paste in the class-level as well; your instances might look something like this:
```
private int numOfEntities;
private Entity[] entities;
private int[] tickets;
private int numOfTickets;
//Stores Entity Name
String entName;
int entityid = 0;
int currentTicketWon = 0;
```

(v) Now, let us create the entity objects right after the view components. You can
copy the codes from your Assignment II GuessMaster class and paste it right
before the onClickListener methods. Your code to create the objects might
look like this snippet:
```
Country usa= new Country(“United States”, new Date(“July”, 4, 1776), “Wash-
ingston DC”, 0.1);
Person myCreator= new Person(“myCreator”, new Date(”May”, 6, 1800), ”Male”,1);
Politician trudeau = new Politician(“Justin Trudeau”, new Date(”December”,25,1971),”Male”, ”Liberal”, 0.25);
Singer dion= new Singer(“Celine Dion”, new Date(“March”, 30, 1961), “Female”,
“La voix du bon Dieu”, new Date(“November”,6,1981),0.5);
Add final keyword so that we can access the class within.finalGuessMaster gm =
new GuessMaster();
```

(vi) Also, copy all methods (e.g. playGame(), addEntity(), genRandomEntityId()
etc.) and constructor (public GuessMaster() from GuessMaster file in As-
signment II and add them to the class-level of GuessMaster class in Android
project. Remove the while() loops in the two playGame() methods, we will
not that since most actions or events in the game will be triggered by Button
click. Make sure these methods are pasted outside of theonCreate()call-back
method.

(vii) Now, dive into the onCreate()method in your GuessMaster acitivity class.
In the setContentView( ) method, define the layout file to be used by your
activity:
```
//Set the xml as the activity UI view
setContentView(R.layout.activityguessmaster);
(viii) Right after the setContentView( ) method in theonCreate(), define the view
components defined earlier in ((iii)):
//Specify the button in the view
guessButton = (Button) findViewById(R.id.btnGuess); (Please note that the
R.id references the name of the Guess Button defined in the xml file)
//EditText for user input
userIn = (EditText) findViewById(R.id.guessinput);
//TextView for total tickets
ticketsum = (TextView) findViewById(R.id.ticket);
Define the remaining view components yourself.
```

(ix) Think about the importance ofonCreate()method in the life-cycle of an app
as discussed in class (refer to lecture slides on Android programming). While
you are still in theonCreate(), we need to define actions to perform when
the two Buttons defined earlier are clicked by the user. Define the actions as
follows each button using the setOnClickListener() method:
```
//OnClick Listener action for clear button
btnclearContent.setOnClickListener(newView.OnClickListener(){
@Override
public void onClick(View v){
changeEntity() ;
}
}) ;
//OnClick Listener action for submit button
guessButton.setOnClickListener(newView.OnClickListener(){
@Override
public void onClick(View v){
// playing game
playGame ( ) ;
}
}) ;
````

(x) Now, create the a method: changeEntity() to clear user entries from the
userInEditText and randomly choose another entity.
(xi)Setting Images: Each entity that is randomly selected, there should be a
corresponding image displayed in theImageViewdefined earlier. Download
the three (3) images provided with this assignment instruction. One is a USA
flag, one is Trudeau’s picture and the third one is Celine’s picture. Copy this
pictures to thedrawablefolder in your project directory. You should include
any picture for themyCreatorentity because you will need four (4) pictures
in total. Then, create a method calledImageSetter()that sets appropriate
picture for each entity when selected; you can useswitch..case or if..else
statements and useequals()method to compare strings.
(xii) In this version, you should print the welcome message once as opposed to
printing it multiple times in version 2. Create a method called welcome-
ToGame(Entity entity)and add the AlertDialog that shows your welcome
message as follows:
You can remove the Scanner instances since we are no longer using the con-
sole. To welcome user to your game, you will print the message to screen using
AlertDialoginstead of theSystem.out.println. To useAlertDialog,

- Import android.content.DialogInterface;
- Define your welcome message inside welcomeToGame(Entity Entity) as
    follows:

```
//Welcome Alert
AlertDialog.Builderwelcomealert =
new AlertDialog.Builder(GuessMasterActivity.this);
//System.out.println(”(mm/dd/yyyy )”);
welcomealert.setTitle(”GuessMaster Gamev3”);
welcomealert.setMessage(entity.welcomeMessage()) ;
welcomealert.setCancelable(false); //No Cancel Button
welcomealert.setNegativeButton(”STARTGAME” , new);
DialogInterface.OnClickListener(){
@Override
public void onClick(DialogInterfacedialop, int which) {
Toast.makeText(getBaseContext(),”Game is Starting...Enjoy” ,Toast.LENGTHSHORT).show();
}
}) ;
//Show Dialog
AlertDialogdialog = welcomealert.create();
dialog.show() ;
```

(xiii) Modify your playGame(Entity Entity) where the game engine is located. First,
remove thewhile(true)except for its contents. We do need not a while() loop
here. As long as the activity is the current user view, the playGame(Entity
Entity) method will be called every time a user clicks on the guessButton.
While inside this method, remove the Scanner.nextLine() since we are not
getting input from command line. Print the entity name to the entityName
TextView and get the input from the EdiText userIn:
```
//Name of the entity to be guessed in the entityName textview
entityName.setText(entity.getName());
//Get Input from the EdiText
answer = userIn.getText().toString();
answer = answer.replace(”\n”,” ”).replace(”\r ”,” ”) ;
Date date =new Date(answer) ;
```

- For the two incorrect cases shown below in the playGame(Entity Entity) , create
    AlertDialog for each. Set their titles as “Incorrect.” The alert message for the first
    one should be “Try a later date,” and “Try an earlier date” for the second one.
    Then, use setNegativeButton(“Ok”, new ...) for each dialog.
```
       //Check User Date Input
          if (date.precedes(entity.getBorn())) {
             // System.out.println(”Incorrect. Try a later date.”);
} elseif(Entity.getBorn().precedes(date)){
       } else {
tickets[numOfTickets++] = Entity.getAwardedTicketNumber();
for(int i = 0 ; i < 100 ; i ++) {
totaltik = totaltik + tickets[i];
}
//Use alert here to l e t user know they have won
// Call the ContinueGame() method inside the onClick() method
// of the DialogInterface.
}
```
- After the if...else block, use AlertDialog to inform users that they have won. Use
    the setNegativeButton(”Continue”, new ...) to continue playing the game. set title
    as ”You won” and message should be “BINGO! ”+ Entity.closingMessage(). In
    the onClick() method of the setNegativeButton(), make sure you add the awarded
    ticket to the Toast.makeText() using the getAwardedTicketNumber() method in
    Entity class.
- Lastly, using the setText() method update the ticketsum TextView with the to-
    tal ticket obtained. To ensure that the gameContinues, create a method called
    ContinueGame()with the below sample code and call this method in the if...else
    statement that checks if user wins. Logically, the ContinueGame() method should
    be called after a user wins. Call the ContinueGame() method inside the onClick()
    method of the DialogInterface.
```
//Continue Game Method− This method gets another Entity randomly
//Updates the Image view and the Entity Name Field
public void ContinueGame ( ){
entityid = genRandomEntityId ( );
Entityentity = entities[entityid];
entName = entity.getName();
// Call the ImageSetter method
ImageSetter();
// Print the name of the entity to be guessed
// in the entityName textview
entityName.setText(entName);
// Clear Previous Entry
userIn.getText().clear();
}
```

- Test your code using physical device or the Emulator. See sample GUI on onQ
    under Week 11 Assignment III folder.
- Packaging your app as .apk: Go to Build→Build apk. Find apk in the project
    directory, see example in Fig 2. Submit this apk file along with your project file.
    COPY THIS apk file to the home directory of your project folder (THANK YOU
    :))

**HOPE YOU ENJOY ANDROID PROGRAMMING :)**
