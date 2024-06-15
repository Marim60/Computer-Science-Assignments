#include <mpi.h>
#include <stdio.h>
#include <stdlib.h>
int rows1,col1; int **matrix1;
int rows2,col2; int **matrix2;
int** dynamicAlocation(int numOfRow , int numOfColoum)
{
    int ** arr;  
    arr = (int **)malloc(numOfRow * sizeof(int *));
    for(int i=0;i<numOfRow;i++)
    {
        arr[i]=(int*)malloc(numOfColoum*sizeof(int));
    }
    return arr;
}
void delete(int **m, int n)
{
     for(int i = 0; i < n; i++)
        free(m[i]);
}
void printMultiplicationMatrix(int **ary,int row,int col)
{
    for (int i = 0; i <row; ++i) {
        for (int j = 0; j < col; ++j) {
            printf("%d ",ary[i][j]);
        }
        printf("\n");
    }
}
void console(){
        printf("Enter dimentions of the first matrix: ");
        fflush(stdout); //flush after printing so it appeares before input.
        scanf("%d %d",&rows1,&col1);
        //building the 2d array.
        matrix1 = dynamicAlocation(rows1,col1);;
        for (int i = 0; i < rows1; i++)
        {
            matrix1[i] = (int*)malloc(col1 * sizeof(int));
        }
        printf("Enter elements of the first matrix: \n");
        fflush(stdout);
        for (int i = 0; i < rows1; i++)
        {
            for (int j = 0; j < col1; j++)
            {
                scanf("%d",&matrix1[i][j]);
            }
            
        }
        //print_2d(matrix1, rows1, col1);
        printf("\n");
        printf("Enter dimentions of the second matrix: ");
        fflush(stdout); //flush after printing so it appeares before input.
        scanf("%d %d",&rows2,&col2);
        //building the 2d array.
        matrix2 = dynamicAlocation(rows2,col2);;
        for (int i = 0; i < rows2; i++)
        {
            matrix2[i] = (int*)malloc(col2 * sizeof(int));
        }
        //taking array elements as input.
        printf("Enter elements of the second matrix: \n");
        fflush(stdout);
        for (int i = 0; i < rows2; i++)
        {
            for (int j = 0; j < col2; j++)
            {
                scanf("%d",&matrix2[i][j]);
            }
            
        }
}
void file(){
        FILE *matrixFile;
        matrixFile = fopen("matrixFile.txt", "r");
        if(matrixFile != NULL)
        {
            fscanf(matrixFile,"%d", &rows1);
            fscanf(matrixFile,"%d", &col1);
            matrix1 = dynamicAlocation(rows1,col1);
            for (int i = 0; i < rows1; i++)
            {
                for (int j = 0; j < col1; j++)
                {
                    fscanf(matrixFile,"%d",&matrix1[i][j]);
                }
            }
            fscanf(matrixFile,"%d", &rows2);
            fscanf(matrixFile,"%d", &col2);
            // printf("%d \n %d \n", rows2,col2);
            fflush(stdout);
            matrix2 = dynamicAlocation(rows2,col2);
            for (int i = 0; i < rows2; i++)
            {
                for (int j = 0; j < col2; j++)
                {
                    fscanf(matrixFile,"%d",&matrix2[i][j]);
                }
            }
        }

        fclose(matrixFile);

}
int main() {
    int source; /* rank of sender */
    int tag = 0; /* tag for messages */
    MPI_Status status; /* return status for */
  
    int numProcess;
    int rank;
    int **multiplicationMatrix;
    int indexOfRow;
    int numOfRowsPerSlave;
    int numOfElemnts;
    int ** multiplication;
    int ** array;
    int ** temp1;
    // Initialize the MPI environment
    MPI_Init(NULL, NULL);
    // Get the number of processes
    MPI_Comm_size(MPI_COMM_WORLD, &numProcess);
    
    // Get the rank of the process
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    //make variables for all processes.
    // take variable input at master process.
    if(rank==0)
    {
        printf("To read dimensions and values from console press 1\n");
        printf("To read dimensions and values from file press 2\n");
        fflush(stdout);
        int choice;
        scanf("%d",&choice);
        switch(choice){
            case 1:
               console();
               break;
            case 2:
                file();
                break;
        }
       
        if(rows2 != col1)
        {
            printf("invalid inputs\n");
            MPI_Abort(MPI_COMM_WORLD,1);
            return 0;
        }
        multiplicationMatrix = dynamicAlocation(rows1,col2);
        // divide rows to slaves
        numOfRowsPerSlave = rows1 / numProcess;
        indexOfRow = numOfRowsPerSlave;
        int ind = 1;
        if(numProcess > 1)
        {
            for(int i = 1; i < numProcess - 1; i++)
            {
                MPI_Send(&rows2, 1, MPI_INT, i, tag, MPI_COMM_WORLD);
                MPI_Send(&rows1, 1, MPI_INT, i, tag, MPI_COMM_WORLD);
                MPI_Send(&col1, 1, MPI_INT, i, tag, MPI_COMM_WORLD);
                MPI_Send(&col2, 1, MPI_INT, i, tag, MPI_COMM_WORLD);
                MPI_Send(&indexOfRow, 1, MPI_INT, i, tag, MPI_COMM_WORLD);
                MPI_Send(&numOfRowsPerSlave, 1, MPI_INT, i, tag, MPI_COMM_WORLD);
                for (size_t j = 0; j < numOfRowsPerSlave; j++)
                    MPI_Send(matrix1[j], col1, MPI_INT, i, 0, MPI_COMM_WORLD); 
                
                for (size_t j = 0; j <rows2; j++)
                    MPI_Send(matrix2[j], col2, MPI_INT, i, 0, MPI_COMM_WORLD);
                indexOfRow += numOfRowsPerSlave;
                ind = i;
        }
            MPI_Send(&rows2, 1, MPI_INT, ind + 1, tag, MPI_COMM_WORLD);
            MPI_Send(&rows1, 1, MPI_INT, ind + 1, tag, MPI_COMM_WORLD);
            MPI_Send(&col1, 1, MPI_INT, ind + 1, tag, MPI_COMM_WORLD);
            MPI_Send(&col2, 1, MPI_INT, ind + 1, tag, MPI_COMM_WORLD);
            int rimender = (rows1 % numProcess) + numOfRowsPerSlave;
            MPI_Send(&indexOfRow, 1, MPI_INT, ind + 1, tag, MPI_COMM_WORLD);
            MPI_Send(&rimender, 1, MPI_INT, ind + 1, tag, MPI_COMM_WORLD);
            for (size_t j = 0; j <rimender; j++)
            {
                MPI_Send(matrix1[j], col1, MPI_INT, ind + 1, 0, MPI_COMM_WORLD); 
            }
            for (size_t j = 0; j <rows2; j++)
            {
                MPI_Send(matrix2[j], col2, MPI_INT, ind + 1, 0, MPI_COMM_WORLD); 
            }
        }
    
        // master work in matrix;
        for(int i = 0; i < numOfRowsPerSlave; i++)
        {
            for(int j = 0; j < col2; j++)
            {
                multiplicationMatrix[i][j] = 0;
                for(int k = 0; k < rows2; k++)
                {
                    multiplicationMatrix[i][j] += (matrix1[i][k] * matrix2[k][j]);
                }
            }
        }
        for(int i = 1; i < numProcess; i++)
        {
            MPI_Recv(&indexOfRow, 1,MPI_INT, i, tag, MPI_COMM_WORLD, &status);
            MPI_Recv(&numOfRowsPerSlave, 1, MPI_INT,i, tag, MPI_COMM_WORLD, &status);
             for (int j = indexOfRow; j < numOfRowsPerSlave + indexOfRow; j++)
            {
                MPI_Recv(multiplicationMatrix[j], col2, MPI_INT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
            }
        }
        printf("result: \n");
        printMultiplicationMatrix(multiplicationMatrix, rows1, col2);
        
    }
    // slave
    else
    {
        
        printf("Hello slave \n");
        MPI_Recv(&rows2, 1, MPI_INT, source, tag, MPI_COMM_WORLD, &status);
        MPI_Recv(&rows1, 1, MPI_INT, source, tag, MPI_COMM_WORLD, &status);
        MPI_Recv(&col1, 1, MPI_INT, source, tag, MPI_COMM_WORLD, &status);
        MPI_Recv(&col2, 1, MPI_INT, source, tag, MPI_COMM_WORLD, &status);
        MPI_Recv(&indexOfRow, 1, MPI_INT, source, tag, MPI_COMM_WORLD, &status);
        MPI_Recv(&numOfRowsPerSlave, 1, MPI_INT, source, tag, MPI_COMM_WORLD, &status);
         temp1 = dynamicAlocation(numOfRowsPerSlave,col1);
         for (int j = 0; j < numOfRowsPerSlave; j++)
        {
            MPI_Recv(temp1[j], col1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        }
       array = dynamicAlocation(rows2,col2);
        for (size_t j = 0; j <rows2; j++)
        {
            MPI_Recv(array[j], col2, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        }
       multiplication = dynamicAlocation(numOfRowsPerSlave,col2);
        for(int i = 0; i < numOfRowsPerSlave; i++)
        {
            for(int j = 0; j < col2; j++)
            {
                multiplication[i][j] = 0;
                for(int k = 0; k < rows2; k++)
                {
                    multiplication[i][j] += (temp1[i][k] * array[k][j]);
                }
            }
        }
        MPI_Send(&indexOfRow, 1, MPI_INT, 0, tag, MPI_COMM_WORLD);
        MPI_Send(&numOfRowsPerSlave, 1, MPI_INT, 0, tag, MPI_COMM_WORLD);
        for (size_t j = 0; j < numOfRowsPerSlave; j++)
        {
          MPI_Send(multiplication[j], col2, MPI_INT, 0, tag, MPI_COMM_WORLD); 
        }

    }
    MPI_Finalize();
    delete(matrix1,rows1 );
    delete(matrix2, rows2);
    delete(multiplicationMatrix, rows1);
    delete(multiplication, numOfRowsPerSlave);
    delete(array, rows2);
    delete(temp1, numOfRowsPerSlave);
    return 0;
}
