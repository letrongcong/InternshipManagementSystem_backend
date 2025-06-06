package com.project.internship_backend.responses.mentor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Mentor;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorResponse {

    private Long id;

    @JsonProperty("mentor_name")
    private String mentorName;

    @JsonProperty("company_id")
    private Long companyId;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("position")
    private String position;

    @JsonProperty("user_id")
    private Long userId;
    public static MentorResponse fromMentor(Mentor mentor) {
        return MentorResponse.builder()
                .id(mentor.getId())
                .mentorName(mentor.getUser().getFullName())
                .companyId(mentor.getCompany().getId())
                .companyName(mentor.getCompany().getCompanyName())
                .position(mentor.getPosition())
                .userId(mentor.getUser().getId())
                .build();
    }
}
