package com.project.fanla.event;

import com.project.fanla.model.entity.Lyrics;
import org.springframework.context.ApplicationEvent;

/**
 * Şarkı sözleri ile ilgili olaylar için event sınıfı
 */
public class LyricsEvent extends ApplicationEvent {
    
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
    
    private final EventType eventType;
    private final Lyrics lyrics;
    private final Long soundId;
    
    public LyricsEvent(Object source, EventType eventType, Lyrics lyrics, Long soundId) {
        super(source);
        this.eventType = eventType;
        this.lyrics = lyrics;
        this.soundId = soundId;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public Lyrics getLyrics() {
        return lyrics;
    }
    
    public Long getSoundId() {
        return soundId;
    }
}
