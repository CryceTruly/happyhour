package com.crycetruly.happyhour.model;

/**
 * Created by Elia on 26/04/2018.
 */

public class HappyHour {
    public String startTime, endTime, ownername, idd, owneridd, category, showsIn;
    public Double lat, lng;
    long posted;
    public long sortDate;

    public String email;
    public String description;
    public String title;
    public Boolean allowcomments;
    public String timezone;

    public HappyHour(String startTime, String endTime, String ownername, String idd, String owneridd, String category, String showsIn, Double lat, Double lng, long posted,
                     long sortDate, String email, String description, String title, Boolean allowcomments, String timezone) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.ownername = ownername;
        this.idd = idd;
        this.owneridd = owneridd;
        this.category = category;
        this.showsIn = showsIn;
        this.lat = lat;
        this.lng = lng;
        this.posted = posted;
        this.sortDate = sortDate;
        this.email = email;
        this.description = description;
        this.title = title;
        this.allowcomments = allowcomments;
        this.timezone = timezone;
    }

    public long getSortDate() {
        return sortDate;
    }

    public void setSortDate(long sortDate) {
        this.sortDate = sortDate;
    }

    public String getShowsIn() {
        return showsIn;
    }

    public void setShowsIn(String showsIn) {
        this.showsIn = showsIn;
    }

    public long getPosted() {
        return posted;
    }

    public void setPosted(long posted) {
        this.posted = posted;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getOwnername() {
        return ownername;
    }

    public void setOwnername(String ownername) {
        this.ownername = ownername;
    }

    public String getIdd() {
        return idd;
    }

    public void setIdd(String idd) {
        this.idd = idd;
    }

    public String getOwneridd() {
        return owneridd;
    }

    public void setOwneridd(String owneridd) {
        this.owneridd = owneridd;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public Boolean getAllowcomments() {
        return allowcomments;
    }

    public void setAllowcomments(Boolean allowcomments) {
        this.allowcomments = allowcomments;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public HappyHour() {
    }
}

