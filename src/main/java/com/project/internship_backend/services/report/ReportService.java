package com.project.internship_backend.services.report;

import com.project.internship_backend.dtos.ReportDTO;
import com.project.internship_backend.entities.*;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.LecturerRepository;
import com.project.internship_backend.repositories.ReportRepository;
import com.project.internship_backend.repositories.ReportStudentRepository;
import com.project.internship_backend.repositories.StudentRepository;
import com.project.internship_backend.responses.report.ReportDetailResponse;
import com.project.internship_backend.responses.report.ReportResponse;
import com.project.internship_backend.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class ReportService implements IReportService {
    private final EmailService emailService;
    private final ReportRepository reportRepository;
    private final StudentRepository studentRepository;
    private final LecturerRepository lecturerRepository;
    private final ReportStudentRepository reportStudentRepository;


    @Override
    public Report createReport(ReportDTO reportDTO, Long lecturerId) throws Exception {

        Lecturer existingLecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find lecturer with id : " + lecturerId));
        Report newReport = Report.builder()
                .title(reportDTO.getTitle())
                .description(reportDTO.getDescription())
                .dueDate(reportDTO.getDueDate())
                .lecturer(existingLecturer)
                .build();
        Report savedReport = reportRepository.save(newReport);

        // Lưu ReportStudent và gửi email thông báo
        for (Long studentId : reportDTO.getStudentCodes()) {
            ReportStudent reportStudent = createReportStudent(savedReport.getId(), studentId);
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find student with id: " + studentId));

            //Gửi email thông báo
            String subject = "New Assignment: " + savedReport.getTitle();
            String text = "Dear " + student.getUser().getFullName() + ",\n\n"
                    + "You have been assigned a new assignment:\n"
                    + "Title: " + savedReport.getTitle() + "\n"
                    + "Description: " + savedReport.getDescription() + "\n"
                    + "Due Date: " + savedReport.getDueDate() + "\n\n"
                    + "Please submit your assignment before the due date.\n\n"
                    + "Best regards,\n"
                    + "Your Lecturer";
            emailService.sendEmail(student.getUser().getEmail(), subject, text);
        }

        return savedReport;
    }

    @Override
    public ReportStudent createReportStudent(Long reportId, Long studentCode) throws Exception {
        Report existingReport = reportRepository
                .findById(reportId)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find report with id: "+reportId));
        Student existingStudent = studentRepository
                .findById(studentCode)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                "Cannot find student with id: "+studentCode));
        ReportStudent newReportStudent = ReportStudent.builder()
                .report(existingReport)
                .student(existingStudent)
                .build();
        return reportStudentRepository.save(newReportStudent);
    }

    @Override
    public Page<ReportResponse> getAllReports(String keyword, PageRequest pageRequest) {
        Page<Report> reportsPage;
        reportsPage = reportRepository.findAllReports(keyword, pageRequest);
        return reportsPage.map(ReportResponse::fromReport);
    }

    @Override
    public Page<ReportResponse> getReportByLecturer(String keyword, PageRequest pageRequest, Long lecturerId) throws Exception {
        Page<Report> reportsPage;
        reportsPage = reportRepository.findReportsByLecturer(keyword, pageRequest, lecturerId );

        return reportsPage.map(ReportResponse::fromReport);
    }



    @Override
    public ReportResponse getReportById(Long reportId) throws Exception {
        Report report = reportRepository.findById(reportId).orElseThrow(
                () -> new DataNotFoundException("Report not found")
        );
        return ReportResponse.fromReport(report);
    }



    public Page<ReportDetailResponse> getReportsByStudentCode(String keyword, PageRequest pageRequest, Long studentCode) {
        Page<Report> reportsPage;
        reportsPage = reportRepository.findReportsByStudentCode(keyword, pageRequest, studentCode);
        return reportsPage.map(ReportDetailResponse::fromReport);
    }

    @Override
    @Transactional
    public Report updateReportByLecturer(Long lecturerId, Long reportId, ReportDTO reportDTO) throws Exception {
        // Lấy report hiện tại
        Report existingReport = reportRepository.findById(reportId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find report with id: " + reportId));

        // Lấy giảng viên mới
        Lecturer existingLecturer = lecturerRepository.findById(lecturerId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find lecturer with id: " + lecturerId));

        // Cập nhật thông tin report
        if (reportDTO.getTitle() != null) {
            existingReport.setTitle(reportDTO.getTitle());
        }
        if (reportDTO.getDescription() != null) {
            existingReport.setDescription(reportDTO.getDescription());
        }
        if (reportDTO.getDueDate() != null) {
            existingReport.setDueDate(reportDTO.getDueDate());
        }

        existingReport.setLecturer(existingLecturer);

        // Lưu lại bản cập nhật trước khi xử lý student
        reportRepository.save(existingReport);

        // Xoá tất cả các ReportStudent cũ liên quan tới report này
        studentRepository.deleteById(reportId);

        // Tạo lại danh sách sinh viên mới
        for (Long studentId : reportDTO.getStudentCodes()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new DataNotFoundException("Cannot find student with id: " + studentId));

            ReportStudent reportStudent = ReportStudent.builder()
                    .report(existingReport)
                    .student(student)
                    .build();

            reportStudentRepository.save(reportStudent);
        }

        return existingReport;
    }



//    @Override
//    @Transactional
//    public Report updateReport(Long reportId, ReportDTO reportDTO) throws Exception {
//        Report existingReport = getReportById(reportId);
//        if(existingReport != null){
//            Lecturer existingLecturer = lecturerRepository.findById(reportDTO.getLecturerId())
//                    .orElseThrow(() -> new DataNotFoundException(
//                            "Cannot find lecturer with id : "+reportDTO.getLecturerId()));
//            existingReport.setTitle(reportDTO.getTitle());
//            existingReport.setDescription(reportDTO.getDescription());
//            existingReport.setDueDate(reportDTO.getDueDate());
//            existingReport.setLecturer(existingLecturer);
//            reportRepository.save(existingReport);
//        }
//        return null;
//    }

    @Override
    @Transactional
    public void deleteReport(Long reportId) {
        reportRepository.deleteById(reportId);
    }
}
