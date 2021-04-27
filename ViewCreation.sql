use test_timekeeper;
-- DROP ALL VIEWS
DROP VIEW IF EXISTS user_total_time;
DROP VIEW IF EXISTS user_category_time;
DROP VIEW IF EXISTS user_percent_time;
DROP VIEW IF EXISTS world_category_time;
DROP VIEW IF EXISTS world_total_time;
DROP VIEW IF EXISTS world_percent_time;

-- CREATE ALL VIEWS
CREATE VIEW user_total_time AS
SELECT username, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS total_time
FROM entry
GROUP BY username;

CREATE VIEW user_category_time AS
SELECT username, category_name, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS category_time
FROM entry
GROUP BY username, category_name;

CREATE VIEW user_percent_time AS
SELECT entry.username, entry.category_name, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time)))/user_total_time.total_time * 100 AS percent_time
FROM entry
INNER JOIN(user_total_time)
ON entry.username = user_total_time.username
GROUP BY username, category_name;

CREATE VIEW world_total_time AS
SELECT SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS total_time
FROM entry;

CREATE VIEW world_category_time AS
SELECT category_name, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time))) AS category_time
FROM entry
GROUP BY category_name;

CREATE VIEW world_percent_time AS
SELECT category_name, SUM(TIME_TO_SEC(timediff(end_date_time, start_date_time)))/world_total_time.total_time * 100 AS percent_time
FROM entry
INNER JOIN(world_total_time)
GROUP BY category_name, world_total_time.total_time;