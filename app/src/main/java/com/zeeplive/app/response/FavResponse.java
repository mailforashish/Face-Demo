package com.zeeplive.app.response;

import java.util.List;

public class FavResponse {

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
        int id, favorite_id, points;
        List<Favourite> favorites;

        public int getId() {
            return id;
        }

        public int getFavorite_id() {
            return favorite_id;
        }

        public List<Favourite> getFavorites() {
            return favorites;
        }

        public int getPoints() {
            return points;
        }

        public void setPoints(int points) {
            this.points = points;
        }
    }


    public static class Favourite {
        int id, profile_id, call_rate, is_online;
        String name, email, mobile, gender, dob, about_user;
        List<UserListResponse.UserPics> profile_images;


        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getMobile() {
            return mobile;
        }

        public String getGender() {
            return gender;
        }

        public int getProfile_id() {
            return profile_id;
        }

        public int getCall_rate() {
            return call_rate;
        }

        public int getIs_online() {
            return is_online;
        }

        public String getDob() {
            return dob;
        }

        public String getAbout_user() {
            return about_user;
        }

        public List<UserListResponse.UserPics> getProfile_images() {
            return profile_images;
        }
    }
}
