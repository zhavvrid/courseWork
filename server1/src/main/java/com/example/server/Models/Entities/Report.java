package com.example.server.Models.Entities;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "reports")
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // Идентификатор отчета

    @Column(name = "report_type", nullable = false, length = 50)
    private String reportType; // Тип отчета

    @Column(name = "content", columnDefinition = "TEXT")
    private String content; // Содержимое отчета

    @Column(name = "created_date", nullable = false, updatable = false)
    private Timestamp createdDate; // Время создания отчета

    @ManyToOne
    @JoinColumn(name = "created_by_user_id", nullable = false)
    private User createdBy; // Пользователь, создавший отчет

    @Lob
    @Column(name = "file_data")
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

    public Report(String reportType, String content, User createdBy, byte[] fileData) {
        this.reportType = reportType;
        this.content = content;
        this.createdBy = createdBy;
        this.fileData = fileData;
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
