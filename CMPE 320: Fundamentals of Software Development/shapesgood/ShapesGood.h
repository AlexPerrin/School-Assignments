#pragma once

#include <string>
using namespace std;

class Shape {
public:
	Shape(int, int, const string&);
	virtual ~Shape();
	int getLength();
	int getWidth();
    string getColour();
    virtual void draw() = 0;
private:
	int length, width;
    string* colour;
};

class Fillable : virtual public Shape {
public:
    Fillable(int, int, const string&, const string&);
    virtual ~Fillable();
    string getFillColour();
private:
    string* fillColour;
};

class Text : virtual public Fillable {
public:
    Text(int, int, const string&, const string&, const string&);
    virtual ~Text();
    string getLabel();
private:
    string* label;
};

class Square : virtual public Shape {
public:
    Square(int, int, const string&);
    virtual ~Square();
    virtual void draw();
    string drawOutside();
};

class FilledSquare : virtual public Fillable, virtual public Square {
public:
    FilledSquare(int, int, const string&, const string&);
    virtual ~FilledSquare();
    virtual void draw();
    string fill();
};

class FilledTextSquare : virtual public Text, virtual public FilledSquare {
public:
    FilledTextSquare(int, int, const string&, const string&, const string&);
    virtual ~FilledTextSquare();
    virtual void draw();
    string drawText();
};

class Circle : virtual public Shape {
public:
    Circle(int, int, const string&);
    virtual ~Circle();
    virtual void draw();
    string drawOutside();
};

class FilledCircle : virtual public Fillable, virtual public Circle {
public:
    FilledCircle(int, int, const string&, const string&);
    virtual ~FilledCircle();
    virtual void draw();
    string fill();
};

class Arc : virtual public Shape {
public:
    Arc(int, int, const string&);
    virtual ~Arc();
    virtual void draw();
    string drawOutside();
};