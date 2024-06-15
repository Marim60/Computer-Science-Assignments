#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
int main(int argc, char* argv[])
{
    int my_rank;
    int numOfProcess;
    int elementsPerProcess;
    int startIndex;
    int endIndex;
     int n;
    double* a;
    double* subArray;
    double mean;
    double startTime, endTime;
    double totalTime;
    MPI_Status status;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &numOfProcess);
    if(my_rank == 0)
    {
        printf("Enter size of the array : \n");
        scanf("%d", &n);
        a = (double*)malloc(n * sizeof(double));
        int choice;
        printf("Enter 1 for random 2 for console : \n");
        scanf("%d", &choice);
        switch(choice)
        {
            case 1:
                    for(int i=0;i<n;i++)
                        a[i]=rand()%10;
                break;
            case 2:
            { 
                printf("Enter the array : \n");
                if (a != NULL)
                {
                    int i;
                    for (i = 0; i < n; ++i) {
                        scanf("%lf", &a[i]);
                    }
                }
            }
            break;
        }
        elementsPerProcess = n / numOfProcess;
        startTime = MPI_Wtime();
    }
    MPI_Bcast(&elementsPerProcess, 1, MPI_INT, 0, MPI_COMM_WORLD);
    subArray= (double*)malloc(elementsPerProcess * sizeof(double));
    MPI_Scatter(a, elementsPerProcess, MPI_DOUBLE, subArray, elementsPerProcess, MPI_DOUBLE, 0, MPI_COMM_WORLD);
    double subSum = 0;
    for(int i = 0 ; i < elementsPerProcess; i++)
    {
        subSum += subArray[i];
    }
    double totalSum;
    MPI_Allreduce(&subSum, &totalSum, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
    if(my_rank == 0)
    {
        int element = elementsPerProcess * numOfProcess;
       for(int i = element; i < n ; i++)
        {
           totalSum += a[i];
        }
         mean = totalSum / n;
         printf("mean %lf \n", mean);
    }
    MPI_Bcast(&mean, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD);
    subSum = 0;
    for(int i = 0 ; i < elementsPerProcess; i++)
    {
        double diff = (subArray[i] - mean);
        diff =  pow(diff, 2);
        subSum += diff;
    }
    totalSum = 0;
    MPI_Allreduce(&subSum, &totalSum, 1, MPI_DOUBLE, MPI_SUM, MPI_COMM_WORLD);
    if(my_rank == 0)
    {
        int element = elementsPerProcess * numOfProcess;
        for(int i = element; i < n ; i++)
        {
            double diff =  pow((a[i] - mean), 2);
            totalSum += diff;
        }
        endTime = MPI_Wtime();
        double  varinace = totalSum / n;
       printf("varinace %lf \n", varinace);
        double standardDiv = sqrt(varinace);
       printf("standardDiv %lf \n", standardDiv);
       totalTime = endTime - startTime;
       printf("total time = %lf\n", totalTime);
    }
    MPI_Finalize();
    free(a);
    free(subArray);
     return 0;
}