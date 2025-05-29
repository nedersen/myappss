package com.project.fanla.event;

import com.project.fanla.model.entity.Sound;
import org.springframework.context.ApplicationEvent;

/**
 * Ses ile ilgili olaylar için event sınıfı
 */
public class SoundEvent extends ApplicationEvent {
    
    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
    
    private final EventType eventType;
    private final Sound sound;
    
    public SoundEvent(Object source, EventType eventType, Sound sound) {
        super(source);
        this.eventType = eventType;
        this.sound = sound;
    }
    
    public EventType getEventType() {
        return eventType;
    }
    
    public Sound getSound() {
        return sound;
    }
}
