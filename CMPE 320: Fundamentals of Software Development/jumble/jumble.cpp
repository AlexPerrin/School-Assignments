#include <stdio.h>
#include <stdlib.h>
#include <iostream>
#include <string>
#include <bits/stdc++.h>

#include "jumble.h"

using namespace std;

BadJumbleException::BadJumbleException(const string& m) : message(m) {}
string& BadJumbleException::what() { return message; }

JumblePuzzle::JumblePuzzle(const string& toHide, const string& difficulty) {
    
    if (difficulty == "easy") {
        //cout << "easy selected" << endl;
        size = toHide.size() * 2;
    }
    else if (difficulty == "medium") {
        //cout << "medium selected" << endl;    
        size = toHide.size() * 3;
    }
    else if (difficulty == "hard") {
        //cout << "hard selected" << endl;    
        size = toHide.size() * 4;
    }  
    else
        throw BadJumbleException("Invalid difficulty");   

    jumble = new charArrayPtr[size]; //Heap
    for(int i = 0; i < size; i++)
        jumble[i] = new char;

    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++)
            jumble[i][j] = 'a' + rand()%26;
    }

    rowPos = rand() % size;
    colPos = rand() % size;
    string nesw = "nesw";
    int seed = rand() % 4;
    direction = nesw[seed];
   
   /*
    cout << "size: " << size << endl;
    cout << "rowPos: " << rowPos << endl;
    cout << "colPos: " << colPos << endl;
    cout << "seed: " << seed << endl;
    cout << "direction: " << direction << endl;
    */

    int i = 0;
    int j = 0;
    int k = 0;

    //for (k = 0; k < toHide.size(); k++) {
    //    jumble[0][k] = toHide[k];
    //}
    
    
    for (k = 0; k < toHide.size(); k++) {
        if (rowPos + i < 0 || rowPos + i >= size || colPos + j < 0 || colPos + j >= size) {
            //cout << "out of bounds retrying" << endl;
            i = 0;
            j = 0;
            k = -1;
            rowPos = rand() % size;
            colPos = rand() % size;
            seed = rand() % 4;
            direction = nesw[seed];
            //cout << "rowPos: " << rowPos << endl;
            //cout << "colPos: " << colPos << endl;
            //cout << "seed: " << seed << endl;
            //cout << "direction: " << direction << endl;
            continue;
        }
        //printf("placing letter %c at [%d][%d]\n",toHide[k],rowPos + i,colPos + j);
        jumble[rowPos + i][colPos + j] = toHide[k];
        switch (seed) {
            case 0:
                i--;
                break;
            case 1:
                j++;
                break;
            case 2:
                i++;
                break;
            case 3:
                j--;
                break;
        }
    }

    /*
    cout << "printing jumble" << endl;
    for (int i = 0; i < size; i++) {
        for (int j = 0; j < size; j++) {
            printf("%c ", jumble[i][j]); 
        }
        printf("\n");
    }
    */
}

JumblePuzzle::~JumblePuzzle() {
    for(int i = 0; i < size; i++)
        delete[] jumble[i];
    delete[] jumble;
	jumble = nullptr;
}

JumblePuzzle::JumblePuzzle(const JumblePuzzle& copy) {
    size = copy.getSize();
	rowPos = copy.getRowPos();	
	colPos = copy.getColPos();
	direction = copy.getDirection();
    jumble = copy.getJumble();
}

JumblePuzzle& JumblePuzzle::operator=(const JumblePuzzle& copy) {
    size = copy.getSize();
	rowPos = copy.getRowPos();	
	colPos = copy.getColPos();
	direction = copy.getDirection();
    jumble = copy.getJumble();
}

charArrayPtr* JumblePuzzle::getJumble() const {
    charArrayPtr* temp = new charArrayPtr[size]; //Heap
    for(int i = 0; i < size; i++)
        temp[i] = new char;

    for (int i = 0; i < size; i++)
        for (int j = 0; j < size; j++)
            temp[i][j] = jumble[i][j];
    
    return temp;
}

int JumblePuzzle::getSize() const {
    return size;
}

int JumblePuzzle::getRowPos() const {
    return rowPos;
}

int JumblePuzzle::getColPos() const {
    return colPos;
}

char JumblePuzzle::getDirection() const {
    return direction;
}