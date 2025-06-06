package com.project.internship_backend.services.submission;

import com.project.internship_backend.dtos.SubmissionDTO;
import com.project.internship_backend.entities.Submission;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.submission.SubmissionDetailResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface ISubmissionService {
    Submission createSubmission(String file, String note, Long reportId, Long studentCode) throws DataNotFoundException;
    Page<SubmissionDetailResponse> getAllSubmissions( PageRequest pageRequest);
    Page<SubmissionDetailResponse> getAllSubmissionsByStudentCodeAndReportId(Long studentCode, Long reportId, String keyword, PageRequest pageRequest);
    Page<SubmissionDetailResponse> getAllSubmissionsByReport(Long reportId, String keyword, PageRequest pageRequest);
    Page<SubmissionDetailResponse> getAllSubmissionsByStudent(Long studentCode, String keyword, PageRequest pageRequest);
    Submission updateSubmissionByStudent(Long submissionId, Long studentCode, SubmissionDTO submissionDTO) throws DataNotFoundException;
}
