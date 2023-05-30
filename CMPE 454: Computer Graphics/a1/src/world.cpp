// world.cpp


#include "world.h"
#include "lander.h"
#include "ll.h"
#include "gpuProgram.h"
#include "strokefont.h"

#include <sstream>
#include <iomanip>
#include <time.h>

//const float textAspect = 0.7;	// text width-to-height ratio (you can use this for more realistic text on the screen)

#define ARROW_LENGTH 10 

time_t TimeCodeStarted = time(0);
time_t pausedTime = 0;
float pausedSeconds = 0;  // stores the time at which the game is paused
float dist = 0.0;
float elapsedSeconds = 0;  // used for in-game timer
int fail = -1;


void World::updateState(float elapsedTime)

{
    // See if any keys are pressed for thrust

    if (glfwGetKey(window, GLFW_KEY_RIGHT) == GLFW_PRESS) // right arrow
        lander->rotateCW(elapsedTime);

    if (glfwGetKey(window, GLFW_KEY_LEFT) == GLFW_PRESS) // left arrow
        lander->rotateCCW(elapsedTime);

    if (glfwGetKey(window, GLFW_KEY_DOWN) == GLFW_PRESS) // down arrow
        lander->addThrust(elapsedTime);

    // Update the position and velocity

    lander->updatePose(elapsedTime);


    // See if the lander has touched the terrain

    vec3 closestTerrainPoint = landscape->findClosestPoint(lander->centrePosition());
    float closestDistance = (closestTerrainPoint - lander->centrePosition()).length();


    // Find if the view should be zoomed

    zoomView = (closestDistance < ZOOM_RADIUS);

    // YOUR CODE HERE

    // Check for landing or collision and let the user know
    //
    // Conditions for a successful landing:
    // (1) vertical velocity < 1 m/s
    // (2) horizontal velocity < 0.5 m/s
    // (3) lander's rotational orientation must be < 5 degrees off of being perfectly upright on the landed-upon surface
    //
    // Score is out of a possible 1000 points, scaled linearly according to the above criteria + fuel remaining

    if (world->Altitude() < 1)
    {
        endGame = true;

        if (((abs(lander->velocity.x) < 0.50) && (abs(lander->velocity.y) < 1.0)))
        {
            fail = 0;
            // the scores for the three criteria are scaled to sum to a fraction
            float hSpeedScore = ((0.5 - lander->velocity.x) / 0.5) / 3;
            float vSpeedScore = ((1.0 - lander->velocity.y) / 1.0) / 3;
            float fuelScore = (lander->fuel / 5000) / 3;
            score = 1000 * (hSpeedScore + vSpeedScore + fuelScore);
        }
        else
        {
            fail = 1;
        }
    }
}

int World::Altitude() //return altidue based on lander height from the ground currently. 
{
    float landerX = lander->centrePosition().x;
    float landerY = lander->centrePosition().y;
    vec4 Ground = landscape->GroundUnder(lander->centrePosition());
    float StartX = Ground.x;
    float StartY = Ground.y;
    float EndX = Ground.z;
    float EndY = Ground.w;
    float segrat = (landerX - StartX) / (EndX - StartX);
    float segYpoint = ((EndY - StartY) * segrat) + StartY;

    // calculate height, also subtracting (height of lander)/2 to account for calculations utilizing center of lander
    float path = (landerY - lander->height * 0.5 ) - segYpoint;
    return path;
}

void World::resetLander() {
  lander->reset();
  endGame = false;
  TimeCodeStarted = time(0);
  score = 0;
  lander->fuel = 5000;
}


// Create an arrow VAO
//
// This needs 'numArrowVerts' and 'arrowVAO' defined in world.h

void World::setupArrowVAO1()
{
    // NOTE - this is for the vertical arrow - we could not figure out a way
    // draw two arrows using a single VAO setup and draw function

    // Create an arrow from line segments

    GLfloat arrowVerts[] = {
      0,    1,    0,    -1,
      0,    1,    0.5, 0.25,
      0,    1,   -0.5, 0.25,
      0.5, 0.25, -0.5, 0.25
    };

    numArrowVerts = sizeof(arrowVerts) / sizeof(GLfloat);

    // Find the bounding box of the arrow

    vec3 min = vec3(arrowVerts[0], arrowVerts[1], 0);
    vec3 max = vec3(arrowVerts[0], arrowVerts[1], 0);

    for (int i = 0; i < numArrowVerts; i += 2) {
        vec3 v(arrowVerts[i], arrowVerts[i + 1], 0);
        if (v.x < min.x) min.x = v.x;
        if (v.x > max.x) max.x = v.x;
        if (v.y < min.y) min.y = v.y;
        if (v.y > max.y) max.y = v.y;
    }

    // Translate the arrow so that its lower-left corner is at ____
    // and its length is ARROW_LENGTH and y increases upward.
    // 
    // Note that y increases downward in the model, so the y axis must
    // be inverted.

    float s = ARROW_LENGTH / (max.x - min.x);
    
    mat4 modelToWorldTransform = scale(s, -s, 1) * translate(66, -75.5, 0) * rotate(3.14159, vec3(0, 0, 1));

    // Rewrite the model vertices so that they are in the world
    // coordinate system.

    for (int i = 0; i < numArrowVerts; i += 2) {
        vec4 newV = modelToWorldTransform * vec4(arrowVerts[i], arrowVerts[i + 1], 0, 1);
        arrowVerts[i] = newV.x / newV.w;
        arrowVerts[i + 1] = newV.y / newV.w;
    }

    // ---- Create a VAO for this object ----

    glGenVertexArrays(1, &arrowVAO1);
    glBindVertexArray(arrowVAO1);

    // Store the vertices

    GLuint VBO;

    glGenBuffers(1, &VBO);
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, numArrowVerts * sizeof(GLfloat), arrowVerts, GL_STATIC_DRAW);

    // define the position attribute

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 0, 0);

    // Done

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
}

void World::setupArrowVAO2()
{
    // NOTE - this is for the horizontal arrow - we could not figure out a way
    // draw two arrows using a single VAO setup and draw function

    // create an arrow from line segments

    GLfloat arrowVerts[] = {
      0,    1,    0,    -1,
      0,    1,    0.5, 0.25,
      0,    1,   -0.5, 0.25,
      0.5, 0.25, -0.5, 0.25
    };

    numArrowVerts = sizeof(arrowVerts) / sizeof(GLfloat);

    // Find the bounding box of the arrow

    vec3 min = vec3(arrowVerts[0], arrowVerts[1], 0);
    vec3 max = vec3(arrowVerts[0], arrowVerts[1], 0);

    for (int i = 0; i < numArrowVerts; i += 2) {
        vec3 v(arrowVerts[i], arrowVerts[i + 1], 0);
        if (v.x < min.x) min.x = v.x;
        if (v.x > max.x) max.x = v.x;
        if (v.y < min.y) min.y = v.y;
        if (v.y > max.y) max.y = v.y;
    }

    // Translate the arrow so that its lower-left corner is at ____
    // and its length is ARROW_LENGTH and y increases upward.
    // 
    // Note that y increases downward in the model, so the y axis must
    // be inverted.

    float s = ARROW_LENGTH / (max.x - min.x);

    mat4 modelToWorldTransform = scale(s, -s, 1) * translate(66, -78, 0) * rotate(4.71239, vec3(0, 0, 1));

    // Rewrite the model vertices so that they are in the world
    // coordinate system.

    for (int i = 0; i < numArrowVerts; i += 2) {
        vec4 newV = modelToWorldTransform * vec4(arrowVerts[i], arrowVerts[i + 1], 0, 1);
        arrowVerts[i] = newV.x / newV.w;
        arrowVerts[i + 1] = newV.y / newV.w;
    }

    // ---- Create a VAO for this object ----

    glGenVertexArrays(1, &arrowVAO2);
    glBindVertexArray(arrowVAO2);

    // Store the vertices

    GLuint VBO;

    glGenBuffers(1, &VBO);
    glBindBuffer(GL_ARRAY_BUFFER, VBO);
    glBufferData(GL_ARRAY_BUFFER, numArrowVerts * sizeof(GLfloat), arrowVerts, GL_STATIC_DRAW);

    // define the position attribute

    glEnableVertexAttribArray(0);
    glVertexAttribPointer(0, 2, GL_FLOAT, GL_FALSE, 0, 0);

    // Done

    glBindBuffer(GL_ARRAY_BUFFER, 0);
    glBindVertexArray(0);
}

void World::drawArrow1(mat4& worldToViewTransform)
{
    glBindVertexArray(arrowVAO1);

    glUniformMatrix4fv(glGetUniformLocation(myGPUProgram->id(), "MVP"), 1, GL_TRUE, &worldToViewTransform[0][0]);
    glLineWidth(2.0);
    glDrawArrays(GL_LINES, 0, numArrowVerts);

    glBindVertexArray(0);
}

void World::drawArrow2(mat4& worldToViewTransform)
{
    glBindVertexArray(arrowVAO2);

    glUniformMatrix4fv(glGetUniformLocation(myGPUProgram->id(), "MVP"), 1, GL_TRUE, &worldToViewTransform[0][0]);
    glLineWidth(2.0);
    glDrawArrays(GL_LINES, 0, numArrowVerts);

    glBindVertexArray(0);
}

void World::draw()
{
    mat4 worldToViewTransform;
    
    if (!zoomView) {

        // Find the world-to-view transform that transforms the world
        // to the [-1,1]x[-1,1] viewing coordinate system, with the
        // left edge of the landscape at the left edge of the screen, and
        // the bottom of the landscape BOTTOM_SPACE above the bottom edge
        // of the screen (BOTTOM_SPACE is in viewing coordinates).

        float s = 2.0 / (landscape->maxX() - landscape->minX());

        //cout << "NOT ZOOMED" <<"\n";

        worldToViewTransform
            = translate(-1, -1 + BOTTOM_SPACE, 0)
            * scale(s, s, 1)
            * translate(-landscape->minX(), -landscape->minY(), 0);
    }
    else {

        // Find the world-to-view transform that is centred on the lander
        // and is 2*ZOOM_RADIUS wide (in world coordinates).
        // YOUR CODE HERE

        float s = 0.5 * ZOOM_RADIUS / (landscape->maxX() - landscape->minX());  // TODO find correct coefficient of ZOOM_RADIUS

        worldToViewTransform
            = scale(s, s, 1)
            * translate(-lander->centrePosition()[0], -lander->centrePosition()[1], 0); 
    }   

    // Draw the landscape and lander, passing in the worldToViewTransform
    // so that they can append their own transforms before passing the
    // complete transform to the vertex shader.

    landscape->draw(worldToViewTransform);
    lander->draw(worldToViewTransform);
    world->drawArrow1(worldToViewTransform);
    world->drawArrow2(worldToViewTransform);

    // Draw the heads-up display (i.e. all text).
    
    stringstream title;
    title.setf(ios::fixed, ios::floatfield);
    title << "LUNAR LANDER ";
    drawStrokeString("LUNAR LANDER", -0.25, 0.85, 0.06, glGetUniformLocation(myGPUProgram->id(), "MVP"));

    // YOUR CODE HERE (modify the above code, too)

    // score
    stringstream ss;
    ss.setf(ios::fixed, ios::floatfield);
    ss.precision(1);
    ss << "SCORE:" << world->score;
    drawStrokeString(ss.str(), -0.95, 0.70, 0.04, glGetUniformLocation(myGPUProgram->id(), "MVP"));

    // Calculate the amount of seconds since the program started, handling game pauses and game over

    time_t currentTime = time(0);

    if (endGame) {
      // do nothing, no more updating time
    }    
    else if (!pauseGame && !pausedFlag) {
      elapsedSeconds = difftime(currentTime, TimeCodeStarted) - pausedSeconds;
    }
    else if (pauseGame && !pausedFlag) {
      pausedFlag = true;
      pausedTime = time(0);
    }
    else if (!pauseGame && pausedFlag) { 
      pausedFlag = false;
      pausedSeconds += difftime(currentTime, pausedTime);
      elapsedSeconds = difftime(currentTime, TimeCodeStarted) - pausedSeconds;
    }


    //time
    stringstream T;
    T.setf(ios::fixed, ios::floatfield);
    T.precision(0);
    T << "TIME:" << elapsedSeconds;
    drawStrokeString(T.str(), -0.95, 0.65, 0.04, glGetUniformLocation(myGPUProgram->id(), "MVP"));

    //fuel
    stringstream F;
    F.setf(ios::fixed, ios::floatfield);
    F.precision(0);
    F << "FUEL:" << lander->fuel;
    drawStrokeString(F.str(), -0.95, 0.60, 0.04, glGetUniformLocation(myGPUProgram->id(), "MVP"));

    // V speed
    stringstream Vs;
    Vs.setf(ios::fixed, ios::floatfield);
    Vs.precision(0);
    Vs << "VERTICAL SPEED:" << abs(lander->velocity.y);
    drawStrokeString(Vs.str(), 0.35, 0.60, 0.04, glGetUniformLocation(myGPUProgram->id(), "MVP"));

    //H speed
    stringstream Hs;
    Hs.setf(ios::fixed, ios::floatfield);
    Hs.precision(0);
    Hs << "HORIZONTAL SPEED:" << abs(lander->velocity.x);
    drawStrokeString(Hs.str(), 0.35, 0.65, 0.04, glGetUniformLocation(myGPUProgram->id(), "MVP"));

    // Altitude
    stringstream AL;
    AL.setf(ios::fixed, ios::floatfield);
    AL.precision(0);
    AL << "ALTITUDE:" << world->Altitude();
    drawStrokeString(AL.str(), 0.35, 0.70, 0.04, glGetUniformLocation(myGPUProgram->id(), "MVP"));

    // game over text
    if (endGame && fail == 1) {
      stringstream gameOver;
      gameOver.setf(ios::fixed, ios::floatfield);
      gameOver << "Game Over";
      drawStrokeString(gameOver.str(), -0.55, 0.2, 0.2, glGetUniformLocation(myGPUProgram->id(), "MVP"));

      stringstream fail;
      fail.setf(ios::fixed, ios::floatfield);
      fail << "Crash Landing!";
      drawStrokeString(fail.str(), -0.45, -0.1, 0.1, glGetUniformLocation(myGPUProgram->id(), "MVP"));

      stringstream finalScore;
      finalScore.setf(ios::fixed, ios::floatfield);
      finalScore << "Final Score:" << world->score;
      drawStrokeString(finalScore.str(), -0.4, -0.3, 0.1, glGetUniformLocation(myGPUProgram->id(), "MVP"));
    }
    else if (endGame) {
      stringstream gameOver;
      gameOver.setf(ios::fixed, ios::floatfield);
      gameOver << "Game Over";
      drawStrokeString(gameOver.str(), -0.55, 0.2, 0.12, glGetUniformLocation(myGPUProgram->id(), "MVP"));

      stringstream success;
      success.setf(ios::fixed, ios::floatfield);
      success << "Good Landing!";
      drawStrokeString(success.str(), -0.45, -0.1, 0.1, glGetUniformLocation(myGPUProgram->id(), "MVP"));

      stringstream finalScore;
      finalScore.setf(ios::fixed, ios::floatfield);
      finalScore << "Final Score:" << world->score;
      drawStrokeString(finalScore.str(), -0.5, -0.3, 0.1, glGetUniformLocation(myGPUProgram->id(), "MVP"));
    }

}



