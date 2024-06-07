package NSGAII;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class NSGAII {
    private int populationSize;
    private int maxGenerations;
    private double mutationRate;
    private double crossoverRate;
    private Markowitz problem;
    private Random random;

    public NSGAII(int populationSize, int maxGenerations, double mutationRate, double crossoverRate,
            Markowitz problem) {
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
        List<double[]> nextGeneration = new ArrayList<>();
        List<List<double[]>> fronts = fastNonDominatedSort(population);
        int i = 0;
        while (nextGeneration.size() + fronts.get(i).size() <= populationSize) {
            nextGeneration.addAll(fronts.get(i));
            i++;
        }
        List<double[]> remainingFront = fronts.get(i);
        Collections.sort(remainingFront,
                (a, b) -> Double.compare(crowdingDistance(a, fronts), crowdingDistance(b, fronts)));
        nextGeneration.addAll(remainingFront.subList(0, populationSize - nextGeneration.size()));
        return nextGeneration;
    }

    private List<List<double[]>> fastNonDominatedSort(List<double[]> population) {
        List<List<double[]>> fronts = new ArrayList<>();
        List<double[]> front = new ArrayList<>();
        for (double[] individual : population) {
            boolean dominated = false;
            for (double[] other : population) {
                if (dominates(other, individual)) {
                    dominated = true;
                    break;
                }
            }
            if (!dominated) {
                front.add(individual);
            }
        }
        fronts.add(front);
        List<double[]> remaining = new ArrayList<>(population);
        remaining.removeAll(front);
        while (!remaining.isEmpty()) {
            List<double[]> nextFront = new ArrayList<>();
            for (double[] individual : remaining) {
                boolean dominated = false;
                for (double[] other : remaining) {
                    if (dominates(other, individual)) {
                        dominated = true;
                        break;
                    }
                }
                if (!dominated) {
                    nextFront.add(individual);
                }
            }
            fronts.add(nextFront);
            remaining.removeAll(nextFront);
        }
        return fronts;
    }

    private boolean dominates(double[] a, double[] b) {
        double[] fitnessA = problem.fitness(a);
        double[] fitnessB = problem.fitness(b);
        return fitnessA[0] <= fitnessB[0] && fitnessA[1] <= fitnessB[1]
                && (fitnessA[0] < fitnessB[0] || fitnessA[1] < fitnessB[1]);
    }

    private double crowdingDistance(double[] individual, List<List<double[]>> fronts) {
        double distance = 0.0;
        for (List<double[]> front : fronts) {
            double maxFitness0 = Double.NEGATIVE_INFINITY;
            double minFitness0 = Double.POSITIVE_INFINITY;
            double maxFitness1 = Double.NEGATIVE_INFINITY;
            double minFitness1 = Double.POSITIVE_INFINITY;
            for (double[] other : front) {
                double[] fitness = problem.fitness(other);
                if (fitness[0] > maxFitness0)
                    maxFitness0 = fitness[0];
                if (fitness[0] < minFitness0)
                    minFitness0 = fitness[0];
                if (fitness[1] > maxFitness1)
                    maxFitness1 = fitness[1];
                if (fitness[1] < minFitness1)
                    minFitness1 = fitness[1];
            }
            double[] fitness = problem.fitness(individual);
            distance += (fitness[0] - minFitness0) / (maxFitness0 - minFitness0)
                    + (fitness[1] - minFitness1) / (maxFitness1 - minFitness1);
        }
        return distance;
    }
}
