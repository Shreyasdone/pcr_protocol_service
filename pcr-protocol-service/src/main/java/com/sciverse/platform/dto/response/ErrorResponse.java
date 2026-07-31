package com.sciverse.platform.dto.response;

import java.time.Instant;
import java.util.List;

public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> details;

    public ErrorResponse() {}

    public ErrorResponse(Instant timestamp, int status, String error, String message, List<FieldError> details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.details = details;
    }

    public Instant getTimestamp() { return timestamp; }
    public int getStatus() { return status; }
    public String getError() { return error; }
    public String getMessage() { return message; }
    public List<FieldError> getDetails() { return details; }

    public static class FieldError {
        private String field;
        private String issue;

        public FieldError() {}

        public FieldError(String field, String issue) {
            this.field = field;
            this.issue = issue;
        }

        public String getField() { return field; }
        public String getIssue() { return issue; }
    }
}
