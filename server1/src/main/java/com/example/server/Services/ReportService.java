package com.example.server.Services;

import com.example.server.DAO.ReportDAO;
import com.example.server.Interfaces.Service;
import com.example.server.Models.Entities.Report;
import com.example.server.Models.Entities.User;
import com.example.server.Utility.HibernateUtil;

import java.util.List;

public class ReportService implements Service<Report> {

    private static ReportDAO reportDAO;

    public ReportService() {
        reportDAO = new ReportDAO(HibernateUtil.getSessionFactory());
    }

    @Override
    public void insert(Report report) {
        reportDAO.insert(report);
    }

    @Override
    public void update(Report report) {
        reportDAO.update(report);
    }

    @Override
    public void delete(int id) {
        reportDAO.delete(id);
    }

    @Override
    public Report findById(int id) {
        return reportDAO.find(id);
    }

    @Override
    public List<Report> findAll() {
        return reportDAO.findAll();
    }

    // Проверка на существование отчета по типу
    public static boolean isReportTypeExists(String reportType) {
        Report existingReport = reportDAO.findByReportType(reportType);
        return existingReport != null;
    }

    // Метод для получения отчетов по типу
    public List<Report> getReportsByType(String reportType) {
        return reportDAO.findReportsByType(reportType);
    }

    // Метод для авторизации, в данном контексте используется для проверки прав доступа к отчетам
    public List<Report> getReportsCreatedByUser(User user) {
        return reportDAO.findReportsByUser(user);
    }
}
