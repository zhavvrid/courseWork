package com.example.client.Models.Entities;


import com.example.client.Models.Entities.User;

import java.sql.Timestamp;

public class Report {

    private int id; // Идентификатор отчета

    private String reportType; // Тип отчета

    private String content; // Содержимое отчета

    private Timestamp createdDate; // Время создания отчета

    private User createdBy; // Пользователь, создавший отчет

    private byte[] fileData;

    public byte[] getFileData() {
        return fileData;
    }

    public void setFileData(byte[] fileData) {
        this.fileData = fileData;
    }

    // Конструктор по умолчанию
    public Report() {
    }

    // Конструктор для создания отчета
    public Report(String reportType, String content, User createdBy) {
        this.reportType = reportType;
        this.content = content;
        this.createdBy = createdBy;
        this.createdDate = new Timestamp(System.currentTimeMillis());
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }
}
