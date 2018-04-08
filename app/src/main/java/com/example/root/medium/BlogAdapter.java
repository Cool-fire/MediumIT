package com.example.root.medium;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by root on 29/12/17.
 */

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.Viewholder>{
    private static final String TAG = "BlogAdapter" ;
    private final List<Blog> blogList;
    private final Context context;
    private final String query;
    private int flag =0;


    public BlogAdapter(Context context, List<Blog> blogList, String query) {
        this.context = context;
        this.blogList = blogList;
        this.query = query;
    }

    @Override
    public Viewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(query=="userposts")
        {
            View ItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_post_card,parent,false);
            flag = 1;
            return new Viewholder(ItemView);
        }
        else
        {
            View ItemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_row_list,parent,false);
            return new Viewholder(ItemView);
        }


    }

    @Override
    public void onBindViewHolder(Viewholder holder, int position) {


        final Blog blog = blogList.get(position);
        holder.title.setText(blog.getTitle().toString());
        holder.body.setText(blog.getBody().toString());
        final String author =capitalize(blog.author.toString().toLowerCase());
        holder.author_text.setText(author);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
               Intent intent = new Intent(v.getContext(),DisplayActivity.class);
                intent.putExtra("obj", gson.toJson(blog));
                Log.d(TAG, "onClick: "+blog.imageurl);
                v.getContext().startActivity(intent);
            }
        });


       try
        {
            Picasso.with(context).load(blog.Userpic.toString()).transform(new CropCircleTransformation()).into(holder.PimageView);

            if(blog.imageurl.toString() != "no_image")
            {
                if(flag == 1)
                {
                    Glide.with(context).load(blog.imageurl).fitCenter().into(holder.pic1);
                   // Picasso.with(context).load(blog.imageurl.toString()).fit().centerCrop().into(holder.pic1);
                }
                Glide.with(context).load(blog.imageurl).centerCrop().crossFade(500).into(holder.pic);
                //Picasso.with(context).load(blog.imageurl.toString()).centerCrop().resize(220,220).into(holder.pic);
            }

        }
        catch (Exception e)
        {

        }

    }

    private String capitalize(String str) {
        String[] words = str.trim().split(" ");
        StringBuilder ret = new StringBuilder();
        for(int i = 0; i < words.length; i++)
        {
            if(words[i].trim().length() > 0)
            {
                ret.append(Character.toUpperCase(words[i].trim().charAt(0)));
                ret.append(words[i].trim().substring(1));
                if(i < words.length - 1) {
                    ret.append(' ');
                }
            }
        }

        return ret.toString();
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {
        private final TextView title,body,author_text;
        private final ImageView PimageView;
        private final RelativeLayout card;
        private final ImageView pic;
        private final ImageView pic1;
        public View itemView;

        public Viewholder( View itemView) {
            super(itemView);
            this.itemView = itemView;
            title = (TextView)itemView.findViewById(R.id.title1);
            body = (TextView)itemView.findViewById(R.id.body1);
            PimageView = (ImageView)itemView.findViewById(R.id.image1);
            author_text=(TextView)itemView.findViewById(R.id.author_field);
            card = (RelativeLayout)itemView.findViewById(R.id.blog_layout);
            pic = (ImageView)itemView.findViewById(R.id.image_set);
            pic1 = (ImageView)itemView.findViewById(R.id.image_set1);
        }
    }
}
