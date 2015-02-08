package ua.pp.condor;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

/**
 * We have a set of N points (all points have integer coordinates).
 * Find a minimum distance between points of this set.
 *
 * Input:
 * 3 - number of points
 * 10 10
 * 20 10
 * 20 15
 *
 * Output:
 * 5
 */
public final class MinimumDistance {

    public static class Point2DC extends Point2D.Double implements Comparable<Point2D> {

        private static final long serialVersionUID = 7060615945279161393L;

        public Point2DC(double x, double y) {
            super(x, y);
        }

        @Override
        public int compareTo(Point2D o) {
            if (x < o.getX()) return -1;
            if (x > o.getX()) return 1;
            if (y < o.getY()) return -1;
            if (y > o.getY()) return 1;
            return 0;
        }
    }

    public static final Comparator<Point2DC> COMPARATOR_BY_X = new Comparator<Point2DC>() {

        @Override
        public int compare(Point2DC p1, Point2DC p2) {
            if (p1.getX() < p2.getX()) return -1;
            if (p1.getX() > p2.getX()) return 1;
            return 0;
        }
    };

    private MinimumDistance() {}

    public static double minimumDistance(Point2DC[] points) {
        final int length = points.length;
        if (length <= 1) {
            return Double.POSITIVE_INFINITY;
        }

        Arrays.sort(points, COMPARATOR_BY_X);

        for (int i = 0; i < length - 1; i++) {
            if (points[i].equals(points[i + 1])) {
                return 0.0;
            }
        }

        Point2DC[] pointsCopy = new Point2DC[length];
        System.arraycopy(points, 0, pointsCopy, 0, length);

        double closestDistanceSq = closest(points, pointsCopy, new Point2DC[length], 0, length - 1);
        return Math.sqrt(closestDistanceSq);
    }

    private static double closest(Point2DC[] sortedByX, Point2DC[] sortedByY, Point2DC[] tmp, int lo, int hi) {
        if (hi <= lo) {
            return Double.POSITIVE_INFINITY;
        }
        if (hi - lo < 3) {
            double closest = Double.POSITIVE_INFINITY;
            for (int i = lo; i < hi; i++) {
                for (int j = i + 1; j <= hi; j++) {
                    double currentDistance = sortedByY[i].distanceSq(sortedByY[j]);
                    if (currentDistance < closest) {
                        closest = currentDistance;
                    }
                }
            }
            return closest;
        }

        int mid = lo + hi >>> 1;
        Point2D median = sortedByX[mid];

        double left = closest(sortedByX, sortedByY, tmp, lo, mid);
        double right = closest(sortedByX, sortedByY, tmp, mid + 1, hi);
        merge(sortedByY, tmp, lo, mid, hi);

        double closest = Math.min(left, right);
        double closestSqrt = Math.sqrt(closest);

        int k = 0;
        for (int i = lo; i <= hi; i++) {
            if (Math.abs(sortedByY[i].getX() - median.getX()) < closestSqrt) {
                tmp[k++] = sortedByY[i];
            }
        }

        for (int i = 0; i < k; i++) {
            for (int j = i + 1; j < k && tmp[j].getY() - tmp[i].getY() < closestSqrt; j++) {
                double currentDistance = tmp[i].distanceSq(tmp[j]);
                if (currentDistance < closest) {
                    closest = currentDistance;
                }
            }
        }
        return closest;
    }

    private static void merge(Point2DC[] points, Point2DC[] tmp, int lo, int mid, int hi) {
        System.arraycopy(points, lo, tmp, lo, hi - lo + 1);

        int i = lo, j = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) {
                points[k] = tmp[j++];
            } else if (j > hi) {
                points[k] = tmp[i++];
            } else if (tmp[j].compareTo(tmp[i]) < 0) {
                points[k] = tmp[j++];
            } else {
                points[k] = tmp[i++];
            }
        }
    }

    /**
     * Use {@code java MinimumDistance test-filename} for testing.
     */
    public static void main(String[] args) throws Exception {
        PrintWriter pw = new PrintWriter(System.out);
        Scanner sc;
        if (args.length > 0) {
            sc = new Scanner(new File(args[0]));
        } else {
            sc = new Scanner(System.in);
        }
        int n = sc.nextInt();
        Point2DC[] points = new Point2DC[n];
        for (int i = 0; i < n; i++) {
            int x = sc.nextInt(), y = sc.nextInt();
            points[i] = new Point2DC(x, y);
        }
        double result = minimumDistance(points);
        pw.println(new DecimalFormat("#.###").format(result));
        pw.flush();
    }
}
