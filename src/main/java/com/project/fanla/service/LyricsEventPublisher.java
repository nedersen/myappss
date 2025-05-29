package com.project.fanla.service;

import com.project.fanla.event.LyricsEvent;
import com.project.fanla.model.entity.Lyrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Şarkı sözleri olaylarını yayınlayan servis
 */
@Service
public class LyricsEventPublisher {
    
    private static final Logger logger = LoggerFactory.getLogger(LyricsEventPublisher.class);
    
    private final ApplicationEventPublisher eventPublisher;
    
    public LyricsEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
    
    /**
     * Şarkı sözü oluşturuldu olayını yayınlar
     * 
     * @param lyrics Oluşturulan şarkı sözü
     * @param soundId İlgili sesin ID'si
     */
    public void publishLyricsCreated(Lyrics lyrics, Long soundId) {
        logger.debug("Publishing lyrics created event for lyrics ID: {}, sound ID: {}", lyrics.getId(), soundId);
        eventPublisher.publishEvent(new LyricsEvent(this, LyricsEvent.EventType.CREATED, lyrics, soundId));
    }
    
    /**
     * Şarkı sözü güncellendi olayını yayınlar
     * 
     * @param lyrics Güncellenen şarkı sözü
     * @param soundId İlgili sesin ID'si
     */
    public void publishLyricsUpdated(Lyrics lyrics, Long soundId) {
        logger.debug("Publishing lyrics updated event for lyrics ID: {}, sound ID: {}", lyrics.getId(), soundId);
        eventPublisher.publishEvent(new LyricsEvent(this, LyricsEvent.EventType.UPDATED, lyrics, soundId));
    }
    
    /**
     * Şarkı sözü silindi olayını yayınlar
     * 
     * @param lyrics Silinen şarkı sözü
     * @param soundId İlgili sesin ID'si
     */
    public void publishLyricsDeleted(Lyrics lyrics, Long soundId) {
        logger.debug("Publishing lyrics deleted event for lyrics ID: {}, sound ID: {}", lyrics.getId(), soundId);
        eventPublisher.publishEvent(new LyricsEvent(this, LyricsEvent.EventType.DELETED, lyrics, soundId));
    }
}
