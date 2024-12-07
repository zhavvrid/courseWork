    package com.example.client.Models.Entities;


    import java.sql.Timestamp;

    public class Message {

        private int id;

        private User sender;

        private User receiver;

        private String content;

        private Timestamp timestamp;

        private boolean isRead = false;

        public Message(int id, User sender, User receiver, String content, Timestamp timestamp, boolean isRead) {
            this.id = id;
            this.sender = sender;
            this.receiver = receiver;
            this.content = content;
            this.timestamp = timestamp;
            this.isRead = isRead;
        }

        public Message() {
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

        public boolean isRead() {
            return isRead;
        }

        public void setRead(boolean read) {
            isRead = read;
        }
    }

