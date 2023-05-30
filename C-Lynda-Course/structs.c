#include <stdio.h>

struct vector { //declare struct type with 3 fields
        int x;
        int y;
        int z;
    };

void printStruct(struct vector a) {
    printf("x: %d\n",a.x);
    printf("y: %d\n",a.y);
    printf("z: %d\n",a.z);
}

int main() {
    struct vector velocity; //initilize variable of struct type
    velocity.x = 3;
    velocity.y = 2;
    velocity.z = -1;

    printStruct(velocity);
}

