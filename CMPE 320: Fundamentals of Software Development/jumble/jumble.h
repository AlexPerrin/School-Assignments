#pragma once

#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <bits/stdc++.h>
using namespace std;

typedef char* charArrayPtr;

class BadJumbleException {
public:
	BadJumbleException(const string& message);
	string& what();
private:
	string message;
};

class JumblePuzzle {
public:
	JumblePuzzle(const string&, const string&);	// Constructor
	~JumblePuzzle();	// Deconstructor
	JumblePuzzle(const JumblePuzzle&);	// Copy constructor
	JumblePuzzle& operator=(const JumblePuzzle&);
	char* operator[](int) const;
	charArrayPtr* getJumble() const;
	int getSize() const;
	int getRowPos() const;
	int getColPos() const;
	char getDirection() const;
private:
	charArrayPtr* jumble;
	int size;
	int rowPos;	
	int colPos;
	char direction;
};
