package ua.pp.condor.searchengine.index;

import static java.util.Objects.requireNonNull;

class DocWithScore implements Comparable<DocWithScore> {

    private final Integer id;
    private final double score;

    DocWithScore(Integer id, double score) {
        requireNonNull(id);
        this.id = id;
        this.score = score;
    }

    public Integer getId() {
        return id;
    }

    @Override
    public int compareTo(DocWithScore o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return '{' +
                "id=" + id +
                ", score=" + score +
                '}';
    }
}
