package com.project.fanla.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.fanla.event.LyricsEvent;
import com.project.fanla.model.dto.LyricsDto;
import com.project.fanla.model.entity.Lyrics;
import com.project.fanla.model.entity.Match;
import com.project.fanla.model.websocket.SoundWebSocketMessage;
import com.project.fanla.repository.LyricsRepository;
import com.project.fanla.repository.MatchRepository;
import com.project.fanla.service.MatchSoundStateManager;
import com.project.fanla.service.websocket.WebSocketService.SoundUpdateEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
public class MatchWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(MatchWebSocketHandler.class);
    private static final UriTemplate MATCH_ID_TEMPLATE = new UriTemplate("/match-socket/{matchId}");
    
    // Key: matchId, Value: Map<sessionId, session>
    private final Map<Long, Map<String, WebSocketSession>> matchSessions = new ConcurrentHashMap<>();
    
    // Key: soundId, Value: List<LyricsDto>
    private final Map<Long, List<LyricsDto>> lyricsCache = new ConcurrentHashMap<>();
    

    
    @Autowired
    private MatchSoundStateManager matchSoundStateManager;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private LyricsRepository lyricsRepository;
    
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long matchId = getMatchId(session);
        if (matchId != null) {
            // Maç için oturum haritasını al veya oluştur
            Map<String, WebSocketSession> sessions = matchSessions.computeIfAbsent(matchId, k -> new ConcurrentHashMap<>());
            
            // Oturumu ekle
            sessions.put(session.getId(), session);
            
            logger.info("WebSocket connection established for match {}, session ID: {}", matchId, session.getId());
            
            // Mevcut ses durumunu gönder
            sendCurrentSoundState(matchId, session);
        } else {
            logger.warn("Cannot extract match ID from session: {}", session.getId());
            session.close(CloseStatus.BAD_DATA);
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        Long matchId = getMatchId(session);
        if (matchId != null) {
            Map<String, WebSocketSession> sessions = matchSessions.get(matchId);
            if (sessions != null) {
                sessions.remove(session.getId());
                
                logger.info("WebSocket connection closed for match {}, session ID: {}, status: {}", 
                        matchId, session.getId(), status);
                
                // Eğer maç için hiç oturum kalmadıysa, maç haritasından kaldır
                if (sessions.isEmpty()) {
                    matchSessions.remove(matchId);
                    logger.info("Removed match {} from sessions map, no more active sessions", matchId);
                }
            }
        }
    }
    
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Bu örnekte istemciden mesaj almıyoruz, sadece sunucudan istemciye mesaj gönderiyoruz
        logger.debug("Received message from client: {}", message.getPayload());
    }
    
    /**
     * Ses güncelleme olayını dinler
     * 
     * @param event Ses güncelleme olayı
     */
    @EventListener
    public void handleSoundUpdateEvent(SoundUpdateEvent event) {
        Long matchId = event.getMatchId();
        logger.info("Received sound update event for match {}", matchId);
        broadcastSoundUpdate(matchId);
    }
    
    /**
     * Belirli bir maç için tüm bağlı istemcilere ses güncellemesi gönderir
     * 
     * @param matchId Maç ID'si
     */
    public void broadcastSoundUpdate(Long matchId) {
        Map<String, WebSocketSession> sessions = matchSessions.get(matchId);
        if (sessions != null && !sessions.isEmpty()) {
            MatchSoundStateManager.MatchSoundState state = matchSoundStateManager.getState(matchId);
            if (state != null) {
                SoundWebSocketMessage message = createSoundWebSocketMessage(matchId, state);
                
                try {
                    String jsonMessage = objectMapper.writeValueAsString(message);
                    TextMessage textMessage = new TextMessage(jsonMessage);
                    
                    logger.info("Broadcasting sound update to {} clients for match {}: Sound ID: {}, Status: {}, Position: {}", 
                            sessions.size(), matchId, state.getActiveSoundId(), state.getSoundStatus(), state.getCurrentMillisecond());
                    
                    for (WebSocketSession session : sessions.values()) {
                        if (session.isOpen()) {
                            try {
                                session.sendMessage(textMessage);
                            } catch (IOException e) {
                                logger.error("Error sending message to session {}: {}", session.getId(), e.getMessage());
                            }
                        }
                    }
                } catch (Exception e) {
                    logger.error("Error serializing sound update message: {}", e.getMessage());
                }
            } else {
                logger.warn("Cannot broadcast sound update for match {} because state is null", matchId);
            }
        } else {
            logger.debug("No active sessions for match {}", matchId);
        }
    }
    
    /**
     * Belirli bir oturuma mevcut ses durumunu gönderir
     * 
     * @param matchId Maç ID'si
     * @param session WebSocket oturumu
     */
    private void sendCurrentSoundState(Long matchId, WebSocketSession session) {
        MatchSoundStateManager.MatchSoundState state = matchSoundStateManager.getState(matchId);
        
        if (state != null) {
            // RAM'deki mevcut durumu doğrudan kullan, veritabanından çekme
            SoundWebSocketMessage message = createSoundWebSocketMessage(matchId, state);
            
            try {
                String jsonMessage = objectMapper.writeValueAsString(message);
                TextMessage textMessage = new TextMessage(jsonMessage);
                
                logger.info("Sending initial sound state to session {} for match {}: Sound ID: {}, Status: {}, Position: {}", 
                        session.getId(), matchId, state.getActiveSoundId(), state.getSoundStatus(), state.getCurrentMillisecond());
                
                session.sendMessage(textMessage);
            } catch (Exception e) {
                logger.error("Error sending initial sound state to session {}: {}", session.getId(), e.getMessage());
            }
        } else {
            logger.info("No sound state found for match {}", matchId);
        }
    }
    
    /**
     * Ses durumundan WebSocket mesajı oluşturur
     * 
     * @param matchId Maç ID'si
     * @param state Ses durumu
     * @return WebSocket mesajı
     */
    private SoundWebSocketMessage createSoundWebSocketMessage(Long matchId, MatchSoundStateManager.MatchSoundState state) {
        // Şarkı sözlerini önbellekten al
        List<LyricsDto> lyricsList = null;
        if (state.getActiveSoundId() != null) {
            // Önbellekte var mı kontrol et
            lyricsList = lyricsCache.get(state.getActiveSoundId());
            
            // Önbellekte yoksa veritabanından al ve önbelleğe ekle
            if (lyricsList == null) {
                lyricsList = lyricsRepository.findBySoundIdOrderBySecondAsc(state.getActiveSoundId())
                    .stream()
                    .map(lyrics -> new LyricsDto(lyrics.getId(), lyrics.getLyric(), lyrics.getSecond()))
                    .collect(Collectors.toList());
                
                // Önbelleğe ekle
                lyricsCache.put(state.getActiveSoundId(), lyricsList);
                logger.debug("Cached {} lyrics for sound {}", lyricsList.size(), state.getActiveSoundId());
            } else {
                logger.debug("Using cached lyrics for sound {}", state.getActiveSoundId());
            }
        }
        
        return new SoundWebSocketMessage(
            matchId,
            state.getActiveSoundId(),
            state.getActiveSoundTitle(),
            state.getActiveSoundUrl(),
            state.getActiveSoundImageUrl(),
            state.getSoundStatus(),
            state.getCurrentMillisecond(),
            lyricsList
        );
    }
    
    /**
     * WebSocket oturumundan maç ID'sini çıkarır
     * 
     * @param session WebSocket oturumu
     * @return Maç ID'si veya null
     */
    private Long getMatchId(WebSocketSession session) {
        String path = session.getUri().getPath();
        Map<String, String> variables = MATCH_ID_TEMPLATE.match(path);
        
        if (variables != null && variables.containsKey("matchId")) {
            try {
                return Long.parseLong(variables.get("matchId"));
            } catch (NumberFormatException e) {
                logger.error("Invalid match ID format: {}", variables.get("matchId"));
            }
        }
        
        return null;
    }
    
    /**
     * Şarkı sözleri olaylarını dinler ve önbelleği günceller
     * 
     * @param event Şarkı sözleri olayı
     */
    @EventListener
    public void handleLyricsEvent(LyricsEvent event) {
        Long soundId = event.getSoundId();
        
        switch (event.getEventType()) {
            case CREATED:
            case UPDATED:
                logger.info("Lyrics {} for sound {}, updating cache", 
                        event.getEventType() == LyricsEvent.EventType.CREATED ? "created" : "updated", 
                        soundId);
                
                // Önbellekteki şarkı sözlerini temizle, bir sonraki istekte yeniden yüklenecek
                lyricsCache.remove(soundId);
                break;
                
            case DELETED:
                logger.info("Lyrics deleted for sound {}, updating cache", soundId);
                
                // Önbellekteki şarkı sözlerini temizle
                lyricsCache.remove(soundId);
                break;
        }
    }
}
