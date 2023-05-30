package com.example.alexperrin.guessmaster;

import java.util.Random;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

public class GuessMaster extends AppCompatActivity {
    //Instance Variables
    private TextView entityName;
    private TextView ticketsum;
    private Button guessButton;
    private EditText userIn;
    private Button btnclearContent;
    private ImageView entityImage;
    String answer;

    private int numOfEntities;
    private Entity[] entities;
    private int numOfTickets;
    //Stores Entity Name
    String entName;
    int entityid = 0;
    int currentTicketWon = 0;

    public GuessMaster() {
        numOfEntities = 0;
        entities = new Entity[10];
    }

    public void addEntity(Entity entity) {
        entities[numOfEntities++] = entity.clone();
    }

    public void playGame(int entityId) {
        Entity entity = entities[entityId];
        playGame(entity);
    }

    public void playGame(Entity entity) {
        entityName.setText(entity.getName());
        //Get User Input
        answer = userIn.getText().toString();
        //Format the User input
        answer = answer.replace("\n", "").replace("\r", "");
        answer = answer.replace("\n", "").replace("\r", "");
        //If the answer is not empty excecute play game.
        if ((!answer.equals(""))) {
            Date date = new Date(answer);
            if (date.precedes(entity.getBorn())) {
                AlertDialog.Builder precedes = new AlertDialog.Builder(GuessMaster.this);
                precedes.setTitle("Incorrect");
                precedes.setMessage("Try a later date.");
                precedes.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog dialog = precedes.create();
                dialog.show();
            } else if (entity.getBorn().precedes(date)) {
                AlertDialog.Builder after = new AlertDialog.Builder(GuessMaster.this);
                after.setTitle("Incorrect");
                after.setMessage("Try an earlier date.");
                after.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { dialog.cancel();
                    }
                });
                AlertDialog dialog = after.create();
                dialog.show();
            } else {
                //When the player guesses correctly, display the tickets and the you won screen with all the information
                currentTicketWon = entity.getAwardedTicketNumber();
                numOfTickets += entity.getAwardedTicketNumber();
                AlertDialog.Builder youwin = new AlertDialog.Builder(GuessMaster.this);
                youwin.setTitle("You Won");
                youwin.setMessage("BINGO! " + entity.closingMessage());
                youwin.setCancelable(false);

                youwin.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { Toast.makeText(getBaseContext(), "You won " + currentTicketWon + " tickets!", Toast.LENGTH_SHORT).show();
                    //Set the tickets won in the textview
                        ticketsum.setText("Total Tickets: " + Integer.toString(numOfTickets));
                        //Continue game after the player has one
                        ContinueGame();
                    }
                });
                AlertDialog dialog = youwin.create();
                dialog.show();
            }
        }
    }

    //Generate a random integer
    public int genRandomEntityId() {
        Random randomNumber = new Random();
        return randomNumber.nextInt(numOfEntities);
    }

    public void playGame() {
        int entityId = genRandomEntityId();
        playGame(entityId);
    }

    public void changeEntity() {
        userIn.getText().clear();
        entityid = genRandomEntityId();
        Entity entity = entities[entityid];
        entName = entity.getName();
        ImageSetter();
        playGame(entity);

    }

    public void ImageSetter() {
        if (entName.equals("Justin Trudeau")) {
            entityImage.setImageResource(R.drawable.justint);
        } else if (entName.equals("Celine Dion")) {
            entityImage.setImageResource(R.drawable.celidion);
        } else if (entName.equals("United States")) {
            entityImage.setImageResource(R.drawable.usaflag);
        } else {
            entityImage.setImageResource(R.drawable.alex);
        }
    }

    //Continue Game Method - This method gets another Entity randomly
    //Updates the Image view and the Entity Name Field
    public void ContinueGame() {
        entityid = genRandomEntityId();
        Entity entity = entities[entityid];

        entName = entity.getName();

        //Call the ImageSetter method
        ImageSetter();

        //Print the name of the entity to be guessed
        //in the entityName textview
        entityName.setText(entName);
        userIn.getText().clear();
    }

    public void newInstance() {
        entityid = genRandomEntityId();
        Entity entity = entities[entityid];
        entName = entity.getName();
        ImageSetter();
        welcomeToGame(entity);
        playGame(entity);
    }

    public void welcomeToGame(Entity entity) {
        //Welcome Alert
        AlertDialog.Builder welcomealert = new AlertDialog.Builder(GuessMaster.this);
        welcomealert.setTitle("GuessMaster Game v3");
        welcomealert.setMessage(entity.welcomeMessage());
        welcomealert.setCancelable(false); //no Cancel Button
        welcomealert.setNegativeButton("Start Game", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getBaseContext(), "Game is starting... Enjoy", Toast.LENGTH_SHORT).show();
            }
        });
        //Show Dialog
        AlertDialog dialog = welcomealert.create();
        dialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guess_activity);

        ticketsum = findViewById(R.id.ticket);
        entityImage = findViewById(R.id.entityImage);
        entityName = findViewById(R.id.entityName);
        userIn = findViewById(R.id.guessinput);
        guessButton = findViewById(R.id.btnGuess);
        btnclearContent = findViewById(R.id.btnClear);

        Politician jTrudeau = new Politician("Justin Trudeau", new Date("December", 25, 1971), "Male", "Liberal", 0.25);////
        Singer cDion = new Singer("Celine Dion", new Date("March", 30, 1961), "Female", "La voix du bon Dieu", new Date("November", 6, 1981), 0.5);////
        Person myCreator = new Person("Alex Perrin", new Date("May", 20, 1999), "Male", 1);////
        Country usa = new Country("United States", new Date("July", 4, 1776), "Washinton D.C.", 0.1);////
        addEntity(jTrudeau);
        addEntity(cDion);
        addEntity(myCreator);
        addEntity(usa);
        playGame();

        btnclearContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeEntity();
            }
        });

        guessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playing game
                Entity entity = entities[entityid];
                playGame(entity);
            }
        });
        newInstance();
    }
}
