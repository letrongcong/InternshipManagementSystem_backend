package com.project.internship_backend.services.comment;

import com.project.internship_backend.dtos.CommentDTO;
import com.project.internship_backend.entities.Comment;
import com.project.internship_backend.entities.Submission;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.exceptions.DataNotFoundException;
import com.project.internship_backend.repositories.CommentRepository;
import com.project.internship_backend.repositories.SubmissionRepository;
import com.project.internship_backend.repositories.UserRepository;
import com.project.internship_backend.responses.comment.CommentResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService implements ICommentService {
    private static final Logger logger = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final SubmissionRepository submissionRepository;

    @Override
    @Transactional
    public Comment insertComment(CommentDTO commentDTO) {
        User user = userRepository.findById(commentDTO.getUserId()).orElse(null);
        Submission submission = submissionRepository.findById(commentDTO.getSubmissionId()).orElse(null);
        if (user == null || submission == null) {
            throw new IllegalArgumentException("User or product not found");
        }
        Comment newComment = Comment.builder()
                .user(user)
                .submission(submission)
                .content(commentDTO.getContent())
                .build();
        return commentRepository.save(newComment);
    }

    @Override
    @Transactional
    public void updateComment(Long id, CommentDTO commentDTO) throws DataNotFoundException {
        Comment existingComment = commentRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException("Comment not found"));
        existingComment.setContent(commentDTO.getContent());
        commentRepository.save(existingComment);
    }

    @Override
    @Transactional
    public List<CommentResponse> getCommentsByUserAndSubmission(Long userId, Long submissionId) {
        List<Comment> comments = commentRepository.findByUserIdAndSubmissionId(userId, submissionId);
        return comments.stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<CommentResponse> getCommentsBySubmission(Long productId) {
        List<Comment> comments = commentRepository.findBySubmissionId(productId);
        return comments.stream()
                .map(CommentResponse::fromComment)
                .collect(Collectors.toList());
    }
}
