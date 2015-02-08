SET SCHEMA 'hhschool';

-- Для каждого user_id получить самую раннюю (по modification_time) запись в translation_history для
-- которых ui = true и вывести только 10 записей самых ранних по modification_time отсортированных
-- по возрастанию modification_time.
SELECT *
FROM translation_history
WHERE translation_history_id IN (
  SELECT sq.translation_history_id
  FROM (
    SELECT
      th.user_id,
      min(th.modification_time),
      min(translation_history_id) AS translation_history_id
    FROM translation_history th
      INNER JOIN translation t
        ON (th.name = t.name AND th.lang = t.lang AND th.site_id = t.site_id)
    WHERE t.ui = TRUE
    GROUP BY th.user_id
    ORDER BY 2 ASC
    LIMIT 10
  ) AS sq
);

-- EXPLAIN ANALYZE:
-- "Hash Semi Join  (cost=540.39..668.43 rows=10 width=57) (actual time=5.829..7.760 rows=10 loops=1)"
-- "  Hash Cond: (translation_history.translation_history_id = sq.translation_history_id)"
-- "  ->  Seq Scan on translation_history  (cost=0.00..113.60 rows=5460 width=57) (actual time=0.004..0.855 rows=5460 loops=1)"
-- "  ->  Hash  (cost=540.26..540.26 rows=10 width=4) (actual time=5.771..5.771 rows=10 loops=1)"
-- "        Buckets: 1024  Batches: 1  Memory Usage: 1kB"
-- "        ->  Subquery Scan on sq  (cost=540.14..540.26 rows=10 width=4) (actual time=5.756..5.765 rows=10 loops=1)"
-- "              ->  Limit  (cost=540.14..540.16 rows=10 width=16) (actual time=5.755..5.759 rows=10 loops=1)"
-- "                    ->  Sort  (cost=540.14..540.39 rows=100 width=16) (actual time=5.753..5.756 rows=10 loops=1)"
-- "                          Sort Key: (min(th.modification_time))"
-- "                          Sort Method: top-N heapsort  Memory: 25kB"
-- "                          ->  HashAggregate  (cost=536.98..537.98 rows=100 width=16) (actual time=5.664..5.698 rows=101 loops=1)"
-- "                                ->  Hash Join  (cost=228.92..523.84 rows=1752 width=16) (actual time=2.426..5.172 rows=1717 loops=1)"
-- "                                      Hash Cond: (((th.name)::text = (t.name)::text) AND ((th.lang)::text = (t.lang)::text) AND (th.site_id = t.site_id))"
-- "                                      ->  Seq Scan on translation_history th  (cost=0.00..113.60 rows=5460 width=32) (actual time=0.002..0.563 rows=5460 loops=1)"
-- "                                      ->  Hash  (cost=167.72..167.72 rows=3497 width=19) (actual time=2.401..2.401 rows=3497 loops=1)"
-- "                                            Buckets: 1024  Batches: 1  Memory Usage: 178kB"
-- "                                            ->  Seq Scan on translation t  (cost=0.00..167.72 rows=3497 width=19) (actual time=0.006..1.239 rows=3497 loops=1)"
-- "                                                  Filter: ui"
-- "                                                  Rows Removed by Filter: 4775"
-- "Total runtime: 7.811 ms"
