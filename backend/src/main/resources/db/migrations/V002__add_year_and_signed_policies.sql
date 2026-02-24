-- ABOUTME: Adds year column to Votes and Feedback, creates SignedPolicies table.
-- ABOUTME: Backfills existing data with year=2025 and migrates policy data from Users.

-- Add year column to Votes
ALTER TABLE "Votes" ADD COLUMN IF NOT EXISTS "year" INT;
UPDATE "Votes" SET "year" = 2025 WHERE "year" IS NULL;
ALTER TABLE "Votes" ALTER COLUMN "year" SET NOT NULL;
ALTER TABLE "Votes" DROP CONSTRAINT IF EXISTS "pk_Votes";
ALTER TABLE "Votes" ADD CONSTRAINT "pk_Votes" PRIMARY KEY ("uuid", "sessionId", "year");

-- Add year column to Feedback
ALTER TABLE "Feedback" ADD COLUMN IF NOT EXISTS "year" INT;
UPDATE "Feedback" SET "year" = 2025 WHERE "year" IS NULL;
ALTER TABLE "Feedback" ALTER COLUMN "year" SET NOT NULL;
ALTER TABLE "Feedback" DROP CONSTRAINT IF EXISTS "pk_Feedback";
ALTER TABLE "Feedback" ADD CONSTRAINT "pk_Feedback" PRIMARY KEY ("uuid", "sessionId", "year");

-- Create SignedPolicies table
CREATE TABLE IF NOT EXISTS "SignedPolicies" (
    "uuid" VARCHAR(50) NOT NULL,
    "timestamp" VARCHAR(50) NOT NULL,
    "year" INT NOT NULL,
    CONSTRAINT "pk_SignedPolicies" PRIMARY KEY ("uuid", "year")
);

-- Backfill SignedPolicies from existing Users (they implicitly accepted 2025 policies)
INSERT INTO "SignedPolicies" ("uuid", "timestamp", "year")
SELECT "uuid", "timestamp", 2025 FROM "Users"
WHERE NOT EXISTS (
    SELECT 1 FROM "SignedPolicies" sp WHERE sp."uuid" = "Users"."uuid" AND sp."year" = 2025
);
