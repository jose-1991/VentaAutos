package com.browser.app.models;

public class Condition {
    private String keyword;
    private boolean shouldContain;
    private boolean isOptional;

    public Condition() {
    }

    public Condition(String keyword) {
        this.keyword = keyword;
    }

    public Condition(String keyword, boolean shouldContain) {
        this.keyword = keyword;
        this.shouldContain = shouldContain;
    }

    public Condition(boolean isOptional, String keyword) {
        this.keyword = keyword;
        this.isOptional = isOptional;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public boolean isShouldContain() {
        return shouldContain;
    }

    public void setShouldContain(boolean shouldContain) {
        this.shouldContain = shouldContain;
    }

    public boolean isOptional() {
        return isOptional;
    }

    public void setOptional(boolean optional) {
        isOptional = optional;
    }
}
