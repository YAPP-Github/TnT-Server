-- Add pt_goals column to trainee table
ALTER TABLE trainee
ADD COLUMN pt_goals TEXT NOT NULL DEFAULT '[]';
