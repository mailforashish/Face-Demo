package com.zeeplive.app.response;

import java.util.List;

public class RecentRechargeResponse {

    boolean success;
    List<Result> result;
    String error;



    public boolean isSuccess() {
        return success;
    }

    public List<RecentRechargeResponse.Result> getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public static class Result {
        int points, status;
        UserDetails user_id;
        String razorpay_id, transaction_des;

        public int getPoints() {
            return points;
        }

        public int getStatus() {
            return status;
        }

        public UserDetails getUser_id() {
            return user_id;
        }

        public String getRazorpay_id() {
            return razorpay_id;
        }

        public String getTransaction_des() {
            return transaction_des;
        }


    }

    public static class UserDetails {
        int id, profile_id;
        String name;
        List<PicDetails> profile_images;
        List<favorite> favorite;

        public int getId() {
            return id;
        }

        public int getProfile_id() {
            return profile_id;
        }

        public String getName() {
            return name;
        }

        public List<PicDetails> getProfile_images() {
            return profile_images;
        }

        public List<favorite> getFavorite() {
            return favorite;
        }

        public void setFavorite(List<favorite> favorite) {
            this.favorite = favorite;
        }
    }

    public static class PicDetails {
        int id, user_id;
        String image_name;

        public int getId() {
            return id;
        }

        public int getUser_id() {
            return user_id;
        }

        public String getImage_name() {
            return image_name;
        }
    }

    public static class favorite {
        int id;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }
    }
}