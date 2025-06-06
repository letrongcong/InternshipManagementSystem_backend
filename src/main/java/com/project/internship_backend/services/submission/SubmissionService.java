package com.project.internship_backend.services.submission;

import com.project.internship_backend.dtos.SubmissionDTO;
import com.project.internship_backend.entities.Report;
import com.project.internship_backend.entities.Student;
import com.project.internship_backend.entities.Submission;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.ReportRepository;
import com.project.internship_backend.repositories.StudentRepository;
import com.project.internship_backend.repositories.SubmissionRepository;
import com.project.internship_backend.responses.submission.SubmissionDetailResponse;
import com.project.internship_backend.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class SubmissionService implements ISubmissionService{
    private final EmailService emailService;
    private final ReportRepository reportRepository;
    private final StudentRepository studentRepository;
    private final SubmissionRepository submissionRepository;

    @Override
    public Submission createSubmission(String file, String note, Long reportId, Long studentCode) throws DataNotFoundException {
        // Kiểm tra báo cáo tồn tại
        Report existingReport = reportRepository
                .findById(reportId)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("Cannot find report with id: %d", reportId)
                ));

        // Kiểm tra sinh viên tồn tại
        Student existingStudent = studentRepository
                .findById(studentCode)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("Cannot find student with id: %d", studentCode)
                ));

        // Tạo submission mới
        Submission newSubmission = Submission.builder()
                .report(existingReport)
                .student(existingStudent)
                .file(file)
                .note(note)
                .submissionDate(LocalDateTime.now())
                .build();

        Submission savedSubmission = submissionRepository.save(newSubmission);

        // Gửi email tới giáo viên của sinh viên
        try {
            String lecturerEmail = existingStudent.getLecturer().getUser().getEmail();
            String subject = "New Submission from " + existingStudent.getUser().getFullName();
            String content = String.format(
                    "Dear %s,<br><br>A new submission has been made by %s for the report ID: %d.<br>File: %s<br><br>Best regards,<br>Internship System",
                    existingStudent.getLecturer().getUser().getFullName(),
                    existingStudent.getUser().getFullName(),
                    reportId,
                    file
            );
            emailService.sendEmail(lecturerEmail, subject, content);
        } catch (Exception e) {
            // Log lỗi gửi email nhưng không làm gián đoạn quy trình
            System.err.println("Error sending email: " + e.getMessage());
        }

        return savedSubmission;
    }

    @Override
    public Page<SubmissionDetailResponse> getAllSubmissions( PageRequest pageRequest) {
        Page<Submission> submissionsPage;
        submissionsPage = submissionRepository.findAllSubmissions(pageRequest);
        return submissionsPage.map(SubmissionDetailResponse::fromSubmissionDetailResponse);
    }

    @Override
    public Page<SubmissionDetailResponse> getAllSubmissionsByStudentCodeAndReportId(Long studentCode, Long reportId, String keyword, PageRequest pageRequest) {
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = null; // Không có từ khóa => hiển thị toàn bộ.
        }
        Page<Submission> submissionPage;
        submissionPage = submissionRepository.getSubmissionsByStudentCodeAndReportId(studentCode, reportId, keyword, pageRequest);
        return submissionPage.map(SubmissionDetailResponse::fromSubmissionDetailResponse);
    }

    @Override
    public Page<SubmissionDetailResponse> getAllSubmissionsByReport(Long reportId, String keyword, PageRequest pageRequest) {
        Page<Submission> submissionsPage;
        submissionsPage = submissionRepository.findSubmissionsByReport(reportId, keyword, pageRequest);
        return submissionsPage.map(SubmissionDetailResponse::fromSubmissionDetailResponse);
    }

    @Override
    public Page<SubmissionDetailResponse> getAllSubmissionsByStudent(Long studentCode,
                                                                     String keyword, PageRequest pageRequest) {
        if (keyword == null || keyword.trim().isEmpty()) {
            keyword = null; // Không có từ khóa => hiển thị toàn bộ.
        }
        Page<Submission> submissionPage;
        submissionPage = submissionRepository.getSubmissionsByStudent(studentCode, keyword, pageRequest);
        return submissionPage.map(SubmissionDetailResponse::fromSubmissionDetailResponse);
    }

    @Override
    public Submission updateSubmissionByStudent(Long submissionId, Long studentCode, SubmissionDTO submissionDTO) throws DataNotFoundException {
        return null;
    }
}
