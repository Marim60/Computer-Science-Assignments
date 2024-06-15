import random

genes_range = [-10, 10]
num_generations = 50
population_size = 100
mutation_rate = 0.1
dependency_factor = 1
Crossover_Point = 0.5


# 1- Initialize the population
def initialize_population(population_size, polynomial_degree):
    pop = []
    while len(pop) != population_size:
        chromosome = []
        for _ in range(polynomial_degree + 1):
            chromosome.append(random.uniform(genes_range[0], genes_range[1]))
        pop.append(tuple(chromosome))
    return pop


# 2- Calculate the fitness value of a chromosome
def calculate_fitness(chromosome, points, polynomial_degree):
    fitness = 0
    for point in points:
        x = point[0]
        y = point[1]
        expected_value = 0
        error = 0
        for i in range(0, polynomial_degree + 1):
            expected_value += chromosome[i]*(x ** i)
        error += (expected_value - y) ** 2
        fitness += error
    fitness /= len(points)
    return 1 / fitness


# 3- Tournament Selection Technique
def select_tournament(chromosomes, population_size, points, polynomial_degree):
    mating_pool = []
    fitness_chromosome = []
    for i, chromosome in enumerate(chromosomes):
        fitness_value = calculate_fitness(chromosome, points, polynomial_degree)
        fitness_chromosome.append((fitness_value, i))
    tournament_size = random.randint(2, population_size)
    while len(mating_pool) < population_size:
        tournament_chromosomes = random.sample(fitness_chromosome, tournament_size)
        best_index = min(tournament_chromosomes, key=lambda x: x[0])[1]
        mating_pool.append(chromosomes[best_index])  # append the chromosome with the lowest fitness value

    return mating_pool


# 4- Crossover
def two_point_crossover(parent1, parent2):
    crossover_point1, crossover_point2 = sorted(random.sample(range(len(parent1)), 2))
    crossover_chromosome = random.random()
    if crossover_chromosome <= Crossover_Point:
        offspring1 = parent1[:crossover_point1] + parent2[crossover_point1:crossover_point2] + parent1[
                                                                                               crossover_point2:]
        offspring2 = parent2[:crossover_point1] + parent1[crossover_point1:crossover_point2] + parent2[
                                                                                               crossover_point2:]
    else:
        offspring1 = parent1
        offspring2 = parent2
    return offspring1, offspring2


# 5- Mutation
def non_uniform_mutation(gene, generation):
    rm = random.random()
    #print(f"rm = {rm}")
    #print(f"gene = {gene}")
    if rm <= mutation_rate:
        delta_Lower = gene - genes_range[0]
        #print(f"delta_Lower = {delta_Lower}")
        delta_Upper = genes_range[1] - gene
        #print(f"delta_Upper = {delta_Upper}")
        r1 = random.random()
        y = delta_Lower if r1 <= 0.5 else delta_Upper
        #print(f"y = {y}")
        r = random.random()
        delta = y * (1 - r ** (1 - generation / num_generations) ** dependency_factor)
        #print(f"delta = {delta}")
        if r1 <= 0.5:
            gene = gene - delta
        else:
            gene = gene + delta
        #print(f"gene after mutation = {gene}")
        #gene += delta

    return gene


# 6- Replacement
def elitism_replacement(population, offspring, polynomial_degree, points):
    new_population = population + offspring
    new_population.sort(key=lambda x: calculate_fitness(x, points, polynomial_degree))
    return new_population[:population_size]

def elitismReplacement(population,polynomial_degree, points):
    num_elite = int(0.2 * len(population))
    population.sort(key=lambda x: calculate_fitness(x, points, polynomial_degree))
    elite_chromosomes = population[:num_elite]
    return elite_chromosomes


# 7- Genetic Algorithm
def genetic_algorithm(population_size, points, polynomial_degree):
    population = initialize_population(population_size, polynomial_degree)
    for generation in range(num_generations):
        mating_pool = select_tournament(population, population_size, points, polynomial_degree)
        offspring = []
        num_elite = elitismReplacement(population, polynomial_degree, points)
        while len(offspring) < population_size - len(num_elite):
            parent1, parent2 = random.sample(mating_pool, 2)
            offspring1, offspring2 = two_point_crossover(parent1, parent2)
            offspring1 = tuple([non_uniform_mutation(gene, generation) for gene in offspring1])
            offspring2 = tuple([non_uniform_mutation(gene, generation) for gene in offspring2])
            offspring.append(offspring1)
            offspring.append(offspring2)
        #population = elitism_replacement(population, offspring, polynomial_degree, points)
        population = num_elite + offspring
    return population[0]


# print(genetic_algorithm(population_size, [(0.0 ,0.414551),(0.1, 0.534459),(0.2, 0.682073)], 3))
# 8- Main
def __main__():
    with open("curve_fitting_input.txt", 'r') as file:

        num_test_case = int(file.readline())

        for i in range(num_test_case):
            line = file.readline()
            line = line.split()

            line = list(map(int, line))
            n = line[0]
            polynomial_degree = line[1]
            points = []
            for j in range(n):
                line = file.readline()
                line = line.split()

                line = map(float, line)
                point = tuple(line)
                points.append(point)


            print(f"Test Case {i + 1}:")
            chromosome = genetic_algorithm(population_size, points, polynomial_degree)
            print(chromosome)
            print(f"Fitness Value = {calculate_fitness(chromosome, points, polynomial_degree)}")
            with open("curve_fitting_output.txt", 'a') as output_file:
                output_file.write(f"Test Case {i + 1}:\n")
                output_file.write(f"{chromosome}\n")
                output_file.write(f"Fitness Value = {calculate_fitness(chromosome, points, polynomial_degree)}\n")
                output_file.write("\n")

if __name__ == '__main__':
    __main__()