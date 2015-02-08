SET SCHEMA 'hhschool';

-- Получить список переводов (таблица translation, каждый перевод характеризуется уникальным набором полей:
-- name, lang, site_id), для которых есть русская версия (lang = ‘RU’), но нет украинской (lang = ‘UA’)
-- и флаг ui установлен в true.
SELECT
  name,
  site_id,
  lang
FROM translation t
WHERE ui = TRUE
      AND EXISTS(SELECT *
                 FROM translation t1
                 WHERE t.name = t1.name AND t.site_id = t1.site_id AND t1.lang = 'RU')
      AND NOT EXISTS(SELECT *
                     FROM translation t2
                     WHERE t.name = t2.name AND t.site_id = t2.site_id AND t2.lang = 'UA');

-- EXPLAIN ANALYZE:
-- "Nested Loop Semi Join  (cost=224.66..619.82 rows=254 width=19) (actual time=2.092..4.400 rows=9 loops=1)"
-- "  ->  Hash Anti Join  (cost=224.37..452.60 rows=345 width=19) (actual time=2.065..4.192 rows=15 loops=1)"
-- "        Hash Cond: (((t.name)::text = (t2.name)::text) AND (t.site_id = t2.site_id))"
-- "        ->  Seq Scan on translation t  (cost=0.00..167.72 rows=3497 width=19) (actual time=0.007..1.086 rows=3497 loops=1)"
-- "              Filter: ui"
-- "              Rows Removed by Filter: 4775"
-- "        ->  Hash  (cost=188.40..188.40 rows=2398 width=16) (actual time=1.960..1.960 rows=2398 loops=1)"
-- "              Buckets: 1024  Batches: 1  Memory Usage: 126kB"
-- "              ->  Seq Scan on translation t2  (cost=0.00..188.40 rows=2398 width=16) (actual time=0.005..1.413 rows=2398 loops=1)"
-- "                    Filter: ((lang)::text = 'UA'::text)"
-- "                    Rows Removed by Filter: 5874"
-- "  ->  Index Scan using translation_name_lang_idx on translation t1  (cost=0.29..0.47 rows=1 width=16) (actual time=0.013..0.013 rows=1 loops=15)"
-- "        Index Cond: (((name)::text = (t.name)::text) AND ((lang)::text = 'RU'::text))"
-- "        Filter: (t.site_id = site_id)"
-- "Total runtime: 4.443 ms"



SELECT
  t0.name,
  t0.site_id,
  t0.lang
FROM (SELECT *
      FROM translation) t0
  INNER JOIN (SELECT *
              FROM translation
              WHERE lang = 'RU') t1
    ON (t0.name = t1.name AND t0.site_id = t1.site_id)
  LEFT OUTER JOIN (SELECT *
                   FROM translation
                   WHERE lang = 'UA') t2
    ON (t0.name = t2.name AND t0.site_id = t2.site_id)
WHERE t0.ui = TRUE AND t2.name IS NULL;

-- EXPLAIN ANALYZE:
-- "Nested Loop  (cost=224.66..619.82 rows=253 width=19) (actual time=3.050..6.512 rows=9 loops=1)"
-- "  ->  Hash Anti Join  (cost=224.37..452.60 rows=345 width=19) (actual time=3.003..6.208 rows=15 loops=1)"
-- "        Hash Cond: (((translation.name)::text = (translation_2.name)::text) AND (translation.site_id = translation_2.site_id))"
-- "        ->  Seq Scan on translation  (cost=0.00..167.72 rows=3497 width=19) (actual time=0.009..1.613 rows=3497 loops=1)"
-- "              Filter: ui"
-- "              Rows Removed by Filter: 4775"
-- "        ->  Hash  (cost=188.40..188.40 rows=2398 width=16) (actual time=2.854..2.854 rows=2398 loops=1)"
-- "              Buckets: 1024  Batches: 1  Memory Usage: 126kB"
-- "              ->  Seq Scan on translation translation_2  (cost=0.00..188.40 rows=2398 width=16) (actual time=0.007..2.012 rows=2398 loops=1)"
-- "                    Filter: ((lang)::text = 'UA'::text)"
-- "                    Rows Removed by Filter: 5874"
-- "  ->  Index Scan using translation_name_lang_idx on translation translation_1  (cost=0.29..0.47 rows=1 width=16) (actual time=0.018..0.018 rows=1 loops=15)"
-- "        Index Cond: (((name)::text = (translation.name)::text) AND ((lang)::text = 'RU'::text))"
-- "        Filter: (translation.site_id = site_id)"
-- "Total runtime: 6.572 ms"



SELECT
  t0.name,
  t0.site_id,
  t0.lang
FROM (SELECT *
      FROM translation) t0
  INNER JOIN (SELECT
                name,
                site_id
              FROM translation
              WHERE lang = 'RU'

              EXCEPT

              SELECT
                name,
                site_id
              FROM translation
              WHERE lang = 'UA') t1
    ON (t0.name = t1.name AND t0.site_id = t1.site_id)
WHERE t0.ui = TRUE;

-- EXPLAIN ANALYZE:
-- "Hash Join  (cost=220.18..681.09 rows=4 width=19) (actual time=7.039..7.105 rows=9 loops=1)"
-- "  Hash Cond: (((t1.name)::text = (translation.name)::text) AND (t1.site_id = translation.site_id))"
-- "  ->  Subquery Scan on t1  (cost=0.00..448.30 rows=629 width=278) (actual time=4.965..5.016 rows=35 loops=1)"
-- "        ->  HashSetOp Except  (cost=0.00..442.00 rows=629 width=16) (actual time=4.964..5.013 rows=35 loops=1)"
-- "              ->  Append  (cost=0.00..420.27 rows=4347 width=16) (actual time=0.010..3.810 rows=4347 loops=1)"
-- "                    ->  Subquery Scan on "*SELECT* 1"  (cost=0.00..207.89 rows=1949 width=16) (actual time=0.010..1.688 rows=1949 loops=1)"
-- "                          ->  Seq Scan on translation translation_1  (cost=0.00..188.40 rows=1949 width=16) (actual time=0.010..1.403 rows=1949 loops=1)"
-- "                                Filter: ((lang)::text = 'RU'::text)"
-- "                                Rows Removed by Filter: 6323"
-- "                    ->  Subquery Scan on "*SELECT* 2"  (cost=0.00..212.38 rows=2398 width=16) (actual time=0.006..1.753 rows=2398 loops=1)"
-- "                          ->  Seq Scan on translation translation_2  (cost=0.00..188.40 rows=2398 width=16) (actual time=0.006..1.457 rows=2398 loops=1)"
-- "                                Filter: ((lang)::text = 'UA'::text)"
-- "                                Rows Removed by Filter: 5874"
-- "  ->  Hash  (cost=167.72..167.72 rows=3497 width=19) (actual time=2.045..2.045 rows=3497 loops=1)"
-- "        Buckets: 1024  Batches: 1  Memory Usage: 176kB"
-- "        ->  Seq Scan on translation  (cost=0.00..167.72 rows=3497 width=19) (actual time=0.005..1.239 rows=3497 loops=1)"
-- "              Filter: ui"
-- "              Rows Removed by Filter: 4775"
-- "Total runtime: 7.145 ms"



WITH subquery AS (
    SELECT
      t1.name,
      t1.site_id,
      t1.lang
    FROM (SELECT *
          FROM translation
          WHERE lang != 'UA') t1
      LEFT OUTER JOIN (SELECT *
                       FROM translation
                       WHERE lang = 'UA') t2
        ON (t1.name = t2.name AND t1.site_id = t2.site_id)
    WHERE t1.ui = TRUE AND t2.name IS NULL)

SELECT
  s1.name,
  s1.site_id,
  s1.lang
FROM subquery s1 LEFT OUTER JOIN subquery s2
    ON (s1.name = s2.name AND s1.site_id = s2.site_id AND s1.lang != s2.lang)
WHERE s1.lang = 'RU' OR (s1.lang != 'RU' AND s2.lang = 'RU');

-- EXPLAIN ANALYZE:
-- "Nested Loop Left Join  (cost=224.94..730.98 rows=91 width=19) (actual time=3.148..6.899 rows=9 loops=1)"
-- "  Filter: (((translation.lang)::text = 'RU'::text) OR (((translation.lang)::text <> 'RU'::text) AND ((translation_2.lang)::text = 'RU'::text)))"
-- "  Rows Removed by Filter: 6"
-- "  ->  Hash Anti Join  (cost=224.37..455.73 rows=245 width=19) (actual time=3.102..6.530 rows=15 loops=1)"
-- "        Hash Cond: (((translation.name)::text = (translation_1.name)::text) AND (translation.site_id = translation_1.site_id))"
-- "        ->  Seq Scan on translation  (cost=0.00..188.40 rows=2483 width=19) (actual time=0.015..1.911 rows=3497 loops=1)"
-- "              Filter: (ui AND ((lang)::text <> 'UA'::text))"
-- "              Rows Removed by Filter: 4775"
-- "        ->  Hash  (cost=188.40..188.40 rows=2398 width=16) (actual time=2.943..2.943 rows=2398 loops=1)"
-- "              Buckets: 1024  Batches: 1  Memory Usage: 126kB"
-- "              ->  Seq Scan on translation translation_1  (cost=0.00..188.40 rows=2398 width=16) (actual time=0.006..2.082 rows=2398 loops=1)"
-- "                    Filter: ((lang)::text = 'UA'::text)"
-- "                    Rows Removed by Filter: 5874"
-- "  ->  Nested Loop Anti Join  (cost=0.57..1.11 rows=1 width=19) (actual time=0.022..0.023 rows=0 loops=15)"
-- "        ->  Index Scan using translation_name_site_id_lang_idx on translation translation_2  (cost=0.29..0.56 rows=1 width=19) (actual time=0.018..0.018 rows=0 loops=15)"
-- "              Index Cond: (((translation.name)::text = (name)::text) AND (translation.site_id = site_id))"
-- "              Filter: (ui AND ((lang)::text <> 'UA'::text) AND ((translation.lang)::text <> (lang)::text))"
-- "              Rows Removed by Filter: 1"
-- "        ->  Index Scan using translation_name_lang_idx on translation translation_3  (cost=0.29..0.54 rows=1 width=16) (actual time=0.012..0.012 rows=0 loops=4)"
-- "              Index Cond: (((translation_2.name)::text = (name)::text) AND ((lang)::text = 'UA'::text))"
-- "              Filter: (translation_2.site_id = site_id)"
-- "Total runtime: 7.025 ms"
