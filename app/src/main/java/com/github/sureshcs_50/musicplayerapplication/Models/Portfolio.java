package com.github.sureshcs_50.musicplayerapplication.Models;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by adminaccount on 20/12/17.
 */

public class Portfolio {

    private String key;
    private String value;
    private String link;

    public Portfolio(String key, String value, String link) {
        this.key = key;
        this.value = value;
        this.link = link;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    public String getLink() {
        return link;
    }

    public static List<Portfolio> getDetails() {
        return new ArrayList<>();;
    }

}
