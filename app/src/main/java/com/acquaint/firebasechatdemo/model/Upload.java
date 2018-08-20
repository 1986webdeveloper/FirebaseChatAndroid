package com.acquaint.firebasechatdemo.model;

/**
 * Created by acquaint on 27/7/18.
 */

public class Upload {
    public String name;
    public String url;


    public Upload() {
    }

    public Upload(String url, String name) {
        this.url = url;
        this.name = name;

    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Upload(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
