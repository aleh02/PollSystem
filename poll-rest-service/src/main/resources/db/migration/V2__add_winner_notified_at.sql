ALTER TABLE polls
    ADD COLUMN IF NOT EXISTS winner_notified_at TIMESTAMPTZ NULL;

CREATE INDEX IF NOT EXISTS idx_polls_status_notified
    ON polls (status, winner_notified_at);
