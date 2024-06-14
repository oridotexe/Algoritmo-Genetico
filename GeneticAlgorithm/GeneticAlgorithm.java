package GeneticAlgorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

class GeneticAlgorithm {
    private int populationSize;
    private int maxGenerations; 
    private double mutationRate;
    private double crossoverRate; 
    private MarkowitzProblem problem; 
    private Random random; 

    public GeneticAlgorithm(int populationSize, int maxGenerations, double mutationRate, double crossoverRate,
            MarkowitzProblem problem) {
        this.populationSize = populationSize;
        this.maxGenerations = maxGenerations;
        this.mutationRate = mutationRate;
        this.crossoverRate = crossoverRate;
        this.problem = problem;
        this.random = new Random();
    }

    public List<double[]> optimize() {
        List<double[]> population = initializePopulation();
        for (int generation = 0; generation < maxGenerations; generation++) {
            List<double[]> offspring = createOffspring(population);
            population.addAll(offspring);
            population = selectNextGeneration(population);
        }
        return population;
    }

    private List<double[]> initializePopulation() {
        List<double[]> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) {
            double[] weights = new double[problem.getNumAssets()];
            double sum = 0.0;
            for (int j = 0; j < weights.length; j++) {
                weights[j] = random.nextDouble();
                sum += weights[j];
            }
            for (int j = 0; j < weights.length; j++) {
                weights[j] /= sum;
            }
            population.add(weights);
        }
        return population;
    }

    private List<double[]> createOffspring(List<double[]> population) {
        List<double[]> offspring = new ArrayList<>();
        for (int i = 0; i < populationSize; i += 2) {
            double[] parent1 = population.get(random.nextInt(populationSize));
            double[] parent2 = population.get(random.nextInt(populationSize));
            double[][] children = crossover(parent1, parent2);
            offspring.add(mutate(children[0]));
            offspring.add(mutate(children[1]));
        }
        return offspring;
    }

    private double[][] crossover(double[] parent1, double[] parent2) {
        double[] child1 = new double[parent1.length];
        double[] child2 = new double[parent2.length];
        if (random.nextDouble() < crossoverRate) {
            int crossoverPoint = random.nextInt(parent1.length);
            for (int i = 0; i < crossoverPoint; i++) {
                child1[i] = parent1[i];
                child2[i] = parent2[i];
            }
            for (int i = crossoverPoint; i < parent1.length; i++) {
                child1[i] = parent2[i];
                child2[i] = parent1[i];
            }
        } else {
            System.arraycopy(parent1, 0, child1, 0, parent1.length);
            System.arraycopy(parent2, 0, child2, 0, parent2.length);
        }
        return new double[][] { child1, child2 };
    }

    private double[] mutate(double[] individual) {
        for (int i = 0; i < individual.length; i++) {
            if (random.nextDouble() < mutationRate) {
                individual[i] = random.nextDouble();
            }
        }
        double sum = 0.0;
        for (double gene : individual) {
            sum += gene;
        }
        for (int i = 0; i < individual.length; i++) {
            individual[i] /= sum;
        }
        return individual;
    }

    private List<double[]> selectNextGeneration(List<double[]> population) {
        Collections.sort(population, (a, b) -> Double.compare(fitnessScore(b), fitnessScore(a)));
        return new ArrayList<>(population.subList(0, populationSize));
    }

    private double fitnessScore(double[] individual) {
        double[] fitness = problem.fitness(individual);
        return fitness[0] / fitness[1];
    }
}
