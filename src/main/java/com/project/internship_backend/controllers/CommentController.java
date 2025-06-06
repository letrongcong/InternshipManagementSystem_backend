package com.project.internship_backend.controllers;

import com.project.internship_backend.components.SecurityUtils;
import com.project.internship_backend.dtos.CommentDTO;
import com.project.internship_backend.entities.User;
import com.project.internship_backend.responses.ResponseObject;
import com.project.internship_backend.responses.comment.CommentResponse;
import com.project.internship_backend.services.comment.ICommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/comments")
public class CommentController {
    private final ICommentService commentService;
    private final SecurityUtils securityUtils;

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> getAllComments(
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam("submission_id") Long submissionId
    ) {
        List<CommentResponse> commentResponses;
        if (userId == null) {
            commentResponses = commentService.getCommentsBySubmission(submissionId);
        } else {
            commentResponses = commentService.getCommentsByUserAndSubmission(userId, submissionId);
        }
        return ResponseEntity.ok().body(ResponseObject.builder()
                .message("Get comments successfully")
                .status(HttpStatus.OK)
                .data(commentResponses)
                .build());
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> updateComment(
            @PathVariable("id") Long commentId,
            @Valid @RequestBody CommentDTO commentDTO
    ) throws Exception {
        User loginUser = securityUtils.getLoggedInUser();
        if (!Objects.equals(loginUser.getId(), commentDTO.getUserId())) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot update another user's comment",
                            HttpStatus.BAD_REQUEST,
                            null));

        }
        commentService.updateComment(commentId, commentDTO);
        return ResponseEntity.ok(
                new ResponseObject(
                        "Update comment successfully",
                        HttpStatus.OK, null));
    }
    @PostMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_STUDENT') " +
            "or hasRole('ROLE_LECTURER') or hasRole('ROLE_MENTOR')" )
    public ResponseEntity<ResponseObject> insertComment(
            @Valid @RequestBody CommentDTO commentDTO
    ) {
        // Insert the new comment
        User loginUser = securityUtils.getLoggedInUser();
        if(loginUser.getId() != commentDTO.getUserId()) {
            return ResponseEntity.badRequest().body(
                    new ResponseObject(
                            "You cannot comment as another user",
                            HttpStatus.BAD_REQUEST,
                            null));
        }
        commentService.insertComment(commentDTO);
        return ResponseEntity.ok(
                ResponseObject.builder()
                        .message("Insert comment successfully")
                        .status(HttpStatus.OK)
                        .build());
    }
}
