#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>
#include <math.h>
#include <omp.h>      //OpenMP
int main(int argc, char* argv[])
{
    int n;
    double* a;
    double mean = 0;
    double sum = 0;
    double start_time, end_time;
    double elapsed_time;
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
  
    start_time = omp_get_wtime();
    #pragma omp parallel for reduction(+:sum)num_threads(16)
    for(int i = 0 ; i < n; i++)
    {
        sum += a[i];
    }
    mean = sum / n;
    printf("mean %lf \n", mean);
    sum = 0;
    #pragma omp parallel for reduction(+:sum)
    for(int i = 0 ; i < n; i++)
    {
        double diff =  pow((a[i] - mean), 2);
        sum += diff;
    }
    end_time = omp_get_wtime();
    elapsed_time = (double)(end_time - start_time);
    printf("Elapsed time: %f seconds\n", elapsed_time);
    double  varinace = sum / n;
    printf("varinace %lf \n", varinace);
    double standardDiv = sqrt(varinace);
    printf("standardDiv %lf \n", standardDiv);
    free(a);
}
