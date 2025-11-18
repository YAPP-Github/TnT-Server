-- Rename workout record related tables (remaining migrations)
ALTER TABLE routine RENAME TO workout_record_routine;
ALTER TABLE workout_set RENAME TO workout_record_set;
