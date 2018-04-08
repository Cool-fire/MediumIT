package com.example.root.medium;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 29/12/17.
 */

public class Blog {

    public String imageurl;
    public String author,body,title,uid,Userpic,key,mSerialized;
    public int starCount;
    public Map<String, Boolean> stars = new HashMap<>();




    public Blog() {

    }

    public Map<String, Boolean> getStars() {
        return stars;
    }

    public void setStars(Map<String, Boolean> stars) {
        this.stars = stars;
    }

    public Blog(String author, String body, String title, String uid, int starCount, String Userpic, String key, Map<String, Boolean> stars,String imageurl,String mSerialized ) {
        this.author = author;
        this.body = body;
        this.uid = uid;
        this.key = key;
        this.starCount = starCount;
        this.title = title;
        this.Userpic=Userpic;
        if(imageurl=="noImage")
        {
            this.imageurl = imageurl;
        }

        this.stars = stars;
        this.mSerialized  = mSerialized;
    }

    public String getmSerialized() {
        return mSerialized;
    }

    public void setmSerialized(String mSerialized) {
        this.mSerialized = mSerialized;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStarCount() {
        return starCount;
    }

    public void setStarCount(int starCount) {
        this.starCount = starCount;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
