package com.org.example.model;

public class FakeToken {
    public String Token;

    public boolean expired;

    public FakeToken() {
    }

    public FakeToken(boolean expired) {
        this.expired = expired;
    }
}
