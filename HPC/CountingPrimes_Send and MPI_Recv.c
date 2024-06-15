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
    }

    MPI_Bcast(&elementsPerProcess, 1, MPI_INT, 0, MPI_COMM_WORLD);
 
    MPI_Bcast(&lowerBound, 1, MPI_INT, 0, MPI_COMM_WORLD);

    startIndex = lowerBound + (elementsPerProcess * my_rank);

    endIndex = lowerBound + (elementsPerProcess * (my_rank + 1));

 
    int count = 0;
    for(int i = startIndex ; i < endIndex; i++)
    {
        if(is_prime(i))
            { 
                //printf("Prime= %d my_rank = %d\n", i, my_rank);               
                count++;
            }
    }
    printf("count %d my_rank %d \n ", count, my_rank);
    MPI_Reduce(&count, &totalCount, 1, MPI_INT, MPI_SUM, 0, MPI_COMM_WORLD);
    if(my_rank == 0)
    {
        elementsPerProcess = ((upperBound - lowerBound)) % numOfProcess;
        for(int i = upperBound; i >= upperBound - elementsPerProcess ; i--)
        {
            if(is_prime(i))
            {
                totalCount++;
            }
        }
        end_t = clock();
        printf("totalCount = %d \n", totalCount);
        total_t = (double)(end_t - start_t) / CLOCKS_PER_SEC;
        printf("total time %f per second\n", total_t);
    }
    MPI_Finalize();
     return 0;
}