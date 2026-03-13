-- ABOUTME: Fixes table name casing issues from V001/V002 which used quoted mixed-case identifiers.
-- ABOUTME: Renames SignedPolicies to lowercase. Ghost table cleanup is PostgreSQL-only (see below).

-- Rename mixed-case SignedPolicies (created by V002) to lowercase.
-- On H2 (case-insensitive) this is a harmless rename-to-self.
ALTER TABLE "SignedPolicies" RENAME TO signedpolicies;

-- Drop duplicate mixed-case tables that V001 may have created alongside pre-existing lowercase ones.
-- These are PostgreSQL-only: on H2 they'd drop the real tables since it's case-insensitive.
-- To clean these up, run manually on PostgreSQL:
--   DROP TABLE IF EXISTS "Users";
--   DROP TABLE IF EXISTS "Votes";
--   DROP TABLE IF EXISTS "Feedback";
