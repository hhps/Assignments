package ua.pp.condor.gametheory.game.player;

class OpponentMoves {

    private int count;
    private int cooperationCount;
    private boolean penultimate;
    private boolean last;

    boolean isEmpty() {
        return count == 0;
    }

    int count() {
        return count;
    }

    int cooperationCount() {
        return cooperationCount;
    }

    void clear() {
        count = 0;
        cooperationCount = 0;
    }

    void add(boolean move) {
        count++;
        if (move) {
            cooperationCount++;
        }
        penultimate = last;
        last = move;
    }

    boolean last() {
        if (isEmpty()) {
            throw new IllegalStateException("Empty history");
        }
        return last;
    }

    boolean penultimate() {
        if (count < 2) {
            throw new IllegalStateException("Not enough elements in history");
        }
        return penultimate;
    }

    boolean isCooperator() {
        return cooperationCount > count >>> 1;
    }
}
