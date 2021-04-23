USE test_timekeeper;

DROP TRIGGER IF EXISTS datetime_check_insert;

DELIMITER $$
CREATE TRIGGER datetime_check_insert BEFORE INSERT ON entry
FOR EACH ROW
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE sdt, edt DATETIME;
    DECLARE cur CURSOR FOR SELECT start_date_time, end_date_time FROM entry WHERE username = NEW.username;
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO sdt, edt;
        IF done THEN
            LEAVE read_loop;
        END IF;
        IF ((NEW.start_date_time < edt) AND (NEW.start_date_time > sdt)) OR ((NEW.end_date_time < edt) AND (NEW.end_date_time > sdt)) OR (NEW.start_date_time = sdt AND NEW.end_date_time = edt) THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'You can not insert entries that overlap with start or end times of other entries.';
        END IF;
    END LOOP;

    CLOSE cur;
END;$$
DELIMITER ;