#  Fuzzy Logic Toolbox Variables and Rules
fuzzy_variables = []
statement_rules = []
crisp_variables = []
fuzzy = []

# data structure for saving fuzzy set
class FuzzySet:
    set_name = ""
    type = ""
    values = []
    centroid = 0

    def __init__(self, set_name, type, values):
        self.set_name = set_name
        self.type = type
        self.values = values

    def calc_centroid(self):
        sum = 0
        for value in self.values:
            sum += value

        self.centroid = sum / len(self.values)


# data structure for saving fuzzy variable
class FuzzyVariable:
    name = ""
    type = ""
    values = []
    fuzzy_sets = []

    def __init__(self, name, type, values, fuzzy_sets):
        self.name = name
        self.type = type
        self.values = values
        self.fuzzy_sets = fuzzy_sets

    def add_fuzzy_set(self, fuzzy_set):
        self.fuzzy_sets.append(fuzzy_set)


# data structure for handle fuzzy logic
class FuzzyLogic:
    rule = []

    def __init__(self, rule):
        self.rule = rule

    def oper_and(self, a, b):
        return min(a, b)

    def oper_or(self, a, b):
        return max(a, b)

    def oper_not(self, a):
        return 1 - a

    def get_rule(self, statement_rule):
        self.rule = statement_rule.split()

# data structure to use in the inference process
class InVariable:
    fuzzy_sets = []
    variable_name = ""

    def __init__(self, variable_name):
        self.variable_name = variable_name
        self.fuzzy_sets = []

    def add_fuzzy_set(self, fuzzy_set):
        self.fuzzy_sets.append(fuzzy_set)

# calculate the intersection point between two lines
def line_intersection(x1, y1, x2, y2, x_point):
    slope = (y2 - y1) / (x2 - x1)
    b = y2 - slope * x2
    y_point = slope * x_point + b
    return y_point

# check if two points are equal
def is_equal(point1, point2):
    return point1 == point2

# calculate the range of a point
def point_range(x_point, fuzzy_sets):
    x_point_ranges = {}
    for fuzzy_set in fuzzy_sets:
        # if shape is triangle
        if fuzzy_set.type == 'TRI':
            if not is_equal(fuzzy_set.values[0], fuzzy_set.values[1]):
                if fuzzy_set.values[0] <= x_point <= fuzzy_set.values[1]:
                    x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[0], 0), (fuzzy_set.values[1], 1)]
                    continue
            if fuzzy_set.values[1] <= x_point <= fuzzy_set.values[2]:
                x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[1], 1), (fuzzy_set.values[2], 0)]
                continue
            if not is_equal(fuzzy_set.values[0], fuzzy_set.values[1]):
                x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[0], 0), (fuzzy_set.values[1], 0)]
                continue
            else:
                x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[1], 0), (fuzzy_set.values[2], 0)]
                continue
            
        # if shape is trapezoid
        if fuzzy_set.type == 'TRAP':
            if not is_equal(fuzzy_set.values[0], fuzzy_set.values[1]):
                if fuzzy_set.values[0] <= x_point <= fuzzy_set.values[1]:
                    x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[0], 0), (fuzzy_set.values[1], 1)]
                    continue

            if fuzzy_set.values[1] <= x_point <= fuzzy_set.values[2]:
                x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[1], 1), (fuzzy_set.values[2], 1)]
                continue

            if fuzzy_set.values[2] <= x_point <= fuzzy_set.values[3]:
                x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[2], 1), (fuzzy_set.values[3], 0)]
                continue

            x_point_ranges[fuzzy_set.set_name] = [(fuzzy_set.values[1], 0), (fuzzy_set.values[2], 0)]
            continue

    return x_point_ranges

# get the index of a fuzzy variable in the fuzzy variables list
def find_fuzzy_variable(variable, fuzzy_variables):
    for i, fuzzy_variable in enumerate(fuzzy_variables):
        if variable[0] == fuzzy_variable.name:
            return i

# fuzzification
def fuzzification(variables, fuzzy_variables):
    in_variables = []
    for variable in variables:
        # get the index of the fuzzy variable in the fuzzy variables list
        index = find_fuzzy_variable(variable, fuzzy_variables)
        # get the of the points (x, y) of the fuzzy sets that the crisp value is in their range
        fuzzy_points = point_range(variable[1], fuzzy_variables[index].fuzzy_sets)
        # create inference variable to use in the inference process
        inference_in_variable = InVariable(fuzzy_variables[index].name)
        for fuzzy_name in fuzzy_points:
            y_point = line_intersection(fuzzy_points[fuzzy_name][0][0], fuzzy_points[fuzzy_name][0][1],
                                        fuzzy_points[fuzzy_name][1][0], fuzzy_points[fuzzy_name][1][1], variable[1])
            inference_in_variable.add_fuzzy_set((fuzzy_name, y_point))
        in_variables.append(inference_in_variable)

    return in_variables

# Get degree of membership (Y) of a fuzzy set in a inference variable
def find_inference_variable(in_variables, fuzzy_variable, fuzzy_set_variable):
    for in_variable in in_variables:
        if in_variable.variable_name == fuzzy_variable:
            for fuzzy_set in in_variable.fuzzy_sets:
                if fuzzy_set[0] == fuzzy_set_variable:
                    # return Y
                    return fuzzy_set[1]

# apply inference rules
def inference(statement_rules, in_variables):
    rule1 = statement_rules[0].split()
    fuzzy_output_variable = rule1[len(rule1) - 2] # get the output variable name
    # create fuzzy output variable
    defuzzification_output = InVariable(fuzzy_output_variable)
    
    # apply inference rules
    for statement in statement_rules:
        # stack for processing the postfix rule for handling the operators precedence
        stack = [] 
        inference_result = 0
        # split the rule to get the fuzzy vairable name and value and operators
        rule = statement.split()
        fuzzy_logic = FuzzyLogic(rule)
        postfix_rule = infix_to_postfix(rule, in_variables)
        postfix_rule = postfix_rule.split()
        # apply the postfix rule
        for r in postfix_rule:
            if r not in ["and", "or", "not"]:
                stack.append(float(r))
            else:
                if r == 'and':
                    y2 = stack.pop()
                    y1 = stack.pop()
                    inference_result = fuzzy_logic.oper_and(y1, y2)
                if r == 'or':
                    y2 = stack.pop()
                    y1 = stack.pop()
                    inference_result = fuzzy_logic.oper_or(y1, y2)
                if r == 'not':
                    y = stack.pop()
                    inference_result = fuzzy_logic.oper_not(y)

                stack.append(inference_result)
                
        # check if the fuzzy set is already in the output variable
        found = 0 
        for index, (name, value) in enumerate(defuzzification_output.fuzzy_sets):
            if name == rule[len(rule) - 1]:
                # old degree of membership for same fuzzy set in the output variable
                old_value = defuzzification_output.fuzzy_sets[index][1]
                # apply or operator on the old value and the new value
                new_tuple = (name, fuzzy_logic.oper_or(old_value, inference_result))
                # update the fuzzy set degree of membership in the output variable
                defuzzification_output.fuzzy_sets[index] = new_tuple
                found = 1
                break
        # add the fuzzy set to the output variable
        if not found:
            defuzzification_output.add_fuzzy_set((rule[len(rule) - 1], inference_result))

    return defuzzification_output


def infix_to_postfix(rule, in_variables):
    precedence = {"not": 2, "and": 1, "or": 0}
    stack = []
    postfix = ""
    i = 0
    while i < len(rule) - 3:
        if rule[i] not in ["and", "or", "and_not", "or_not", "not"]:
            # get the degree of membership of the fuzzy set in the inference variable
            #                                fuzzy variable name, value of the fuzzy set
            y = find_inference_variable(in_variables, rule[i], rule[i + 1])
            postfix += str(y) + " "
            i += 2
        else:
            # handle the precedence of the operators in input format (and_not, or_not)
            if rule[i] == "and_not" or rule[i] == "or_not":
                splitting = rule[i].split('_')
                for split in splitting:
                    while len(stack) != 0 and precedence[split] <= precedence[stack[len(stack) - 1]]:
                        postfix += stack[len(stack) - 1] + " "
                        stack.pop()
                    if len(stack) == 0:
                        stack.append(split)
                    elif precedence[split] > precedence[stack[len(stack) - 1]]:
                        stack.append(split)
            # handle the precedence of the operators in normal cases
            else:
                while len(stack) != 0 and precedence[rule[i]] <= precedence[stack[len(stack) - 1]]:
                    postfix += stack[len(stack) - 1] + " "
                    stack.pop()
                if len(stack) == 0:
                    stack.append(rule[i])
                elif precedence[rule[i]] > precedence[stack[len(stack) - 1]]:
                    stack.append(rule[i])
            i += 1
            
    # add the remaining operators in the stack to the postfix rule
    while len(stack) != 0:
        postfix += stack[len(stack) - 1] + " "
        stack.pop()
    return postfix

# get the centroid of all fuzzy sets in the output variable to use in weighted average
def get_all_output_centroid(defuzzification_output, fuzzy_variables):
    set_centroid = []
    # note the fuzzy set has value of the centroid already calculated
    for fuzzy_variable in fuzzy_variables:
        if defuzzification_output.variable_name == fuzzy_variable.name:
            for fuzzy_set in fuzzy_variable.fuzzy_sets:
                set_centroid.append((fuzzy_set.set_name, fuzzy_set.centroid))
    return set_centroid

# calculate the weighted average based on the centroid of each fuzzy set
def weighted_average(defuzzification_output, set_centroid):
    sum = 0
    sum_y = 0
    for output in defuzzification_output.fuzzy_sets:
        for centroid in set_centroid:
            if output[0] == centroid[0]:
                sum += output[1] * centroid[1]
                sum_y += output[1]
                break
    return sum / sum_y

# defuzzification process and calculate the crisp value
def defuzzification(defuzzification_output, fuzzy_variables):
    set_centroid = get_all_output_centroid(defuzzification_output, fuzzy_variables)
    return weighted_average(defuzzification_output, set_centroid)

# get the fuzzy set name based on the crisp value
def crisp_range_set(value, in_variable, fuzzy_variables):
    max = 0
    setName = ''
    fuzzySet = []
    # get the output variable
    for fuzzy_variable in fuzzy_variables:
        if fuzzy_variable.type == 'OUT':
            fuzzySet = fuzzy_variable.fuzzy_sets
            break
    # get the range of the crisp value
    ranges = point_range(value, fuzzySet)
    
    # get the fuzzy set name
    for var in in_variable.fuzzy_sets:
        for key in ranges.keys():
            if var[0] == key:
                # get the fuzzy set with the maximum membership degree
                if max < var[1]:
                    max = var[1]
                    setName = var[0]
                break
    return setName

# fuzzy system
def fuzzy_system(crisp_variable, fuzzy_variables, statement_rules):
    in_variables = fuzzification(crisp_variable, fuzzy_variables)
    defuzzification_output = inference(statement_rules, in_variables)
    value = defuzzification(defuzzification_output, fuzzy_variables)
    setName = crisp_range_set(value, defuzzification_output, fuzzy_variables)
    return value, setName


def main_menu():
    print('Main Menu:')
    print('==========')
    print('1- Add variables.')
    print('2- Add fuzzy sets to an existing variable.')
    print('3- Add rules.')
    print('4- Run the simulation on crisp values.')
    print('5- Close.')

# add fuzzy variables to the fuzzy variables list
def add_variable():
    print('Enter the variable’s name, type (IN/OUT) and range ([lower, upper]):')
    print('(Press x to finish)')
    variable = input()
    while variable != 'x':
        split_fuzzy = variable.replace("[", "").replace("]", "").replace(",", "")
        split_fuzzy = split_fuzzy.split()
        fuzzy_variables.append(split_fuzzy)
        variable = input()

# add sets to the fuzzy variables in the fuzzy variables list
def add_set():
    print('Enter the variable’s name:')
    print('--------------------------')
    variable_name = input()
    f_variable = FuzzyVariable('', '', [], [])
    print('Enter the fuzzy set name, type (TRI/TRAP) and values: (Press x to finish)')
    print('----------------------------------------------------')
    for fuzzy_variable in fuzzy_variables:
        if fuzzy_variable[0] == variable_name:
            f_variable = FuzzyVariable(fuzzy_variable[0], fuzzy_variable[1],
                                       [int(fuzzy_variable[2]), int(fuzzy_variable[3])], [])

    set = input()
    while set != 'x':
        set = set.split()
        if set[1] == 'TRI':
            fuzzy_set = FuzzySet(set[0], set[1], [int(set[2]), int(set[3]), int(set[4])])
        else:
            fuzzy_set = FuzzySet(set[0], set[1], [int(set[2]), int(set[3]), int(set[4]), int(set[5])])
        # calculate the centroid of the fuzzy set to use in the defuzzification process
        fuzzy_set.calc_centroid()
        f_variable.add_fuzzy_set(fuzzy_set)
        set = input()
    fuzzy.append(f_variable)

# add rules to the fuzzy rules list
def add_rules():
    print('Enter the rules in this format: (Press x to finish)')
    print('IN_variable set operator IN_variable set => OUT_variable set')
    print('------------------------------------------------------------')
    rule = input()
    while rule != 'x':
        statement_rules.append(rule)
        rule = input()

# get the crisp values from the user
def add_crisp_variables():
    for fuzzy_variable in fuzzy:
        if fuzzy_variable.type == 'IN':
            print(f'{fuzzy_variable.name}: ', end="")
            crisp_x = float(input())
            crisp_variables.append((fuzzy_variable.name, crisp_x))


def main():
    while True:
        print('Fuzzy Logic Toolbox')
        print('===================')
        print('1- Create a new fuzzy system')
        print('2- Quit')
        choice = int(input())
        if choice == 1:
            cnt1 = 0
            cnt2 = 0
            cnt3 = 0
            while True:
                main_menu()
                choice_menu = int(input())
                if choice_menu == 1:
                    add_variable()
                    cnt1 += 1
                elif choice_menu == 2:
                    if cnt1 == 0:
                        print('CAN’T ADD FUZZY SET! Please add the fuzzy variables first.')
                        continue
                    add_set()
                    cnt2 += 1
                elif choice_menu == 3:
                    if cnt1 == 0:
                        print('CAN’T ADD RULES! Please add the fuzzy variables and the fuzzy sets first.')
                        continue
                    elif cnt2 == 0:
                        print('CAN’T ADD RULES! Please add the fuzzy sets')
                        continue
                    add_rules()
                    cnt3 += 1
                elif choice_menu == 4:
                    if cnt1 == 0:
                        print(
                            'CAN’T START THE SIMULATION! Please add the fuzzy variables and fuzzy sets and rules first.')
                        continue
                    elif cnt2 == 0:
                        print('CAN’T START THE SIMULATION! Please add the fuzzy sets and rules first.')
                        continue
                    elif cnt3 == 0:
                        print('CAN’T START THE SIMULATION! rules first.')
                        continue
                    add_crisp_variables()
                    print(fuzzy_system(crisp_variables, fuzzy, statement_rules))
                elif choice_menu == 5:
                    break
        elif choice == 2:
            break


if __name__ == "__main__":
    main()
# without Menu input
# add_variable()
# add_set()
# add_set()
# add_set()
# add_rules()
# add_crisp_variables()
# print(fuzzy_system(crisp_variables, fuzzy, statement_rules))
# # input = "dirt small and_not fabric_type soft or dirt medium and fabric_type soft => time small"
# input2 = "2 or 3 and 4  or 6 and_not 5"
# input2 = input2.split()
# input = input.split()
# print(infix_to_postfix(input, []))
