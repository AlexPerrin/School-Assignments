#include <iostream>
#include <string>
#include "ShapesGood.h"

using namespace std;

//~~~~~~~~Shape~~~~~~~~
Shape::Shape(int len, int wid, const string& col) :
    length(len), width(wid) {
    colour = new string(col);
}

Shape::~Shape() {
    delete colour;
    colour = nullptr;
}

int Shape::getLength() {
    return length;
}

int Shape::getWidth() {
    return width;
}

string Shape::getColour() {
    return *colour;
}

//~~~~~~~~Fillable~~~~~~~~
Fillable::Fillable(int len, int wid, const string& col, const string& fill) :
    Shape(len, wid, col) {
    fillColour = new string(fill);
}

Fillable::~Fillable() {
    delete fillColour;
    fillColour = nullptr;
}

string Fillable::getFillColour() {
    return *fillColour;
}

//~~~~~~~~Text~~~~~~~~
Text::Text(int len, int wid, const string& col, const string& fill, const string& lab) :
    Fillable(len, wid, col, fill) {
    label = new string(lab);
}

Text::~Text() {
    delete label;
    label = nullptr;
}

string Text::getLabel() {
    return *label;
}

//~~~~~~~~Square~~~~~~~~
Square::Square(int len, int wid, const string& col) :
    Shape(len, wid, col) {}

Square::~Square() {}

string Square::drawOutside() {
    return  "Drawing a " + getColour() + " square.";
}

void Square::draw() {
    cout << endl << drawOutside();
}

//~~~~~~~~FilledSquare~~~~~~~~
FilledSquare::FilledSquare(int len, int wid, const string& col, const string& fill) :
    Fillable(len, wid, col, fill), Square(len, wid, col), Shape(len, wid, col) {}

FilledSquare::~FilledSquare() {}

string FilledSquare::fill() {
    return " With " + getFillColour() + " fill.";
}

void FilledSquare::draw() {
    cout << endl << drawOutside() << fill();
}

//~~~~~~~~FilledTextSquare~~~~~~~~
FilledTextSquare::FilledTextSquare(int len, int wid, const string& col, const string& fill, const string& lab) :
    Text(len, wid, col, fill, lab), Fillable(len, wid, col, fill), FilledSquare(len, wid, col, fill), Square(len, wid, col), Shape(len, wid, col) {}

FilledTextSquare::~FilledTextSquare() {}

string FilledTextSquare::drawText() {
    return " Containing the text: \"" + getLabel() + "\".";
}

void FilledTextSquare::draw() {
    cout << endl << drawOutside() << fill() << drawText();
}

//~~~~~~~~Circle~~~~~~~~
Circle::Circle(int len, int wid, const string& col) :
    Shape(len, wid, col) {}

Circle::~Circle() {}

string Circle::drawOutside() {
    return  "Drawing a " + getColour() + " circle.";
}

void Circle::draw() {
    cout << endl << drawOutside();
}

//~~~~~~~~FilledCircle~~~~~~~~
FilledCircle::FilledCircle(int len, int wid, const string& col, const string& fill) :
    Fillable(len, wid, col, fill), Circle(len, wid, col), Shape(len, wid, col) {}

FilledCircle::~FilledCircle() {}

string FilledCircle::fill() {
    return " With " + getFillColour() + " fill.";
}

void FilledCircle::draw() {
    cout << endl << drawOutside() << fill();
}

//~~~~~~~~Arc~~~~~~~~
Arc::Arc(int len, int wid, const string& col) :
    Shape(len, wid, col) {}

Arc::~Arc() {}

string Arc::drawOutside() {
    return  "Drawing a " + getColour() + " arc.";
}

void Arc::draw() {
    cout << endl << drawOutside();
}