package com.project.internship_backend.responses.comment;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.project.internship_backend.entities.Comment;
import com.project.internship_backend.responses.BaseResponse;
import com.project.internship_backend.responses.user.UserResponse;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponse extends BaseResponse {
    @JsonProperty("id")
    private Long id;

    @JsonProperty("content")
    private String content;

    //user's information
    //@JsonProperty("user_id")
    //private Long userId;

    @JsonProperty("user")
    private UserResponse user;

    @JsonProperty("submission_id")
    private Long submissionId;

    public static CommentResponse fromComment(Comment comment) {
        UserResponse userResponse = UserResponse.fromUser(comment.getUser());
        CommentResponse result = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .user(userResponse)
                //.userId(comment.getUser().getId())
                .submissionId(comment.getSubmission().getId())
                //.userResponse(UserResponse.fromUser(comment.getUser()))
                //.createdAt(comment.getCreatedAt())
                .build();
        result.setCreatedAt(comment.getCreatedAt());
        return result;
    }
}
