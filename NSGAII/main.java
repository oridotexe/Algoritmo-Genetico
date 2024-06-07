package NSGAII;

import java.util.List;
import java.util.Arrays;

public class main {
    public static void main(String[] args) {
        double[] returns = { 0.00429493, 0.02689857, 0.00827647, 0.00794438 };
        double[][] covMatrix = {
                { 0.00671900, 0.01193778, 0.00170523, 0.00161020 },
                { 0.01193778, 0.03438852, 0.00402569, 0.00375060 },
                { 0.00170523, 0.00402569, 0.00344421, 0.00185332 },
                { 0.00161020, 0.00375060, 0.00185332, 0.00233944 }
        };

        Markowitz problem = new Markowitz(returns, covMatrix);
        NSGAII nsgaII = new NSGAII(100, 1000, 0.01, 0.9, problem);
        List<double[]> paretoFront = nsgaII.optimize();

        System.out.println("Pareto de las soluciones:");
        for (double[] solution : paretoFront) {
            System.out.println(Arrays.toString(solution));
        }

        System.out.println("Coeficiente de variaciones para cada solucion:");
        for (double[] solution : paretoFront) {
            double[] fitness = problem.fitness(solution);
            double cv = -fitness[0] / fitness[1];
            System.out.println("Peso: " + Arrays.toString(solution) + ", CV: " + cv);
        }
    }
}
