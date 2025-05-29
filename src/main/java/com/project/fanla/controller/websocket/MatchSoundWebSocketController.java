package com.project.fanla.controller.websocket;

import com.project.fanla.model.websocket.SoundWebSocketMessage;
import com.project.fanla.service.MatchSoundStateManager;
import com.project.fanla.service.websocket.WebSocketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Bu sınıf artık kullanılmıyor. WebSocket işlemleri MatchWebSocketHandler tarafından yönetiliyor.
 * Sadece geriye dönük uyumluluk için tutulmuştur.
 */
@Component
public class MatchSoundWebSocketController {
    
    private static final Logger logger = LoggerFactory.getLogger(MatchSoundWebSocketController.class);

    @Autowired
    private WebSocketService webSocketService;
    
    @Autowired
    private MatchSoundStateManager matchSoundStateManager;
    
    /**
     * Belirli bir maç için ses güncellemesi yayınlar
     * 
     * @param matchId Maç ID'si
     */
    public void broadcastSoundUpdate(Long matchId) {
        logger.info("Broadcasting sound update for match {} via controller", matchId);
        MatchSoundStateManager.MatchSoundState state = matchSoundStateManager.getState(matchId);
        if (state != null) {
            webSocketService.broadcastSoundUpdate(matchId, state);
        } else {
            logger.warn("Cannot broadcast sound update for match {} because state is null", matchId);
        }
    }
}
