package com.project.internship_backend.services.grading;

import com.project.internship_backend.dtos.GradingDTO;
import com.project.internship_backend.entities.Grading;
import com.project.internship_backend.exceptions.DataNotFoundException;

public interface IGradingService {
    Grading createGrading(Long lecturerId, Long submissionId, GradingDTO gradingDTO) throws DataNotFoundException;

    Grading getGradingBySubmissionId(Long submissionId) throws DataNotFoundException;

    Grading getGradingById(Long gradingId) throws Exception;

    Grading updateGrading(Long gradingId, GradingDTO gradingDTO) throws DataNotFoundException;
}
