package com.zeeplive.app.response;

import java.util.List;

public class BannerResponse {
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

        int id, status;
        String title, image, description, related, created_at, updated_at;

        public int getId() {
            return id;
        }

        public int getStatus() {
            return status;
        }

        public String getTitle() {
            return title;
        }

        public String getImage() {
            return image;
        }

        public String getDescription() {
            return description;
        }

        public String getRelated() {
            return related;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }
    }
}
