package com.example.root.medium;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by root on 27/12/17.
 */

public class Post {

    public  String uid,Userpic;
    public  String author;
    public  String title;
    public  String body,mSerialized;
    public int starCount = 0;
    public String key;
    public String imageurl = null;
    public Map<String, Boolean> stars = new HashMap<>();



    public Post(String uid, String author, String title, String body, String Userpic,String key,String imageurl,String mSerialized) {
        this.uid = uid;
        this.author = author;
        this.title = title;
        this.body = body;
        this.Userpic = Userpic;
        this.key = key;
        if(imageurl !=null)
        {
            this.imageurl = imageurl;
        }
        this.mSerialized = mSerialized;
    }
/*
    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("title", title);
        result.put("body", body);
        result.put("starCount", starCount);
        result.put("Userpic",Userpic);
        result.put("key",key);
        result.put("stars", stars);

        return result;
    }
*/
}
