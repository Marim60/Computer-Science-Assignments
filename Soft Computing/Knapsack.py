import random

Crossover_Point = 0.5
mutation_rate = 0.05
num_generations = 100
population_size = 50


def initialize_population(population_size, num_of_items):
    pop = []
    while len(pop) != population_size:
        chromosome = []
        for _ in range(num_of_items):
            chromosome.append(random.choice([0, 1]))
        pop.append(tuple(chromosome))

    return pop


# calculates the fitness of a chromosome


def calculate_fitness(chromosome, items):
    total_weight = sum(chromosome[i] * items[i][0] for i in range(len(chromosome)))
    total_value = sum(chromosome[i] * items[i][1] for i in range(len(chromosome)))
    return total_value, total_weight


# probability of selection is proportional to sorted rank
def calculate_probability(sorted_list):
    probability = []
    n = len(sorted_list)
    total_rank = n * (n + 1) / 2
    for i in range(len(sorted_list)):
        probability.append((i + 1) / total_rank)
    return probability


# cumulative probability of selection is calculated
def calculate_cumulative_probability(probability):
    cumulative_probability = [probability[0]]
    for i in range(1, len(probability)):
        cumulative_probability.append(cumulative_probability[i - 1] + probability[i])
    return cumulative_probability


# chromosome is selected based on random number
def select_chromosome(cumulative_probability, random_number):
    for i in range(len(cumulative_probability)):
        if random_number <= cumulative_probability[i]:
            return i
    return len(cumulative_probability) - 1


# random number is generated for each chromosome
def generate_random_number():
    return random.random()


def rank_selection(chromosomes, population_size, items):
    fitness_list = []
    selected_chromosomes = []
    for i, chromosome in enumerate(chromosomes):
        fitness_value, fitness_weight = calculate_fitness(chromosome, items)
        fitness_list.append((fitness_value, i))  # Store fitness and original index as a tuple

    sorted_fitness = sorted(fitness_list, key=lambda x: x[0])
    probability_list = calculate_probability([x[0] for x in sorted_fitness])
    cumulative_probability = calculate_cumulative_probability(probability_list)
    for i in range(population_size):
        random_number = generate_random_number()
        indx = select_chromosome(cumulative_probability, random_number)
        selected_chromosome = chromosomes[sorted_fitness[indx][1]]  # Use the original index

        selected_chromosomes.append(selected_chromosome)
    return selected_chromosomes


def crossover(parent1, parent2, Crossover_Point):
    crossover_position = random.randint(1, len(parent1) - 1)
    crossover_chromosome = random.random()
    if crossover_chromosome <= Crossover_Point:
        offspring1 = parent1[:crossover_position] + parent2[crossover_position:]
        offspring2 = parent2[:crossover_position] + parent1[crossover_position:]
    else:
        offspring1 = parent1
        offspring2 = parent2
    return offspring1, offspring2


def mutation(chromosome, mutation_rate):
    for i in range(len(chromosome)):
        if random.random() <= mutation_rate:
            chromosome[i] = 1 - chromosome[i]
    return chromosome


def is_feasible(chromosome, Capacity, items):
    total_weight = sum(chromosome[i] * items[i][0] for i in range(len(chromosome)))
    return total_weight <= Capacity

def genetic_algorithm(num_generations, items, Capacity, Crossover_Point, mutation_rate, population_size, Num_of_items):
    population = initialize_population(population_size, len(items))
    for i in range(num_generations):
        parents = rank_selection(population, population_size, items)
        new_population = parents.copy()
        while len(new_population) < population_size:
            parent1, parent2 = random.choice(parents), random.choice(parents)
            offspring1, offspring2 = crossover(parent1, parent2, Crossover_Point)
            offspring1, offspring2 = mutation(offspring1, mutation_rate), mutation(offspring2, mutation_rate)
            new_population.extend([offspring1, offspring2])
            population = new_population
    best_feasible_chromosome = None
    best_feasible_value = 0

    # [(1,0,1), (1,1,1)]
    for chromosome in population:
        if is_feasible(chromosome, Capacity, items):
            value, weight = calculate_fitness(chromosome, items)
            if value > best_feasible_value:
                best_feasible_value = value
                best_feasible_chromosome = chromosome
    if best_feasible_chromosome:
        best_value, best_weight = calculate_fitness(best_feasible_chromosome, items)
        print("Number of selected items: ", sum(best_feasible_chromosome))
        print("Total value: ", best_value)
        print("Total weight: ", best_weight)
        print("Selected items: ")
        for i in range(len(best_feasible_chromosome)):
            if best_feasible_chromosome[i] == 1:
                print(f"Items {i + 1}: Weight: {items[i][0]}, Value: {items[i][1]}")
        print()
    else:
        print("No feasible solution found.\n")

def __main__():

    with open("knapsack_input.txt", 'r') as file:

        num_test_case = int(file.readline())

        for i in range(num_test_case):

            file.readline()
            file.readline()

            knapsack_size = int(file.readline())
            num_of_items = int(file.readline())

            items = []

            for j in range(num_of_items):
                line = file.readline()
                line = line.split()

                line = map(int, line)

                item = tuple(line)

                items.append(item)

            print(f"Test Case {i + 1}:")
            genetic_algorithm(num_generations, items, knapsack_size, Crossover_Point, mutation_rate, population_size, num_of_items)

if __name__ == '__main__':
    __main__()
