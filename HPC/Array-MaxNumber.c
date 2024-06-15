#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>


// size of array
int max(int x, int y)
{
    if (x >= y)
        return x;
    else
        return y;
}
// Temporary array for slave process
int* a2;
int main(int argc, char* argv[])
{

    int my_rank;
    int numOfProcess;
    int elementsPerProcess;
    int numOfElementsRrecieved;
    int n;
    int* a;
    int slave = 0;
    MPI_Status status;
    MPI_Init(&argc, &argv);
    MPI_Comm_rank(MPI_COMM_WORLD, &my_rank);
    MPI_Comm_size(MPI_COMM_WORLD, &numOfProcess);
    // master process
    if (my_rank == 0) {
        printf("Hello from master process. \n");
        printf("Enter size of the array : \n");
        scanf("%d", &n);
        a = (int*)malloc(n * sizeof(int));
        printf("Enter the array : \n");
        if (a != NULL)
        {
            int i;
            for (i = 0; i < n; ++i) {
                scanf("%d", &a[i]);
            }
        }
        int index, i;
        elementsPerProcess = n / numOfProcess;

        if (numOfProcess > 1) {
            for (i = 1; i < numOfProcess - 1; i++) {
                index = i * elementsPerProcess;

                MPI_Send(&elementsPerProcess, 1, MPI_INT, i, 0, MPI_COMM_WORLD);
                MPI_Send(&a[index], elementsPerProcess,MPI_INT, i, 0,MPI_COMM_WORLD);
            }
            index = i * elementsPerProcess;
            int elements_left = n - index;

            MPI_Send(&elements_left,1, MPI_INT,i, 0,MPI_COMM_WORLD);
            MPI_Send(&a[index],elements_left,MPI_INT, i, 0,MPI_COMM_WORLD);
        }
        int mx = a[0];
        for (i = 0; i < elementsPerProcess; i++)
            mx = max(mx, a[i]);
        int tmp,idMx;
        int* arr = (int*)malloc((numOfProcess -1) * sizeof(int));
        for (i = 1; i < numOfProcess; i++) {
            MPI_Recv(&tmp, 1, MPI_INT,MPI_ANY_SOURCE, 0,MPI_COMM_WORLD,&status);
            MPI_Recv(&idMx, 1, MPI_INT,MPI_ANY_SOURCE, 0,MPI_COMM_WORLD,&status);
            printf("Hello from slave# %d , Max number in my partition is %d , and index is %d\n" ,i ,tmp, idMx);
            arr[i - 1] = tmp;
        }
        for (int j = 0; j < numOfProcess - 1; ++j) {
            mx = max(arr[j], mx);
        }
        printf("max element of array is : %d\n", mx);
    }
    // slave processes
    else {

        MPI_Recv(&numOfElementsRrecieved,1, MPI_INT, 0, 0,MPI_COMM_WORLD,&status);
        a2 = (int*)malloc(numOfElementsRrecieved * sizeof(int));
        MPI_Recv(a2, numOfElementsRrecieved,MPI_INT, 0, 0,MPI_COMM_WORLD,&status);
        int mx = a2[0];
        int index = 0;
        for (int i = 0; i < numOfElementsRrecieved; i++)
        {
            if (mx < a2[i])
            {
                mx = a2[i];
                index = i;
            }
        }
        MPI_Send(&mx, 1, MPI_INT,0, 0, MPI_COMM_WORLD);
        MPI_Send(&index, 1, MPI_INT,0, 0, MPI_COMM_WORLD);


    }
    MPI_Finalize();
    free(a2);
    free(a);
    return 0;
}