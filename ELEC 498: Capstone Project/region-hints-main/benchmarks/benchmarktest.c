#include "benchmarktest.h"

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <time.h>
#include <omp.h>
#include <curl/curl.h>

typedef short int16_t;

#define ARRAY_CREATE(array, init_capacity, init_size) {\
    array = malloc(sizeof(*array)); \
    array->data = malloc((init_capacity) * sizeof(*array->data)); \
    assert(array->data != NULL); \
    array->capacity = init_capacity; \
    array->size = init_size; \
}
#define ARRAY_PUSH(array, item) {\
    if (array->size == array->capacity) {  \
        array->capacity *= 2;  \
        array->data = realloc(array->data, array->capacity * sizeof(*array->data)); \
        \
    }  \
    array->data[array->size++] = &item; \
}

//assert(array->data != NULL);
struct array_pointer_t {
    int16_t size;
    int16_t capacity;
    void ** data;
};



void benchmarkSerial() {
    int tog = 0;
    int end = 100000;

    for(int i=0; i < end; i++) {
        for(int j=0; j < end; j++)
        {
            if (tog == 0) tog = 1;
            else tog = 0;
        }
    }
}

void benchmarkParallel() {
    int tid;
    float sum = 0;
    int arraysize = 100000;
    float array[arraysize];

    for(int i=0;i<arraysize;i++){
        array[i] = 0.00001;
    }

	#pragma omp parallel for reduction(+:sum)
    for(int i=0; i < arraysize; i++) {
	    float tempsum = 0;
	for(int j=0; j < arraysize; j++ ){

        	tempsum += array[i];
	}
	sum += tempsum;
    }
}




void benchmarkMem() {
    struct array_pointer_t * bigStrArray;
    struct array_pointer_t * bigStrArray2;

    time_t t;
    int16_t j;
    j = 0;
    for (; j < 30000; j++){
        int16_t i;
        i = 0;
        ARRAY_CREATE(bigStrArray, 2, 0);
	ARRAY_CREATE(bigStrArray2, 2, 0);
        for (; i < 10000; i++) {
            //long randByte = (rand() * rand() % (1024 * 1024 / 2));
            	long randByte = i;
		long zero = 0;
		//printf("%dt \n");
		ARRAY_PUSH(bigStrArray, randByte);
		ARRAY_PUSH(bigStrArray2, zero);
        }
	i = 0;
	for(;i < 10000;i++){
		//printf("%d \n",i);
		bigStrArray2->data[i] = bigStrArray->data[i];
	}


	free(bigStrArray2->data);
	free(bigStrArray2);
        free(bigStrArray->data);
        free(bigStrArray);
    }
}

void benchmarkIO() {
  FILE *readFile;
  readFile = fopen("./readFile.txt", "w+");
  if(readFile == NULL){
      printf("Couldn't open file\n");
      return;
  }
  for (int i=0; i < 200000000; i++) {
    fprintf(readFile, "abcdefghi");
  }
  fclose(readFile);

  readFile = fopen("readFile.txt", "r");
  if(readFile == NULL){
      printf("Couldn't open file\n");
      return;
  }

  FILE *writeFile;
  writeFile = fopen("writeFile.txt", "w+");
  if(writeFile == NULL){
      printf("Couldn't open file\n");
      return;
  }
  char inString[10];
  while(fgets(inString, 10, readFile)){
    fprintf(readFile, "%s", inString);
  }

  fclose(readFile);
  fclose(writeFile);

  if(remove("writeFile.txt")==-1){
      printf("Issue deleting file\n");
      return;
  }
  if(remove("readFile.txt")==-1){
      printf("Issue deleting file\n");
      return;
  }

}

size_t noop_cb(void *ptr, size_t size, size_t nmemb, void *data){
	return 1;
}

void benchmarkComm(void) {
    int numPackets = 700;

    for(int i=0; i<numPackets; i++) {
        CURL *curl;
        CURLcode res;

        curl = curl_easy_init();
        if(curl) {
            curl_easy_setopt(curl, CURLOPT_URL, "http://google.com");
            curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L);
		//curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, noop_cb);


        res = curl_easy_perform(curl);
        if(res != CURLE_OK)
            fprintf(stderr, "curl_easy_perform() failed: %s\n", curl_easy_strerror(res));

        curl_easy_cleanup(curl);
        }
    }
   return;
}

void writeLog(double runTime) {
    // Open csv file
    FILE *file;
    file = fopen("../log.csv", "a");

    if(file == NULL){
        printf("Couldn't open file\n");
        return;
    }

    time_t t = time(NULL);
    struct tm *tm = localtime(&t);
    char s[64];
    size_t ret = strftime(s, sizeof(s), "%c", tm);
    assert(ret);

    fprintf(file, "%s, %f", s, runTime);
    fclose(file);
}

unsigned char *randomBytes(size_t num_bytes)
{
    unsigned char *stream = malloc (num_bytes);
    size_t i;

    for (i = 0; i < num_bytes; i++)
    {
        stream[i] = rand ();
    }

    return stream;
}

int main() {
    clock_t begin = clock();

#ifdef serial
    benchmarkSerial();
#endif

#ifdef parallel
    benchmarkParallel();

#endif

#ifdef mem
    benchmarkMem();
#endif

#ifdef comm
    benchmarkComm();
#endif

#ifdef io
    benchmarkIO();
#endif

    clock_t end = clock();

    double runTime = (double)(end-begin) / CLOCKS_PER_SEC;

    writeLog(runTime);

    return 0;
}
