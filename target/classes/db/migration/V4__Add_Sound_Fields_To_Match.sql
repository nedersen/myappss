-- Add sound related fields to matches table
ALTER TABLE matches ADD COLUMN active_sound_id BIGINT;
ALTER TABLE matches ADD COLUMN sound_status VARCHAR(20) DEFAULT 'STOPPED';
ALTER TABLE matches ADD COLUMN current_millisecond BIGINT DEFAULT 0;
ALTER TABLE matches ADD COLUMN sound_updated_at TIMESTAMP;

-- Add foreign key constraint
ALTER TABLE matches ADD CONSTRAINT fk_matches_active_sound FOREIGN KEY (active_sound_id) REFERENCES sounds(id);

-- Copy data from match_sound_details to matches (if needed)
UPDATE matches m
SET 
    active_sound_id = (
        SELECT msd.active_sound_id 
        FROM match_sound_details msd 
        WHERE msd.match_id = m.id 
        ORDER BY msd.id DESC 
        LIMIT 1
    ),
    sound_status = (
        SELECT msd.sound_status 
        FROM match_sound_details msd 
        WHERE msd.match_id = m.id 
        ORDER BY msd.id DESC 
        LIMIT 1
    ),
    current_millisecond = (
        SELECT msd.current_millisecond 
        FROM match_sound_details msd 
        WHERE msd.match_id = m.id 
        ORDER BY msd.id DESC 
        LIMIT 1
    )
WHERE EXISTS (
    SELECT 1 
    FROM match_sound_details msd 
    WHERE msd.match_id = m.id
);

-- Set sound_updated_at to current timestamp for updated records
UPDATE matches
SET sound_updated_at = NOW()
WHERE active_sound_id IS NOT NULL;
