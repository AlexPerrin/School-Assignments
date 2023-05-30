#include <stdio.h>
#include <string.h>

struct vector {
    int x;
    int y;
    int z;
};

void addvector(struct vector a, struct vector b, struct vector out) {
    out.y = a.y + b.y;
    out.z = a.z + b.z;
    out.x = a.x + b.x;
}

void printvector(struct vector a) {
    printf("x: %d\n",a.x);
    printf("y: %d\n",a.y);
    printf("z: %d\n",a.z);
}

int main() {
    struct vector vel1, vel2, out;

    vel1.x = 1;
    vel1.y = 3;
    vel1.z = -2;

    vel1.x = 4;
    vel1.y = -1;
    vel1.z = 7;

    addvector(vel1,vel2,out);
    printvector(out);
}