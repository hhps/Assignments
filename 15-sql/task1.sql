SET SCHEMA 'hhschool';

-- Получить список organization_form, использующихся в таблице employer отсортированные по убыванию.
SELECT
  DISTINCT organization_form
FROM employer
WHERE organization_form IS NOT NULL
ORDER BY 1 DESC;

-- EXPLAIN ANALYZE:
-- "Sort  (cost=1.25..1.26 rows=1 width=4) (actual time=0.029..0.030 rows=4 loops=1)"
-- "  Sort Key: organization_form"
-- "  Sort Method: quicksort  Memory: 25kB"
-- "  ->  HashAggregate  (cost=1.23..1.24 rows=1 width=4) (actual time=0.021..0.021 rows=4 loops=1)"
-- "        ->  Seq Scan on employer  (cost=0.00..1.22 rows=5 width=4) (actual time=0.010..0.016 rows=5 loops=1)"
-- "              Filter: (organization_form IS NOT NULL)"
-- "              Rows Removed by Filter: 17"
-- "Total runtime: 0.058 ms"



SELECT
  organization_form
FROM employer
WHERE organization_form IS NOT NULL
GROUP BY organization_form
ORDER BY 1 DESC;

-- EXPLAIN ANALYZE:
-- "Sort  (cost=1.25..1.26 rows=1 width=4) (actual time=0.025..0.025 rows=4 loops=1)"
-- "  Sort Key: organization_form"
-- "  Sort Method: quicksort  Memory: 25kB"
-- "  ->  HashAggregate  (cost=1.23..1.24 rows=1 width=4) (actual time=0.018..0.018 rows=4 loops=1)"
-- "        ->  Seq Scan on employer  (cost=0.00..1.22 rows=5 width=4) (actual time=0.008..0.011 rows=5 loops=1)"
-- "              Filter: (organization_form IS NOT NULL)"
-- "              Rows Removed by Filter: 17"
-- "Total runtime: 0.058 ms"
