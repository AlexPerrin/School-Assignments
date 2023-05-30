#include <stdio.h>

int main() {
    int intVar = 12;
    //integer atleast 16 bits, 2 bytes %d

    char charVar = 'a';
    //single character byte %c
    
    float floatVar = 3.14;
    //floating point atleast 32 bits, 4 bytes %f
    
    double doubleVar = 3.1415926535;
    //more percise floating point atleast 64 bits, 8 bytes %lf

    printf("Type\tValue\t\t Size (Bytes) \n");
    printf("int \t%d \t \t \t %lu \n",intVar,sizeof(intVar));
    printf("char \t%c \t \t \t %lu \n",charVar,sizeof(charVar));
    printf("float \t%f \t %lu \n",floatVar,sizeof(floatVar));
    printf("double \t%lf \t %lu \n",doubleVar,sizeof(doubleVar));
}