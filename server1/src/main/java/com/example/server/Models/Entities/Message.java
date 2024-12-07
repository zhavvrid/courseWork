package com.example.server.Models.Entities;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "sender_id", nullable = false)
    private User sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id", nullable = false)
    private User receiver;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "timestamp", nullable = false)
    private Timestamp timestamp;

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT(1)")
    private boolean isReadStatus = false;

    public boolean getIsReadStatus() {
        return isReadStatus;
    }

    public void setIsReadStatus(boolean isReadStatus) {
        this.isReadStatus = isReadStatus;
    }

    public Message(User sender, User receiver, String content, boolean isReadStatus) {
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.isReadStatus = isReadStatus;
    }

    public Message() {
    }

    public Message(int id, User sender, User receiver, String content, Timestamp timestamp, boolean isReadStatus) {
        this.id = id;
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.timestamp = new Timestamp(System.currentTimeMillis());
        this.isReadStatus = isReadStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isReadStatus() {
        return isReadStatus;
    }

    public void setRead(boolean read) {
        this.isReadStatus = read;
    }
}

