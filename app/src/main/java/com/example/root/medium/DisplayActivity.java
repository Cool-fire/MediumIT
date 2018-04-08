package com.example.root.medium;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.Editor;
import com.github.irshulx.EditorListener;
import com.github.irshulx.models.EditorContent;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class DisplayActivity extends AppCompatActivity {

    private static final String TAG = "DisplayActivity";
    private ImageView ppic;
    private Blog blog;
    private TextView author;
    private ImageButton back;
    private TextView Title;
    private Editor body;
    private RelativeLayout spinner;
    private DatabaseReference mDatabase;
    private int flag = 0;
    private FirebaseAuth mAuth;
    private String uid;
    private FloatingActionButton fab;
    private ImageView pic;
    private String blogKey;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();

    fab = (FloatingActionButton) findViewById(R.id.fab);
       // fab.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mAuth.getCurrentUser()!=null)
                {
                    onStarClicked(mDatabase);
                }
                else
                {
                    Toast.makeText(getApplicationContext(),"please SignIn to upvote",Toast.LENGTH_SHORT).show();
                }



            }
        });
        getData();

        back = (ImageButton)findViewById(R.id.back_bttn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void onStarClicked(DatabaseReference mDatabase) {
        if(mDatabase!=null) {
            mDatabase.runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    Blog p = mutableData.getValue(Blog.class);
                    if (p == null) {
                        return Transaction.success(mutableData);
                    } else if (!p.stars.containsKey(getUid())) {
                        p.starCount = p.starCount + 1;
                        p.stars.put(getUid(), true);
                        DisplayActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                fab.setImageResource(R.mipmap.hifi1);
                            }
                        });

                        mutableData.setValue(p);

                    } else {
                        DisplayActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(DisplayActivity.this, "Already Upvoted", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                    Log.d(TAG, "postTransaction:onComplete:" + databaseError);
                }
            });
        }
    }

    private void getData() {
        Intent intent = getIntent();
        try
        {
            Gson gson = new Gson();
            String strObj = getIntent().getStringExtra("obj");
            blog = gson.fromJson(strObj, Blog.class);
            Log.d(TAG, "getData: "+blog.key);
            Log.d(TAG, "getData: "+blog.imageurl);
            init(blog);

        }
        catch (Exception e)
        {
            Log.d(TAG, "getData: "+e);
        }


    }

    private void init(Blog blog1) {
        spinner = (RelativeLayout)findViewById(R.id.loadingPanel);
        ppic = (ImageView)findViewById(R.id.ppic);
        author = (TextView)findViewById(R.id.author);
        Title = (TextView)findViewById(R.id.title);
        body = (Editor)findViewById(R.id.body);
        pic = (ImageView)findViewById(R.id.pic);

        spinner.setVisibility(View.GONE);

        Picasso.with(getApplicationContext()).load(this.blog.Userpic.toString()).resize(75,75).transform(new CropCircleTransformation()).into(ppic);
        author.setText(capitalize(this.blog.author.toLowerCase()));
        Title.setText(this.blog.title);
        body.setDividerLayout(R.layout.tmpl_divider_layout);
        body.setEditorImageLayout(R.layout.tmpl_image_view);
        body.setListItemLayout(R.layout.tmpl_list_item);
        body.setNormalTextSize(20);
        body.setFontFace(R.font.sahitya_bold);

        String content= this.blog.mSerialized;
        EditorContent Deserialized= body.getContentDeserialized(content);
        body.render(Deserialized);
        Picasso.with(getApplicationContext()).load(this.blog.imageurl.toString()).into(pic);
        mDatabase = FirebaseDatabase.getInstance().getReference("/posts/"+ this.blog.key);
        ValueEventListener valueListner = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Blog blog = dataSnapshot.getValue(Blog.class);
                if (blog.stars.containsKey(getUid())) {
                    fab.setImageResource(R.mipmap.hifi1);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabase.addListenerForSingleValueEvent(valueListner);
        mDatabase.removeEventListener(valueListner);

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

    public String getUid() {
        uid = mAuth.getUid();
        return uid;
    }
}
