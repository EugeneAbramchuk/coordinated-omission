package main.java;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {
        List<Long> latencies = generateLoad();
        analyzeData(latencies);
    }

    private static List<Long> generateLoad() {
        List<Long> latencies = new ArrayList<>();
        for (int i = 0; i < 6000; i++) {
            long latency = ThreadLocalRandom.current().nextInt(1, 5);
            if (ThreadLocalRandom.current().nextFloat() > 0.99) {
                latency = 100;
            }
            latencies.add(latency * 1000000);
        }
        return latencies;
    }

    private static void analyzeData(List<Long> latencies) {
        if (latencies.size() > 0) {
            Collections.sort(latencies);
            LongSummaryStatistics stats = latencies.stream().mapToLong(v -> v).summaryStatistics();
            double sqrDiffsToMean = 0.0;
            for (Long latency : latencies) {
                sqrDiffsToMean += Math.pow(latency - stats.getAverage(), 2);
            }
            double stdDeviation = Math.sqrt(sqrDiffsToMean / (double) latencies.size());
            double pct25 = getPercentile(25, latencies);
            double pct50 = getPercentile(50, latencies);
            double pct75 = getPercentile(75, latencies);
            double pct90 = getPercentile(90, latencies);
            double pct95 = getPercentile(95, latencies);
            double pct99 = getPercentile(99, latencies);
            double pct999 = getPercentile(99.9, latencies);
            double pct9999 = getPercentile(99.99, latencies);

            StringBuilder sb = new StringBuilder(2000);
            sb.append("+================+======================+\n");
            sb.append("| Metric name    | Result set 1         |\n");
            sb.append("+================+======================+\n");
            sb.append(String.format("| Sample size    | %20s |\n", stats.getCount()));
            sb.append(String.format("| Min, ms        | %20s |\n", stats.getMin() / 1000000.0));
            sb.append(String.format("| Max, ms        | %20s |\n", stats.getMax() / 1000000.0));
            sb.append(String.format("| Mean, ms       | %20s |\n", stats.getAverage() / 1000000.0));
            sb.append(String.format("| Std.dev, ms    | %20s |\n", stdDeviation / 1000000.0));
            sb.append(String.format("| 25%%-ile, ms    | %20s |\n", pct25 / 1000000.0));
            sb.append(String.format("| 50%%-ile, ms    | %20s |\n", pct50 / 1000000.0));
            sb.append(String.format("| 75%%-ile, ms    | %20s |\n", pct75 / 1000000.0));
            sb.append(String.format("| 90%%-ile, ms    | %20s |\n", pct90 / 1000000.0));
            sb.append(String.format("| 95%%-ile, ms    | %20s |\n", pct95 / 1000000.0));
            sb.append(String.format("| 99%%-ile, ms    | %20s |\n", pct99 / 1000000.0));
            sb.append(String.format("| 99.9%%-ile, ms  | %20s |\n", pct999 / 1000000.0));
            sb.append(String.format("| 99.99%%-ile, ms | %20s |\n", pct9999 / 1000000.0));
            sb.append("+================+======================+\n");
            System.out.println(sb.toString());
        }

    }

    private static double getPercentile(double percentile, List<Long> latencies) {
        if (percentile <= 0 || percentile > 100) {
            System.out.println("Error: the percentile must be a valid number > 0 and <= 100.");
            System.exit(1);
        }
        return latencies.get((int) Math.round((percentile / 100.0) * (latencies.size() - 1)));
    }
}
