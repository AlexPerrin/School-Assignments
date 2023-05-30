#include <iostream>
#include <fstream>
#include <sstream>
#include <string>
#include <vector>
#include <set>

#include "insultgenerator_16arp5.h"

using namespace std;

FileException::FileException(const string& message) : message(message) {}
string& FileException::what() { return message; }

NumInsultsOutOfBounds::NumInsultsOutOfBounds(const string& message) : message(message) {}
string& NumInsultsOutOfBounds::what() { return message; }

void InsultGenerator::initialize() {
    ifstream fileIn("InsultsSource.txt");
	vector<string> contents;
	string line;
	if (fileIn.fail()) {
	    cerr << "Unable to open file: InsultSource.txt" << endl;
	}
	while (getline(fileIn, line)){
        std::stringstream ss(line);
        while(getline(ss, line, '\t')) {
            contents.push_back(line);
        }
    }
    for(int i = 0; i < 50; i++) {
        column1.push_back(contents[i*3]);
        column2.push_back(contents[1+i*3]);
        column3.push_back(contents[2+i*3]);
    }
    fileIn.close();
}

string InsultGenerator::talkToMe() {
    return "Thou " + column1.at(rand()%50) + ' ' + column2.at(rand()%50) + ' ' + column3.at(rand()%50) + '!';
}

vector<string> InsultGenerator::generate(int numInsults) {
    if (numInsults < 1 || numInsults > 10000) {
        throw NumInsultsOutOfBounds("Outside  legal range for the number of insults to generate");
    }
    set<string> insultSet;
    while(insultSet.size() < numInsults) {
        insultSet.insert(talkToMe());
    }
    vector<string> output(insultSet.begin(), insultSet.end());
    return output;
}

void InsultGenerator::generateAndSave(const string& filename, int numInsults) {
    ofstream fileOut(filename);
    if (fileOut.fail()) {
		throw FileException("File cannot be written.");
	}
    vector<string> insults = generate(numInsults);
    for(string insult : insults) {
        fileOut << insult << endl;
    }
	fileOut.close();
}