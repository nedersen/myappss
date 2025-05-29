package com.project.fanla.service.websocket;

import com.project.fanla.model.websocket.SoundWebSocketMessage;
import com.project.fanla.service.MatchSoundStateManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

/**
 * Bu sınıf artık olay tabanlı bir yaklaşım kullanıyor.
 * Geriye dönük uyumluluk için tutulmuştur.
 */
@Service
public class WebSocketService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketService.class);
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    /**
     * Belirli bir maç için ses güncellemesini yayınlar
     * 
     * @param matchId Maç ID'si
     * @param state Ses durumu
     */
    public void broadcastSoundUpdate(Long matchId, MatchSoundStateManager.MatchSoundState state) {
        logger.info("Publishing sound update event for match {}", matchId);
        eventPublisher.publishEvent(new SoundUpdateEvent(matchId));
    }
    
    /**
     * Bir maç için bağlı istemci sayısını döndürür
     * 
     * @param matchId Maç ID'si
     * @return İstemci sayısı
     */
    public int getClientCount(Long matchId) {
        // Bu metot artık kullanılmıyor, ancak geriye dönük uyumluluk için tutulmuştur
        return 0;
    }
    
    /**
     * Ses güncelleme olayı
     */
    public static class SoundUpdateEvent {
        private final Long matchId;
        
        public SoundUpdateEvent(Long matchId) {
            this.matchId = matchId;
        }
        
        public Long getMatchId() {
            return matchId;
        }
    }
}
