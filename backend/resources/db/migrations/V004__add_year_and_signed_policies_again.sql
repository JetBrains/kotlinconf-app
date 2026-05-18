-- ABOUTME: Same as V2, we had to duplicate this to run it again on prod,
-- now with correct table name casing

-- Add year column to Votes (nullable — older clients may omit it)
ALTER TABLE Votes ADD COLUMN IF NOT EXISTS "year" INT;
UPDATE Votes SET "year" = 2025 WHERE "year" IS NULL;

-- Add year column to Feedback (nullable — older clients may omit it)
ALTER TABLE Feedback ADD COLUMN IF NOT EXISTS "year" INT;
UPDATE Feedback SET "year" = 2025 WHERE "year" IS NULL;

-- Create SignedPolicies table
CREATE TABLE IF NOT EXISTS SignedPolicies (
    uuid VARCHAR(50) NOT NULL,
    "timestamp" VARCHAR(50) NOT NULL,
    "year" INT NOT NULL,
    CONSTRAINT pk_SignedPolicies PRIMARY KEY (uuid, "year")
);

-- Backfill SignedPolicies from existing Users (they implicitly accepted 2025 policies)
INSERT INTO SignedPolicies (uuid, "timestamp", "year")
SELECT uuid, "timestamp", 2025 FROM Users
WHERE NOT EXISTS (
    SELECT 1 FROM SignedPolicies sp WHERE sp.uuid = Users.uuid AND sp."year" = 2025
);
