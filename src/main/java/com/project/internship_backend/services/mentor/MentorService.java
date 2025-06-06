package com.project.internship_backend.services.mentor;

import com.project.internship_backend.dtos.MentorDTO;
import com.project.internship_backend.entities.Company;
import com.project.internship_backend.entities.Mentor;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.CompanyRepository;
import com.project.internship_backend.repositories.MentorRepository;
import com.project.internship_backend.repositories.UserRepository;
import com.project.internship_backend.responses.mentor.MentorDetailResponse;
import com.project.internship_backend.responses.mentor.MentorResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class MentorService implements IMentorService {
    private final MentorRepository mentorRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    public Mentor createMentor(
            MentorDTO mentorDTO) throws DataNotFoundException {
        //Kiểm tra xem user id có tồn tại hay không
        User existingUser = userRepository.findById(mentorDTO.getUserId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find user with id : "+ mentorDTO.getUserId()));
        Company existingCompany = companyRepository
                .findById(mentorDTO.getCompanyId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find company with id: "+ mentorDTO.getCompanyId()));
        Mentor newMentor = Mentor.builder()
                .company(existingCompany)
                .position(mentorDTO.getPosition())
                .user(existingUser)
                .build();
        return mentorRepository.save(newMentor);
    }

    @Override
    public MentorDetailResponse getMentorById(Long mentorId) throws DataNotFoundException {
        Mentor mentor = mentorRepository.findById(mentorId).orElseThrow(
                () -> new DataNotFoundException("Mentor not found")
        );
        return MentorDetailResponse.fromMentorDetail(mentor);
    }

    @Override
    public MentorDetailResponse getMentorByToken(Long userId) throws Exception {
        Mentor mentor = mentorRepository.findByUserId(userId).orElseThrow(
                () -> new DataNotFoundException("Mentor not found")
        );
        return MentorDetailResponse.fromMentorDetail(mentor);
    }

    @Override
    public Page<MentorDetailResponse> getAllMentors(String keyword, PageRequest pageRequest) {
        Page<Mentor> mentorsPage;
        mentorsPage = mentorRepository.findAllMentors(keyword, pageRequest);
        return mentorsPage.map(MentorDetailResponse::fromMentorDetail);
    }

    @Override
    public Mentor updateMentor (
            Long mentorId, MentorDTO mentorDTO) throws DataNotFoundException{

        Mentor existingMentor = mentorRepository.findById(mentorId)
                .orElseThrow(() -> new DataNotFoundException("Cannot find mentor with id: "+mentorId));
        Company existingCompany = companyRepository
                .findById(mentorDTO.getCompanyId())
                .orElseThrow(() -> new DataNotFoundException(
                        "Cannot find company with id: "+ mentorDTO.getCompanyId()));
        existingMentor.setCompany(existingCompany);
        existingMentor.setPosition(mentorDTO.getPosition());
        return mentorRepository.save(existingMentor);
    }

    @Override
    public void deleteMentor(Long mentorId) {
        mentorRepository.deleteById(mentorId);
    }
}
