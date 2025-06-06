package com.project.internship_backend.services.report;

import com.project.internship_backend.dtos.ReportDTO;
import com.project.internship_backend.entities.Report;
import com.project.internship_backend.entities.ReportStudent;
import com.project.internship_backend.responses.report.ReportDetailResponse;
import com.project.internship_backend.responses.report.ReportResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IReportService {
    //Lecturer create for student
    ReportStudent createReportStudent(
            Long reportId, Long studentCode) throws Exception;
    Report createReport(ReportDTO reportDTO, Long lecturerId) throws Exception;
    Page<ReportResponse> getAllReports(String keyword, PageRequest pageRequest);
    Page<ReportResponse> getReportByLecturer(String keyword, PageRequest pageRequest, Long lecturerId) throws Exception;
    ReportResponse getReportById(Long reportId) throws Exception;
    Page<ReportDetailResponse> getReportsByStudentCode(String keyword, PageRequest pageRequest, Long studentCode);
    Report updateReportByLecturer (Long lecturerId, Long reportId ,ReportDTO reportDTO) throws Exception;
    void deleteReport(Long reportId);
}
