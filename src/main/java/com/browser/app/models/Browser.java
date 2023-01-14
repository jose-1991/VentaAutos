package com.browser.app.models;

import java.util.List;

public class Browser {
    private String name;
    private String splitByKeyword;
    private String additionalKeyword;
    private String additionalParsing;
    private List<Condition> conditions;

    public Browser() {
    }

    public Browser(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Browser(String name) {
        this.name = name;
    }

    public Browser(String name, String splitByKeyword) {
        this.name = name;
        this.splitByKeyword = splitByKeyword;
    }



    public Browser(String name, String splitByKeyword, List<Condition> conditions) {
        this.name = name;
        this.splitByKeyword = splitByKeyword;
        this.conditions = conditions;
    }

    public Browser(String name, String splitByKeyword, String additionalKeyword) {
        this.name = name;
        this.splitByKeyword = splitByKeyword;
        this.additionalKeyword = additionalKeyword;
    }

    public Browser(String name, String splitByKeyword, String additionalParsing, List<Condition> conditions) {
        this.name = name;
        this.splitByKeyword = splitByKeyword;
        this.additionalParsing = additionalParsing;
        this.conditions = conditions;
    }

    public Browser(String name, String splitByKeyword, String additionalKeyword, String additionalParsing) {
        this.name = name;
        this.splitByKeyword = splitByKeyword;
        this.additionalKeyword = additionalKeyword;
        this.additionalParsing = additionalParsing;
    }

    public Browser(String name, String splitByKeyword, String additionalKeyword, String additionalParsing, List<Condition> conditions) {
        this.name = name;
        this.splitByKeyword = splitByKeyword;
        this.additionalKeyword = additionalKeyword;
        this.additionalParsing = additionalParsing;
        this.conditions = conditions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSplitByKeyword() {
        return splitByKeyword;
    }

    public void setSplitByKeyword(String splitByKeyword) {
        this.splitByKeyword = splitByKeyword;
    }

    public String getAdditionalKeyword() {
        return additionalKeyword;
    }

    public void setAdditionalKeyword(String additionalKeyword) {
        this.additionalKeyword = additionalKeyword;
    }

    public String getAdditionalParsing() {
        return additionalParsing;
    }

    public void setAdditionalParsing(String additionalParsing) {
        this.additionalParsing = additionalParsing;
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public void setConditions(List<Condition> conditions) {
        this.conditions = conditions;
    }
}
