SET SCHEMA 'hhschool';

-- Получить количество работодателей каждой из organization_form, отбросив все organization_form для
-- которых найдено менее 3 записей. Результат отсортировать по убыванию количества найденных записей.
SELECT
  organization_form,
  count(employer_id)
FROM employer
GROUP BY organization_form
HAVING count(employer_id) >= 3
ORDER BY 2 DESC;

-- EXPLAIN ANALYZE:
-- "Sort  (cost=1.48..1.49 rows=4 width=8) (actual time=0.091..0.092 rows=1 loops=1)"
-- "  Sort Key: (count(employer_id))"
-- "  Sort Method: quicksort  Memory: 25kB"
-- "  ->  HashAggregate  (cost=1.39..1.44 rows=4 width=8) (actual time=0.064..0.066 rows=1 loops=1)"
-- "        Filter: (count(employer_id) >= 3)"
-- "        Rows Removed by Filter: 4"
-- "        ->  Seq Scan on employer  (cost=0.00..1.22 rows=22 width=8) (actual time=0.006..0.010 rows=22 loops=1)"
-- "Total runtime: 0.184 ms"
