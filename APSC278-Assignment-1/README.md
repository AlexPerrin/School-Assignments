# Project 1

The goal of this project is to extend your knowledge of basic data structures. In particular, you
will be given a list of functions for implementing a polynomial. You are responsible to provide
implementation for the functions and the main() program to test your implementation. You are
requested to use linked list basic data structure.

**1 Objectives**
The objective of this project is to implement a data type that represents polynomials in _x_. i.e., the
set of values we wish to represent are _polynomials_ of the form where _n_ ≥ _0_ is the _degree_ of the
polynomial. Many functions on polynomials are possible. In this project, you will implement
_additio_ n, _multiplicatio_ n, _differentiation_ and _evaluatio_ n:

**2 Requirements**
You are required to define the structure of Polynomial and implement the functions given below:

typedef struct node
{
double coef;
int exp;
node* next;
} Node;

int getDegree ();
double getCoefficient (int i);
void setCoefficient (int i, double c);
void assign (Node *p);
Node *plus (Node *p);
Node *minus (Node *p);
Node *times (Node *p);
double eval (double x);
Node *getDerivative ();
void display (Node *node);

**2.1 Method Description**

**setCoefficient** This function is used to set the coefficients of a polynomial. It takes two
arguments, _i_ and _c_ , and sets the value of _ai_ , the i _th_ coefficient, to _c_.

**getCoefficient** This function takes a single int argument, _i_ , and returns the value of coefficient _ai_.
Note, if _i_ is greater than the degree of the polynomial, this method simply returns zero.

**getDegree** This function returns the degree of the polynomial.


**assign** This function assigns the given polynomial, _p_ , to this polynomial.

**getDerivative** This function returns a _new_ polynomial that is the derivative of this polynomial.

**display** This function displays a polynomial that contains a textual representation of this
polynomial.

The display must start with the highest degree term ( _anxn_ ) and contain nonzero terms only. An
exception to the latter is the case when _polynomial is equal to zero_

**plus** This function returns a _new_ polynomial that represents the sum of this polynomial and the
given polynomial.

**minus** This function returns a _new_ polynomial that represents the difference between this
polynomial and the given polynomial.

**times** This function returns a _new_ polynomial that represents the product of this polynomial and
the given polynomial.

**eval** This function takes a single double argument, _x_ , and computes the value.

**2.2 Implementation**
Your implementation must use a linked list of doubles to record the coefficients of the
polynomial.

**2.3 Main Program**
You should thoroughly test and debug your implementation.
