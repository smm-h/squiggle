package ir.smmh.math.matrix;

interface Array2D {

    // T get(int i, int j); void set(int i, int j, T value);

    final class Int implements Array2D {
        final int[][] array;

        Int(int rows, int columns) {
            array = new int[rows][columns];
        }
    }

    final class Long implements Array2D {
        final long[][] array;

        Long(int rows, int columns) {
            array = new long[rows][columns];
        }
    }

    final class Double {
        final double[][] array;

        Double(int rows, int columns) {
            array = new double[rows][columns];
        }
    }

    final class Float {
        final float[][] array;

        Float(int rows, int columns) {
            array = new float[rows][columns];
        }
    }

    final class Boolean {
        final boolean[][] array;

        Boolean(int rows, int columns) {
            array = new boolean[rows][columns];
        }
    }
}