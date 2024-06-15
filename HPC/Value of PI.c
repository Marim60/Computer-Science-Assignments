#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
int main(int argc, char* argv[])
{
    int my_rank;
    int numOfProcess;
    static long num_steps = 1000000;
    int numOfStepsPerProcess;
    int startIndex;
    int endIndex;
    double step;
    double x;
    double sum = 0.0;
    double totalSum = 0.0;
    double pi;
    MPI_Status status;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &numOfProcess);
    if(my_rank == 0)
    {
        step = 1.0 / (double)num_steps;
        numOfStepsPerProcess = num_steps / numOfProcess;
    }
    MPI_Bcast(&step, 1, MPI_DOUBLE, 0, MPI_COMM_WORLD);
    MPI_Bcast(&numOfStepsPerProcess, 1, MPI_INT, 0, MPI_COMM_WORLD);
    startIndex = numOfStepsPerProcess * my_rank;
    endIndex =  numOfStepsPerProcess * (my_rank + 1);
    for(int i = startIndex; i < endIndex; i++)
    {
        x = ((double)(i + 0.5))*step;
        sum += 4.0/(1.0+x*x);
    }
    MPI_Reduce(&sum, &totalSum, 1, MPI_DOUBLE, MPI_SUM, 0, MPI_COMM_WORLD);
    if (my_rank == 0)
    {
        int startIndex = numOfStepsPerProcess * numOfProcess;
        for (int i = startIndex; i < num_steps; i++)
        {
            x = ((double)(i + 0.5))*step;
            totalSum += 4.0/(1.0+x*x);
        }
         pi = step*totalSum;
        printf("%.20f", pi);
    }
    MPI_Finalize();
    

}