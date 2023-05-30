#include <stdio.h>
#include <stdlib.h>
#include <string.h>

//explicilty define type struct person
struct person { 
    int age;
    char *name;
};

void printperson(struct person a) {
    printf("age %d\n",a.age);
    printf("age %s\n",a.name);
}

int main(void) {
    
    //explicilty define type struct person
    struct person alex;
    alex.age = 19;
    alex.name = "Alex";

    printperson(alex);
}