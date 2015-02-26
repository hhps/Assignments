package ua.pp.condor.searchengine.index;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ua.pp.condor.searchengine.dto.Document;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SearchService implements ISearchService {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    /**
     * A special class to store and updates of average length of all documents in index.
     */
    private static class AverageDouble {

        private double value;

        public double value() {
            return value;
        }

        public void update(int currentCount, int newValue) {
            log.debug("Average doc length before update: {}", value);
            double fullValue = value * currentCount + newValue;
            value = fullValue / (currentCount + 1);
            log.debug("Average doc length after update: {}", value);
        }
    }

    /**
     * Index for full text search by documents.
     */
    private final ConcurrentMap<String, Map<Integer, Integer>> index = new ConcurrentHashMap<>();

    /**
     * Contains length of each document in index.
     */
    private final Map<Integer, Integer> docsWithLength = new HashMap<>();

    /**
     * Contains an average length of all documents in index.
     */
    private final AverageDouble avgDocLength = new AverageDouble();

    /**
     * An {@link org.apache.lucene.analysis.Analyzer} for English text which consist of:
     * {@link org.apache.lucene.analysis.standard.StandardTokenizer},
     * {@link org.apache.lucene.analysis.standard.StandardFilter},
     * {@link org.apache.lucene.analysis.en.EnglishPossessiveFilter},
     * {@link org.apache.lucene.analysis.core.LowerCaseFilter},
     * {@link org.apache.lucene.analysis.core.StopFilter},
     * {@link org.apache.lucene.analysis.en.PorterStemFilter} and
     * {@link org.apache.lucene.analysis.Analyzer.TokenStreamComponents}.
     */
    private static final Analyzer ANALYZER = new EnglishAnalyzer();

    /**
     * Constants for {@link #bm25Plus} function.
     */
    private static final int K1 = 2;
    private static final int K1_PLUS_ONE = K1 + 1;
    private static final double B = 0.75;
    private static final double ONE_MINUS_B = 1 - B;
    private static final int DELTA = 1;

    /**
     * A dummy object for locking.
     */
    private final Object lock = new Object();

    /**
     * Complexity: O(1)
     */
    @Override
    public boolean isEmpty() {
        return docsWithLength.isEmpty();
    }

    /**
     * Complexity: O(N) + O(N) + O(uN) = O(N)
     *             N - number of words in the document.
     *             uN - unique number of words in the document, uN <= N.
     */
    @Override
    public void addDocument(Document doc) {
        if (StringUtils.isBlank(doc.getText())) {
            return;
        }

        final int docId = doc.getId();
        List<String> words;
        synchronized (lock) {
            if (docsWithLength.containsKey(docId)) {
                log.info("Current document is already in index");
                return;
            }
            words = getAllWords(String.valueOf(doc.getId()), doc.getText());
            final int docLength = words.size();
            log.debug("Current doc length: {}", docLength);
            avgDocLength.update(docsWithLength.size(), docLength);
            docsWithLength.put(docId, docLength);
        }

        Map<String, MutableInt> frequencyOfEachWord = calculateFrequency(words);
        fillIndex(docId, frequencyOfEachWord);

        log.info("Current document successfully added to index");
    }

    /**
     * Complexity: O(N)
     *             N - number of words in {@code text}.
     * @see #fillWords
     */
    private static List<String> getAllWords(final String fieldName, final String text) {
        List<String> words = new LinkedList<>();
        fillWords(words, fieldName, text);
        return words;
    }

    /**
     * Complexity: O(N)
     *             N - number of words in {@code text}.
     * @see #fillWords
     */
    private static Set<String> getUniqueWords(final String fieldName, final String text) {
        Set<String> words = new HashSet<>();
        fillWords(words, fieldName, text);
        return words;
    }

    /**
     * Complexity: O(N)
     *             N - number of words in {@code text}.
     */
    private static void fillWords(Collection<String> collection, final String fieldName, final String text) {
        try {
            TokenStream tokenStream = ANALYZER.tokenStream(fieldName, text);
            CharTermAttribute term = tokenStream.addAttribute(CharTermAttribute.class);
            tokenStream.reset();
            StringBuilder sb = log.isDebugEnabled() ? new StringBuilder(text.length()) : null;
            while(tokenStream.incrementToken()) {
                boolean isAdded = collection.add(term.toString());
                if (sb != null && isAdded) {
                    sb.append('[').append(term).append("] ");
                }
            }
            log.debug("Found {} words: {}", collection.size(), sb);
        } catch (IOException e) {
            log.error("Can not extract words from text of {}", fieldName, e);
        }
    }

    /**
     * Complexity: O(N)
     *             N - size of {@code words}.
     */
    private static Map<String, MutableInt> calculateFrequency(List<String> words) {
        Map<String, MutableInt> map = new HashMap<>();
        for (String word : words) {
            MutableInt frequency = map.get(word);
            if (frequency == null) {
                map.put(word, new MutableInt(1));
            } else {
                frequency.increment();
            }
        }
        log.debug("Words frequency: {}", map);
        return map;
    }

    /**
     * Complexity: O(uN)
     *             uN - size of {@code frequencyOfEachWord}.
     */
    private void fillIndex(final int docId, Map<String, MutableInt> frequencyOfEachWord) {
        for (Map.Entry<String, MutableInt> entry : frequencyOfEachWord.entrySet()) {
            final String word = entry.getKey();
            final int frequency = entry.getValue().intValue();

            Map<Integer, Integer> currentDocs = index.get(word);
            if (currentDocs == null) {
                currentDocs = new ConcurrentHashMap<>();
                Map<Integer, Integer> previous = index.putIfAbsent(word, currentDocs);
                if (previous != null) {
                    currentDocs = previous;
                }
            }
            currentDocs.put(docId, frequency);
        }
    }

    /**
     * Complexity: O(N) + O(uN * minDCS)/O(m) + O(M * uN) + O(M logM) + O(count)
     *             N - number of words in a query.
     *             uN - number of unique in a query.
     *             minDCS - size of minimum collection of documents for any of query words.
     *             m - number of documents satisfying for any word in a query.
     *             M - number of documents satisfying a whole query.
     *
     * Complexity for {@link LogicType#and}: O(uN * minDCS) + O(M * uN) + O(M logM)
     * Complexity for {@link LogicType#or}:  O(m) + O(M * uN) + O(M logM)
     */
    @Override
    public Set<Integer> search(String query, LogicType logic, int count) {
        Set<String> queryWords = getUniqueWords("query", query);
        if (queryWords.isEmpty()) {
            return Collections.emptySet();
        }

        Set<Integer> foundDocs;
        switch (logic) {
            case and:
                foundDocs = searchAnd(queryWords);
                break;
            case or:
                foundDocs = searchOr(queryWords);
                break;
            default:
                throw new IllegalStateException("Unknown logic type: " + logic);
        }
        log.debug("Found {} docs: {}", foundDocs.size(), foundDocs);
        if (foundDocs.isEmpty()) {
            return Collections.emptySet();
        }
        DocWithScore[] sortedByScore = sortByScore(queryWords, foundDocs);
        return getTop(sortedByScore, count);
    }

    /**
     * Complexity: O(uN) + O(uN * minDCS) = O(uN * minDCS), uN << minDCS
     *             uN - size of {@code queryWords}.
     *             minDCS - size of minimum collection of documents for any of query words.
     *
     * uN * minDCS <= m
     *             m - number of documents satisfying for any word in a query.
     */
    private Set<Integer> searchAnd(Set<String> queryWords) {
        Map<Integer, Integer> minDocsCollection = null;
        for (String q : queryWords) {
            Map<Integer, Integer> docsForQ = index.get(q);
            if (docsForQ == null) {
                return Collections.emptySet();  // there is no document which contains all query words
            }
            if (minDocsCollection == null || minDocsCollection.size() > docsForQ.size()) {
                minDocsCollection = docsForQ;
            }
        }
        assert minDocsCollection != null;
        if (queryWords.size() == 1) {
            return minDocsCollection.keySet();
        }
        Set<Integer> result = null;
        for (Integer docId : minDocsCollection.keySet()) {
            boolean containsAllWords = true;
            for (String q : queryWords) {
                Map<Integer, Integer> docsForQ = index.get(q);
                if (docsForQ == minDocsCollection) {
                    continue;
                }
                if (!docsForQ.containsKey(docId)) {
                    containsAllWords = false;
                    break;
                }
            }
            if (containsAllWords) {
                if (result == null) {
                    result = new HashSet<>();
                }
                result.add(docId);
            }
        }
        if (result != null) {
            return result;
        }
        return Collections.emptySet();
    }

    /**
     * Complexity: O(uN) + O(m) = O(m), uN << m
     *             uN - size of {@code queryWords}.
     *             m - number of documents satisfying for any word in a query.
     */
    private Set<Integer> searchOr(Set<String> queryWords) {
        Map<Integer, Integer> maxDocsCollection = Collections.emptyMap();
        for (String q : queryWords) {
            Map<Integer, Integer> docsForQ = index.get(q);
            if (docsForQ == null) {
                continue;
            }
            if (maxDocsCollection.size() < docsForQ.size()) {
                maxDocsCollection = docsForQ;
            }
        }
        Set<Integer> result = new HashSet<>(maxDocsCollection.keySet());
        if (queryWords.size() > 1) {
            for (String q : queryWords) {
                Map<Integer, Integer> docsForQ = index.get(q);
                if (docsForQ == null || docsForQ == maxDocsCollection) {
                    continue;
                }
                result.addAll(docsForQ.keySet());
            }
        }
        return result;
    }

    /**
     * Complexity: O(M * uN) + O(M logM)
     *             M - number of documents satisfying a whole query.
     *             uN - size of {@code queryWords}.
     */
    private DocWithScore[] sortByScore(Set<String> queryWords, Set<Integer> foundDocs) {
        DocWithScore[] sortedByScore = new DocWithScore[foundDocs.size()];
        int i = 0;
        for (Integer docId : foundDocs) {
            final double score = bm25Plus(docId, queryWords);
            sortedByScore[i++] = new DocWithScore(docId, score);
        }
        Arrays.sort(sortedByScore, Collections.reverseOrder()); // desc order
        log.debug("Sorted by score docs: {}", sortedByScore);
        return sortedByScore;
    }

    /**
     * Calculates rank of specified document by BM25+ function.
     *
     * <a href="http://en.wikipedia.org/wiki/Okapi_BM25">BM25+</a> is an extension of BM25. BM25+ was developed to
     * address one deficiency of the standard BM25 in which the component of term frequency normalization by document
     * length is not properly lower-bounded; as a result of this deficiency, long documents which do match the query
     * term can often be scored unfairly by BM25 as having a similar relevancy to shorter documents that do not contain
     * the query term at all.
     * The scoring formula of BM25+ only has one additional free parameter \delta (a default value is 1.0 in
     * absence of a training data) as compared with BM25.
     * 
     * Complexity: O(uN)
     *             uN - size of {@code queryWords}.
     */
    private double bm25Plus(Integer docId, Set<String> queryWords) {
        final int docLength = docsWithLength.get(docId);
        final int numberOfDocs = docsWithLength.size();
        final double avgDocLength = this.avgDocLength.value();

        double score = 0;
        for (String q : queryWords) {
            Map<Integer, Integer> docsForQ = index.get(q);
            if (docsForQ == null) {
                continue;
            }
            final Integer frequency = docsForQ.get(docId);
            if (frequency == null) {
                continue;
            }
            final int fqD = frequency;
            final int nq = docsForQ.size();
            score += idf(numberOfDocs, nq) * ((fqD * K1_PLUS_ONE) /
                    (fqD + K1 * (ONE_MINUS_B + B * docLength / avgDocLength)) + DELTA);
        }
        return score;
    }

    /**
     * Calculates inverse document frequency.
     * Be careful! This function returns {@code 0} instead of negative value of logarithm. For more information about
     * this trick see {@link #bm25Plus}.
     * 
     * Complexity: O(1)
     *
     * @param n  is the total number of documents in the collection.
     * @param nq is the number of documents containing word {@code q}.
     */
    private static double idf(int n, int nq) {
        double result = Math.log((n - nq + 0.5) / (nq + 0.5));
        return result > 0 ? result : 0;
    }

    /**
     * Complexity: O(count)
     */
    private static Set<Integer> getTop(DocWithScore[] sortedByScore, int count) {
        final int minCount = Math.min(count, sortedByScore.length);
        Set<Integer> result = new LinkedHashSet<>(minCount);
        for (int i = 0; i < minCount; i++) {
            result.add(sortedByScore[i].getId());
        }
        log.info("Top {} docs: {}", count, result);
        return result;
    }
}
