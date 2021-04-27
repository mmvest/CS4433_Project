use test_timekeeper;

-- SELECT entry.username, 
-- entry.category_name, 
-- user_category_time.category_time AS category_time, 
-- user_percent_time.percent_time AS percent_time
-- -- world_category_time.category_time AS global_category_time,
-- -- world_category_time.category_time/world_total_time.total_time*100 AS global_percent_time
-- FROM entry
-- INNER JOIN (user_category_time)
-- ON entry.username = user_category_time.username AND entry.category_name = user_category_time.category_name
-- INNER JOIN (user_percent_time)
-- ON entry.username = user_percent_time.username AND entry.category_name = user_percent_time.category_name
-- -- INNER JOIN (world_category_time)
-- -- ON entry.category_name = world_category_time.category_name
-- -- INNER JOIN (world_total_time)
-- WHERE entry.username = "wballoch0" AND start_date_time < "21-04-05 00:00:00"
-- GROUP BY entry.category_name;

SELECT
e1.username,
e1.category_name,
SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS category_time,
SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time)))/e2.total_time * 100 AS percent_time,
global_category_time,
global_category_time/global_total_time * 100 AS global_percent_time
FROM entry e1
INNER JOIN(
    SELECT e2.username, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS total_time
    FROM entry e2
    WHERE e2.username = "wballoch0" AND e2.start_date_time < "21-04-05 00:00:00" AND e2.category_name IN ("Sleeping","Work")
    GROUP BY e2.username
) AS e2
ON e1.username = e2.username
INNER JOIN(
    SELECT category_name, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS global_category_time
    FROM entry e3
    WHERE e3.start_date_time < "21-04-05 00:00:00" AND e3.category_name IN ("Sleeping","Work")
    GROUP BY e3.category_name
) AS e3
ON e1.category_name = e3.category_name
INNER JOIN(
    SELECT SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS global_total_time
    FROM entry e4
    WHERE e4.start_date_time < "21-04-05 00:00:00" AND e4.category_name IN ("Sleeping","Work")
) AS e4
WHERE e1.username = "wballoch0" AND e1.start_date_time < "21-04-05 00:00:00" AND e1.category_name IN ("Sleeping","Work")
GROUP BY e1.username, e1.category_name, global_total_time;