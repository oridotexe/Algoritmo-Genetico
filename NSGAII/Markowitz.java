package NSGAII;

public class Markowitz {
    private double[] returns;
    private double[][] covMatrix;

    public Markowitz(double[] returns, double[][] covMatrix) {
        this.returns = returns;
        this.covMatrix = covMatrix;
    }

    public double[] fitness(double[] weights) {
        double portfolioReturn = 0.0;
        double portfolioVariance = 0.0;

        for (int i = 0; i < weights.length; i++) {
            portfolioReturn += weights[i] * returns[i];
            for (int j = 0; j < weights.length; j++) {
                portfolioVariance += weights[i] * weights[j] * covMatrix[i][j];
            }
        }

        return new double[] { -portfolioReturn, portfolioVariance };
    }

    public int getNumAssets() {
        return returns.length;
    }

    public boolean isFeasible(double[] weights) {
        double sum = 0.0;
        for (double weight : weights) {
            if (weight < 0 || weight > 1) {
                return false;
            }
            sum += weight;
        }
        return Math.abs(sum - 1.0) < 1e-6;
    }
}
