package com.project.internship_backend.services.comment;

import com.project.internship_backend.dtos.CommentDTO;
import com.project.internship_backend.entities.Comment;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.responses.comment.CommentResponse;

import java.util.List;

public interface ICommentService {
    Comment insertComment(CommentDTO comment);

    //void deleteComment(Long commentId);
    void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException;

    List<CommentResponse> getCommentsByUserAndSubmission(Long userId, Long submissionId);
    List<CommentResponse> getCommentsBySubmission(Long productId);
}
