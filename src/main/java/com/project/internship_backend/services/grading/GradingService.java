package com.project.internship_backend.services.grading;

import com.project.internship_backend.dtos.GradingDTO;
import com.project.internship_backend.entities.Grading;
import com.project.internship_backend.entities.Lecturer;
import com.project.internship_backend.entities.Submission;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.GradingRepository;
import com.project.internship_backend.repositories.LecturerRepository;
import com.project.internship_backend.repositories.SubmissionRepository;
import com.project.internship_backend.services.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class GradingService implements IGradingService{
    private final EmailService emailService;
    private final LecturerRepository lecturerRepository;
    private final SubmissionRepository submissionRepository;
    private final GradingRepository gradingRepository;

    @Override
    public Grading createGrading(Long lecturerId, Long submissionId, GradingDTO gradingDTO) throws DataNotFoundException {
        // Lấy thông tin giảng viên
        Lecturer existingLecturer = lecturerRepository
                .findById(lecturerId)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("Cannot find lecturer with id: %d", lecturerId)
                ));

        // Lấy thông tin bài nộp
        Submission existingSubmission = submissionRepository
                .findById(submissionId)
                .orElseThrow(() -> new DataNotFoundException(
                        String.format("Cannot find submission with id: %d", submissionId)
                ));

        // Tạo kết quả chấm điểm mới
        Grading newGrading = Grading.builder()
                .lecturer(existingLecturer)
                .submission(existingSubmission)
                .grade(gradingDTO.getGrade())
                .feedback(gradingDTO.getFeedback())
                .build();

        Grading savedGrading = gradingRepository.save(newGrading);

        // Gửi email thông báo tới sinh viên
        try {
            String studentEmail = existingSubmission.getStudent().getUser().getEmail();
            String subject = "Grading Result for Your Submission";
            String content = String.format(
                    "Dear %s,<br><br>Your submission for report ID: %d has been graded.<br>" +
                            "Grade: %s<br>Feedback: %s<br><br>Best regards,<br>Internship System",
                    existingSubmission.getStudent().getUser().getFullName(),
                    existingSubmission.getReport().getId(),
                    gradingDTO.getGrade(),
                    gradingDTO.getFeedback()
            );
            emailService.sendEmail(studentEmail, subject, content);
        } catch (Exception e) {
            // Log lỗi gửi email nhưng không làm gián đoạn quy trình
            System.err.println("Error sending email: " + e.getMessage());
        }

        return savedGrading;
    }

    @Override
    public Grading getGradingBySubmissionId(Long submissionId) throws DataNotFoundException {
        Optional<Grading> optionalGrading = gradingRepository.findBySubmissionId(submissionId);
        if(optionalGrading.isPresent()) {
            return optionalGrading.get();
        }
        throw new DataNotFoundException("Cannot find report with id =" + submissionId);
    }

    @Override
    public Grading getGradingById(Long gradingId) throws DataNotFoundException {
        Optional<Grading> optionalGrading = gradingRepository.findById(gradingId);
        if(optionalGrading.isPresent()) {
            return optionalGrading.get();
        }
        throw new DataNotFoundException("Cannot find report with id =" + gradingId);
    }

    @Override
    public Grading updateGrading(Long gradingId, GradingDTO gradingDTO) throws DataNotFoundException {
        Grading existingGrading = getGradingById(gradingId);
        if (existingGrading != null) {
            existingGrading.setGrade(gradingDTO.getGrade());
            existingGrading.setFeedback(gradingDTO.getFeedback());
            gradingRepository.save(existingGrading);
        }
        return null;
    }
}
