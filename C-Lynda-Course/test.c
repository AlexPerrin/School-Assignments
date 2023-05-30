#include <stdio.h>

void main() {
    int i;
    int array[5] = {11,22,33,44,55};
    int pArray = &array;

    printf("element \t address \t value \n");
    
    for(i=0;i<5;i++) {
        printf("array[%d] \t %p \t %d \n",i,&array[i],array[i]);
    }
}