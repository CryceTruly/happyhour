package com.crycetruly.happyhour.model;

/**
 * Created by Elia on 17/06/2018.
 */

public class Business extends User {
    private String[]emails;
    public Business(String email) {
        super(email);
    }

    public Business(String email, String[] emails) {
        super(email);
        this.emails = emails;
    }

    public Business(String[] emails) {
        this.emails = emails;
    }

    public Business() {
    }


}
