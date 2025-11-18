-- Migrate pt_goals from Korean to English enum names
UPDATE trainee
SET pt_goals = REPLACE(pt_goals, '"체중 감량"', '"WEIGHT_LOSS"')
WHERE pt_goals LIKE '%체중 감량%';

UPDATE trainee
SET pt_goals = REPLACE(pt_goals, '"근력 향상"', '"STRENGTH_ENHANCE"')
WHERE pt_goals LIKE '%근력 향상%';

UPDATE trainee
SET pt_goals = REPLACE(pt_goals, '"건강 관리"', '"HEALTH_MANAGE"')
WHERE pt_goals LIKE '%건강 관리%';

UPDATE trainee
SET pt_goals = REPLACE(pt_goals, '"유연성 향상"', '"FLEXIBILITY_ENHANCE"')
WHERE pt_goals LIKE '%유연성 향상%';

UPDATE trainee
SET pt_goals = REPLACE(pt_goals, '"바디프로필"', '"BODY_PROFILE"')
WHERE pt_goals LIKE '%바디프로필%';

UPDATE trainee
SET pt_goals = REPLACE(pt_goals, '"자세 교정"', '"POSTURE_CORRECTION"')
WHERE pt_goals LIKE '%자세 교정%';

-- Rename workout tables
ALTER TABLE workout_jpa_entity_body_parts RENAME TO workout_body_parts;
ALTER TABLE workout_jpa_entity_machines RENAME TO workout_machines;

-- Rename workout record related tables
ALTER TABLE workout_routine RENAME TO workout_record_routine;
ALTER TABLE workout_set RENAME TO workout_record_set;
