#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <time.h>
bool is_prime(int x)
{
    if(x == 0 || x == 1)
       return false;
    for(int i = 2; i <= x / 2; i++)
    {
        if (x % i == 0)
            return false;        
    }
    return true;
    
}

int main(int argc, char* argv[])
{
    int my_rank;
    int numOfProcess;
    int lowerBound;
    int upperBound;
    int elementsPerProcess;
    int startIndex;
    int endIndex;
    int totalCount = 0;
    int *slavePrime;
    int* primeArr;
    MPI_Status status;
    clock_t start_t, end_t;
    double total_t;
    MPI_Init(&argc, &argv);
    start_t = clock();
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &numOfProcess);
    if(my_rank == 0)
    {
        printf("Enter lower bound and upper bound numbers\n");
        scanf("%d %d", &lowerBound, &upperBound);
        elementsPerProcess = (upperBound - lowerBound) / numOfProcess;
        startIndex = elementsPerProcess + lowerBound;
        endIndex = startIndex + elementsPerProcess;
        if(numOfProcess > 1)
        {
            for (int i = 1; i < numOfProcess - 1; i++)
            {
                MPI_Send(&startIndex, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
                MPI_Send(&endIndex, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
                startIndex += elementsPerProcess;
                endIndex = startIndex + elementsPerProcess;
            }
            endIndex = upperBound + 1;
            MPI_Send(&startIndex, 1, MPI_INT, numOfProcess - 1, 0, MPI_COMM_WORLD);
            MPI_Send(&endIndex, 1, MPI_INT, numOfProcess - 1, 0, MPI_COMM_WORLD);
            //printf("upper %d\n", upperBound);
        }
        primeArr = (int*)malloc(((upperBound - lowerBound) + 1) * sizeof(int));
        // master do his work
        for (int i = lowerBound; i < elementsPerProcess + lowerBound; i++)
        {
            //printf("i master%d \n", i);
            if(is_prime(i))
              { 
                  primeArr[totalCount] = i;                 
                  totalCount++;
              }
        }
        int countPrime = 0;
        int start = totalCount;
        for (int i = 1; i < numOfProcess; i++)
        {
            MPI_Recv(&countPrime, 1, MPI_INT, i, 0 ,MPI_COMM_WORLD,&status);
            MPI_Recv(&startIndex, 1, MPI_INT, i, 0 ,MPI_COMM_WORLD,&status);
            MPI_Recv(&endIndex, 1, MPI_INT, i, 0 ,MPI_COMM_WORLD,&status);
            printf("count prime %d \n ", countPrime);
            totalCount += countPrime;
            printf("p %d : calculate partial count of prime numbers from %d to %d -> count= %d ( ",i, startIndex, endIndex, countPrime);
            for (int j = start; j < totalCount; j++)
            {
                MPI_Recv(&primeArr[j], 1, MPI_INT, i, 0 ,MPI_COMM_WORLD,&status); 
                printf("%d, ", primeArr[j]);
            }
            printf(")\n");
            start = totalCount;
        }
        end_t = clock();
        printf("P0 will have Count = %d (", totalCount);
        for (int i = 0; i < totalCount; i++)
        {
            printf("%d, ", primeArr[i]);
        }
        printf(")\n");
        total_t = (double)(end_t - start_t) / CLOCKS_PER_SEC;
        printf("total time %f per second\n", total_t);
    }
    // slave do his work
    else
    {
        int startIndex,endIndex;
        MPI_Recv(&startIndex,1, MPI_INT, 0, 0,MPI_COMM_WORLD,&status);
        //printf("startIndex %d \n ", startIndex);
        MPI_Recv(&endIndex, 1, MPI_INT, 0, 0,MPI_COMM_WORLD,&status);
        //printf("endIndex %d \n ", endIndex);
        int count = 0;
        slavePrime = (int*)malloc((endIndex - startIndex)* sizeof(int));
        for(int i = startIndex ; i < endIndex; i++)
        {
            //printf("i %d \n", i);
            if(is_prime(i))
              { 
                //printf("hello\n");
                  slavePrime[count] = i;  
                  //printf("slavePrime= %d \n", slavePrime[count]);               
                  count++;
              }
        }
        //printf("count %d \n ", count);
        MPI_Send(&count, 1, MPI_INT,0, 0, MPI_COMM_WORLD);
        MPI_Send(&startIndex, 1, MPI_INT,0, 0, MPI_COMM_WORLD);
        MPI_Send(&endIndex, 1, MPI_INT,0, 0, MPI_COMM_WORLD);
        for (int i = 0; i < count; i++)
        {
            MPI_Send(&slavePrime[i], 1, MPI_INT,0, 0, MPI_COMM_WORLD);
        }
        
    }
     MPI_Finalize();
     free(slavePrime);
     free(primeArr);
     return 0;
}