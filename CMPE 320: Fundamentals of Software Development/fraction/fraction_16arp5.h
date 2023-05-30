#pragma once

#include <iostream>
#include <string>
#include <algorithm>
using namespace std;

int GCD(int a, int b);

class FractionException {
public:
        FractionException(const string& message);
        string& what();
private:
        string message;
};

class Fraction {
public:
	Fraction();
    Fraction(int);
    Fraction(int, int);
    int numerator() const;
    int denominator() const;
    void print();

private:
    int num;
    int den;

friend ostream& operator<<(ostream& out, const Fraction& value);
//friend istream& operator>>(istream& in, Fraction& value);
friend Fraction operator+(const Fraction& left, const Fraction& right);
friend Fraction operator-(const Fraction& left, const Fraction& right);
friend Fraction operator-(const Fraction& fraction);
friend Fraction operator*(const Fraction& left, const Fraction& right);
friend Fraction operator/(const Fraction& left, const Fraction& right);
friend Fraction operator++(Fraction& value);
friend Fraction operator++(Fraction& value, int incr);
friend Fraction operator--(Fraction& value);
friend Fraction operator--(Fraction& value, int incr);
friend void operator+=(Fraction& left, const Fraction& right);
friend bool operator<(const Fraction& left, const Fraction& right);
friend bool operator<(const int& left, const Fraction& right);
friend bool operator<=(const Fraction& left, const Fraction& right);
friend bool operator==(const Fraction& left, const Fraction& right);
friend bool operator!=(const Fraction& left, const Fraction& right);
friend bool operator>=(const Fraction& left, const Fraction& right);
friend bool operator>(const Fraction& left, const Fraction& right);
friend bool operator>(const Fraction& left, const int& right);
};