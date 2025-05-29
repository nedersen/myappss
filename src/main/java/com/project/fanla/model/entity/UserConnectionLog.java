package com.project.fanla.model.entity;

import jakarta.persistence.*;
import com.project.fanla.model.enums.ConnectionType;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_connection_logs")
public class UserConnectionLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "ip_address", nullable = false)
    private String ipAddress;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "connection_type", nullable = false)
    private ConnectionType connectionType;
    
    @Column(name = "connection_time", nullable = false)
    private LocalDateTime connectionTime;
    
    // Constructors
    public UserConnectionLog() {
    }
    
    public UserConnectionLog(User user, String ipAddress, ConnectionType connectionType) {
        this.user = user;
        this.ipAddress = ipAddress;
        this.connectionType = connectionType;
        this.connectionTime = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public String getIpAddress() {
        return ipAddress;
    }
    
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    
    public ConnectionType getConnectionType() {
        return connectionType;
    }
    
    public void setConnectionType(ConnectionType connectionType) {
        this.connectionType = connectionType;
    }
    
    public LocalDateTime getConnectionTime() {
        return connectionTime;
    }
    
    public void setConnectionTime(LocalDateTime connectionTime) {
        this.connectionTime = connectionTime;
    }
}
