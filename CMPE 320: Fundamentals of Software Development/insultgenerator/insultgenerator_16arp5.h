#pragma once

#include <iostream>
#include <fstream>
#include <string>
#include <vector>

using namespace std;

class FileException {
public:
        FileException(const string& message);
        string& what();
private:
        string message;
};


class NumInsultsOutOfBounds {
public:
        NumInsultsOutOfBounds(const string& message);
        string& what();
private:
        string message;
};

class InsultGenerator {
public:
        // initiliazes an InsultGenerator object, loads phrases from InsultsSource.txt into attributes, and throws an exception if file cannot be read
        // stores each word from each column in its own vector
        void initialize();

        // returns a single insult, generated at random.
        // concats a string using elements from each column attribute chosen at the index determined by the rand() function
        string talkToMe();

        //generates the requested number of unique insults, and throws an exception if numInsults is outside the specified range
        //creates a set of strings populated by iterating talkToMe() for the requested number. A set is used to sort elements alphabetically then converts to and outputs a vectors
        vector<string> generate(int numInsults); 

        // generates the requested number of unique insults and saves them to the filename, and throws an exception if file cannot be written
        // generates a vector using the generate() method and writes each element to the filename
        void generateAndSave(const string& filename, int numInsults); 
private:
        vector<string> column1;
        vector<string> column2;
        vector<string> column3;
};