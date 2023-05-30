#include <iostream>
#include <string>
#include <algorithm>

using namespace std;
#include "fraction_16arp5.h"

int GCD(int a, int b) {
    if(b == 0) {
            return a;
        } else {
            return GCD(b, a % b);
    }
}

FractionException::FractionException(const string& message) : message(message) {}
string& FractionException::what() { return message; }

Fraction::Fraction() : num(0), den(1) { };
Fraction::Fraction(int n) : num(n), den(1) { };
Fraction::Fraction(int n, int d) : num(n/GCD(n,d)), den(d/GCD(n,d)) {
    if(den == 0)
        throw FractionException("Fraction with a zero value denominator");
    if(den < 0) {
        num *= -1;
        den *= -1;
    }
};

int Fraction::numerator() const {
	return num;
}

int Fraction::denominator() const {
	return den;
}

ostream& operator<<(ostream& out, const Fraction& value) {
   out << value.numerator() << "/" << value.denominator();
   return out;
}

//istream& operator>>(istream& in, Fraction& value) {
//    stringstream ss(in);
//    ss >> value.num >> "/" >> value.den;
//}

Fraction operator+(const Fraction& left, const Fraction& right) {
    int numerator = left.numerator()*right.denominator() + left.denominator()*right.numerator();
    int denominator = left.denominator() * right.denominator();
    return Fraction(numerator, denominator);
}

Fraction operator-(const Fraction& left, const Fraction& right) {
    int numerator = left.numerator()*right.denominator() - left.denominator()*right.numerator();
    int denominator = left.denominator() * right.denominator();
    return Fraction(numerator, denominator);
}

Fraction operator-(const Fraction& fraction) {
    return Fraction(fraction.numerator()*-1,fraction.denominator());
}

Fraction operator*(const Fraction& left, const Fraction& right) {
	return Fraction(left.numerator()*right.numerator(), left.denominator()*right.denominator());
}

Fraction operator/(const Fraction& left, const Fraction& right) {
	return Fraction(left.numerator()*right.denominator(), left.denominator()*right.numerator());
}

Fraction operator++(Fraction& value) {
    Fraction temp = value + Fraction(1);
    value.num = temp.num;
    value.den = temp.den;
    return value;
}

Fraction operator++(Fraction& value, int incr) {
    Fraction ret = value;
    Fraction temp = value + Fraction(1);
    value.num = temp.num;
    value.den = temp.den;
    return ret;
}

Fraction operator--(Fraction& value) {
    Fraction temp = value - Fraction(1);
    value.num = temp.num;
    value.den = temp.den;
    return value;
}

Fraction operator--(Fraction& value, int incr) {
    Fraction ret = value;
    Fraction temp = value - Fraction(1);
    value.num = temp.num;
    value.den = temp.den;
    return ret;
}


void operator+=(Fraction& left, const Fraction& right) {
    Fraction temp = left + right;
    left.num = temp.num;
    left.den = temp.den;
}

bool operator<(const Fraction& left, const Fraction& right) {
    return (left.numerator()/left.denominator() < right.numerator()/right.denominator() ? true : false);
}


bool operator<(const int& left, const Fraction& right) {
    return (left < (double)right.numerator()/right.denominator() ? true : false);
}

bool operator<=(const Fraction& left, const Fraction& right) {
    return ((left.numerator()/left.denominator() < right.numerator()/right.denominator()) || (left.numerator() == right.numerator() && left.denominator() == right.denominator()) ? true : false);
}

bool operator==(const Fraction& left, const Fraction& right){
    return (left.numerator() == right.numerator() && left.denominator() == right.denominator() ? true : false);
}

bool operator!=(const Fraction& left, const Fraction& right){
    return (left.numerator() == right.numerator() && left.denominator() == right.denominator() ? false : true);
}

bool operator>=(const Fraction& left, const Fraction& right){
    return ((left.numerator()/left.denominator() > right.numerator()/right.denominator()) || (left.numerator() == right.numerator() && left.denominator() == right.denominator()) ? true : false);
}

bool operator>(const Fraction& left, const Fraction& right) {
    return ((left.numerator()/left.denominator()) > right.numerator()/right.denominator() ? true : false);
}

bool operator>(const Fraction& left, const int& right) {
    return ((double)left.numerator()/left.denominator() > right ? true : false);
}