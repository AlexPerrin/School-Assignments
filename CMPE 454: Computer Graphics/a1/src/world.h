// world.h


#ifndef WORLD_H
#define WORLD_H


#include "headers.h"
#include "landscape.h"
#include "lander.h"
#include "ll.h"


#define BOTTOM_SPACE 0.1f // amount of blank space below terrain (in viewing coordinates) 


class World {

    Landscape* landscape;
    Lander* lander;
    bool       zoomView; // show zoomed view when lander is close to landscape
    GLFWwindow* window;

    char* showMsg;
    int  score;
    GLuint arrowVAO1, arrowVAO2;
    int    numArrowVerts;

public:

    World(GLFWwindow* w) {
        landscape = new Landscape();
        lander = new Lander(maxX(), maxY()); // provide world size to help position lander
        zoomView = false;
        window = w;
        showMsg = NULL;
        score = 0;
        setupArrowVAO1();
        setupArrowVAO2();
    }

    void draw();
    void drawArrow1(mat4& worldToViewTransform);
    void drawArrow2(mat4& worldToViewTransform);

    void updateState(float elapsedTime);

    int Altitude();
    void resetLander();

    void setupArrowVAO1();
    void setupArrowVAO2();


    // World extremes (in world coordinates)

    float minX() { return 0; }
    float maxX() { return landscape->maxX(); }

    float minY() { return 0; }
    float maxY() { return (landscape->maxX() - landscape->minX()) / screenAspect * (2 - BOTTOM_SPACE) / 2; }
};


#endif
