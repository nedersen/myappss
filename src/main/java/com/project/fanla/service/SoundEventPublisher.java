package com.project.fanla.service;

import com.project.fanla.event.SoundEvent;
import com.project.fanla.model.entity.Sound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Ses olaylarını yayınlayan servis
 */
@Service
public class SoundEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(SoundEventPublisher.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public SoundEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Ses oluşturuldu olayını yayınlar
     * 
     * @param sound Oluşturulan ses
     */
    public void publishSoundCreated(Sound sound) {
        logger.debug("Publishing sound created event for sound ID: {}", sound.getId());
        eventPublisher.publishEvent(new SoundEvent(this, SoundEvent.EventType.CREATED, sound));
    }
    
    /**
     * Ses güncellendi olayını yayınlar
     * 
     * @param sound Güncellenen ses
     */
    public void publishSoundUpdated(Sound sound) {
        logger.debug("Publishing sound updated event for sound ID: {}", sound.getId());
        eventPublisher.publishEvent(new SoundEvent(this, SoundEvent.EventType.UPDATED, sound));
    }
    
    /**
     * Ses silindi olayını yayınlar
     * 
     * @param sound Silinen ses
     */
    public void publishSoundDeleted(Sound sound) {
        logger.debug("Publishing sound deleted event for sound ID: {}", sound.getId());
        eventPublisher.publishEvent(new SoundEvent(this, SoundEvent.EventType.DELETED, sound));
    }
}
