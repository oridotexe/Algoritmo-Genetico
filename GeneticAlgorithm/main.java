package GeneticAlgorithm;

import java.util.Arrays;
import java.util.List;

public class main {
    public static void main(String[] args) {
        double[] returns = { 0.00429493, 0.02689857, 0.00827647, 0.00794438 };
        double[][] covMatrix = {
                { 0.00671900, 0.01193778, 0.00170523, 0.00161020 },
                { 0.01193778, 0.03438852, 0.00402569, 0.00375060 },
                { 0.00170523, 0.00402569, 0.00344421, 0.00185332 },
                { 0.00161020, 0.00375060, 0.00185332, 0.00233944 }
        };

        MarkowitzProblem problem = new MarkowitzProblem(returns, covMatrix);

        GeneticAlgorithm ga = new GeneticAlgorithm(100, 1000, 0.01, 0.9, problem);
        List<double[]> solutions = ga.optimize();

        System.out.println("Soluciones:");
        for (double[] solution : solutions) {
            System.out.println(Arrays.toString(solution));
        }

        System.out.println("Coeficiente de variacion para cada solucion:");
        for (double[] solution : solutions) {
            double[] fitness = problem.fitness(solution);
            double cv = fitness[0] / fitness[1];
            System.out.println("Pesos: " + Arrays.toString(solution) + ", CV: " + cv);
        }
    }
}
