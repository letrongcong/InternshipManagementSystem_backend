package com.project.internship_backend.services.mentor;

import com.project.internship_backend.dtos.MentorDTO;
import com.project.internship_backend.entities.Mentor;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.mentor.MentorDetailResponse;
import com.project.internship_backend.responses.mentor.MentorResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

public interface IMentorService {
    Mentor createMentor(MentorDTO mentorDTO) throws Exception;
    Page<MentorDetailResponse> getAllMentors(String keyword, PageRequest pageRequest);
    MentorDetailResponse getMentorById(Long mentorId) throws Exception;
    MentorDetailResponse getMentorByToken(Long mentorId) throws Exception;
    Mentor updateMentor (Long mentorId, MentorDTO mentorDTO) throws Exception;
    void deleteMentor(Long mentorId);
}
