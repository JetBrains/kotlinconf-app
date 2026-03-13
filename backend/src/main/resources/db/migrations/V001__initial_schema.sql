-- ABOUTME: Initial schema migration creating Users, Votes, and Feedback tables.
-- ABOUTME: Matches the schema previously created by SchemaUtils.create().

CREATE TABLE IF NOT EXISTS Users (
    uuid VARCHAR(50) NOT NULL,
    timestamp VARCHAR(50) NOT NULL,
    CONSTRAINT pk_Users PRIMARY KEY (uuid)
);

CREATE INDEX IF NOT EXISTS Users_uuid ON Users (uuid);

CREATE TABLE IF NOT EXISTS Votes (
    timestamp VARCHAR(50) NOT NULL,
    uuid VARCHAR(50) NOT NULL,
    sessionId VARCHAR(50) NOT NULL,
    rating INT NOT NULL,
    CONSTRAINT pk_Votes PRIMARY KEY (uuid, sessionId)
);

CREATE INDEX IF NOT EXISTS Votes_uuid ON Votes (uuid);
CREATE INDEX IF NOT EXISTS Votes_sessionId ON Votes (sessionId);

CREATE TABLE IF NOT EXISTS Feedback (
    timestamp VARCHAR(50) NOT NULL,
    uuid VARCHAR(50) NOT NULL,
    sessionId VARCHAR(50) NOT NULL,
    feedback VARCHAR(5000) NOT NULL,
    CONSTRAINT pk_Feedback PRIMARY KEY (uuid, sessionId)
);

CREATE INDEX IF NOT EXISTS Feedback_uuid ON Feedback (uuid);
CREATE INDEX IF NOT EXISTS Feedback_sessionId ON Feedback (sessionId);
