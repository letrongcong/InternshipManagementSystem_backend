package com.project.internship_backend.responses.mentor;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Mentor;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.responses.user.UserResponse;
import lombok.*;

@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MentorDetailResponse {
    private Long id;

    @JsonProperty("company_name")
    private String companyName;

    @JsonProperty("position")
    private String position;

    @JsonProperty("user_response")
    private UserResponse userResponse;

    public static MentorDetailResponse fromMentorDetail(Mentor mentor) {
        User user = mentor.getUser();
        UserResponse userResponse = UserResponse.fromUser(user);  // Tạo đối tượng UserResponse từ User

        MentorDetailResponse mentorDetailResponse = MentorDetailResponse.builder()
                .id(mentor.getId())
                .companyName(mentor.getCompany().getCompanyName())
                .position(mentor.getPosition())
                .userResponse(userResponse)
                .build();
        return mentorDetailResponse;
    }
}
