package com.zeeplive.app.response;

import java.util.List;

public class ViewTicketResponse {

    boolean success;
    List<Result> result;
    String error;

    public boolean isSuccess() {
        return success;
    }

    public List<Result> getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public static class Result {

        int id, status, profile_id;
        long ticket_no, mobile;
        String description, response, created_at, updated_at, issue_heading;
        UserId user_id;

        public int getId() {
            return id;
        }

        public int getStatus() {
            return status;
        }

        public int getProfile_id() {
            return profile_id;
        }

        public long getTicket_no() {
            return ticket_no;
        }

        public long getMobile() {
            return mobile;
        }

        public String getDescription() {
            return description;
        }

        public String getResponse() {
            return response;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getIssue_heading() {
            return issue_heading;
        }

        public UserId getUser_id() {
            return user_id;
        }
    }

    public static class UserId {
        int id, profile_id;
        String name, username;
        long mobile;

        public int getId() {
            return id;
        }

        public int getProfile_id() {
            return profile_id;
        }

        public String getName() {
            return name;
        }

        public String getUsername() {
            return username;
        }

        public long getMobile() {
            return mobile;
        }
    }
}
