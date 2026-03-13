-- ABOUTME: Fixes table name casing issues from V001/V002 which used quoted mixed-case identifiers.
-- ABOUTME: Renames SignedPolicies to lowercase. Ghost table cleanup is PostgreSQL-only (see below).

-- Rename mixed-case SignedPolicies (created by older SchemaUtils code) to lowercase.
-- Two-step rename via a temp name avoids H2's "already exists" error: H2 is
-- case-insensitive so "SignedPolicies" and signedpolicies resolve to the same table.
ALTER TABLE IF EXISTS "SignedPolicies" RENAME TO signedpolicies_migration_tmp;
ALTER TABLE IF EXISTS signedpolicies_migration_tmp RENAME TO signedpolicies;

-- Drop duplicate mixed-case tables that V001 may have created alongside pre-existing lowercase ones.
-- These are PostgreSQL-only: on H2 they'd drop the real tables since it's case-insensitive.
-- To clean these up, run manually on PostgreSQL:
--   DROP TABLE IF EXISTS "Users";
--   DROP TABLE IF EXISTS "Votes";
--   DROP TABLE IF EXISTS "Feedback";
