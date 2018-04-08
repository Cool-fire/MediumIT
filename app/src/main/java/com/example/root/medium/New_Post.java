package com.example.root.medium;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.irshulx.Editor;
import com.github.irshulx.models.EditorContent;
import com.github.irshulx.models.EditorTextStyle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.richeditor.RichEditor;

public class New_Post extends AppCompatActivity {
    private static final String TAG ="newpost" ;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText mTitleField;

    private TextView mPublish;
    private ImageView clearbttn;
    private FloatingActionButton mSubmitButton;
    private ImageButton addImageBttn;
    private ImageView addImageView;
    private StorageReference mStorage;
    private Uri imageUri;
    private Editor editor;
  //  private RichEditor mEditor;
    private ImageButton mBoldBttn;
    private String body;
    private String mSerialized;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new__post);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mTitleField = (EditText)findViewById(R.id.field_title);

        mPublish = (TextView)findViewById(R.id.publish);
        addImageBttn = (ImageButton)findViewById(R.id.image_add);
        addImageView = (ImageView)findViewById(R.id.add_image);
        editor = findViewById(R.id.editor);
        editor.render();
        setupEditor();


        mStorage = FirebaseStorage.getInstance().getReference();
      /*  mSubmitButton = (FloatingActionButton)findViewById(R.id.fab_submit_post);
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
                
            }
        });*/
      mPublish.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              submit();

          }
      });
       clearbttn = (ImageView) findViewById(R.id.clear_bttn);
       clearbttn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
       addImageBttn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               getImage();
           }
       });


    }

    private void setupEditor() {

        editor.setFontFace(R.font.sahitya_bold);
        findViewById(R.id.action_h1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H1);
            }
        });
        findViewById(R.id.action_h2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H2);
            }
        });

        findViewById(R.id.action_h3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.H3);
            }
        });

        findViewById(R.id.action_bold).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.BOLD);
            }
        });

        findViewById(R.id.action_Italic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.ITALIC);
            }
        });

        findViewById(R.id.action_indent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.INDENT);
            }
        });

        findViewById(R.id.action_outdent).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.updateTextStyle(EditorTextStyle.OUTDENT);
            }
        });

        findViewById(R.id.action_bulleted).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(false);
            }
        });

        findViewById(R.id.action_unordered_numbered).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertList(true);
            }
        });

        findViewById(R.id.action_hr).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.insertDivider();
            }
        });
        findViewById(R.id.action_erase).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.clearAllContents();
            }
        });



    }

    private void getImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Please Select An Image"),0);
    }

    private void submit() {

        final String Title = mTitleField.getText().toString();
        mSerialized  = editor.getContentAsSerialized();
        String HtmlBody = editor.getContentAsHTML();
        Document document = Jsoup.parse(HtmlBody);
        body = document.text();
        Log.d(TAG, "submit: "+body);

        if(!TextUtils.isEmpty(Title) && !TextUtils.isEmpty(body))
        {
            disablefabbutton(false);

            final String userId = mAuth.getCurrentUser().getUid();
            createPost(userId, mAuth.getCurrentUser().getDisplayName(), Title, body);
            disablefabbutton(true);

        }

        if(TextUtils.isEmpty(Title))
        {
            mTitleField.setError("Title is required");
            disablefabbutton(true);

        }

        if(TextUtils.isEmpty(body))
        {

            Toast.makeText(getApplicationContext(),"body is Empty",Toast.LENGTH_SHORT).show();
            disablefabbutton(true);
        }

    }

    private void createPost(final String userId, final String username, final String title, final String body) {
         final String key = mDatabase.child("posts").push().getKey();

        if(imageUri!=null)
        {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading");
        progressDialog.show();

            StorageReference storageReference = mStorage.child("images/"+mAuth.getCurrentUser().getDisplayName()+"/"+key);
            storageReference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Post post = new Post(userId, username, title, body,mAuth.getCurrentUser().getPhotoUrl().toString(),key,taskSnapshot.getDownloadUrl().toString(),mSerialized);
                        //Map<String, Object> postValues = post.toMap();
                        Map<String, Object> childUpdates = new HashMap<>();
                        childUpdates.put("/posts/" + key, post);
                        childUpdates.put("/user-posts/" + username + "/" + key, post);
                        mDatabase.updateChildren(childUpdates);

                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"uploaded",Toast.LENGTH_SHORT).show();
                        finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"upload failed.Please Try again",Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double totalprogress = (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded "+(int)totalprogress+"%");
                }
            });
        }
        else{
            Post post = new Post(userId, username, title, body,mAuth.getCurrentUser().getPhotoUrl().toString(),key,"noImage",mSerialized);
            //Map<String, Object> postValues = post.toMap();
            Map<String, Object> childUpdates = new HashMap<>();
            childUpdates.put("/posts/" + key, post);
            childUpdates.put("/user-posts/" + username + "/" + key, post);
            mDatabase.updateChildren(childUpdates);
            Toast.makeText(getApplicationContext(),"Posted",Toast.LENGTH_SHORT).show();
        }




    }

    private void disablefabbutton(boolean b) {
        mTitleField.setEnabled(b);
        if (b) {
            mPublish.setVisibility(View.VISIBLE);
           // mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mPublish.setVisibility(View.GONE);
//            mSubmitButton.setVisibility(View.GONE);
        }
    }
  /*  private String getactualImage(Uri uri)
    {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return MimeTypeMap.getFileExtensionFromUrl(contentResolver.getType(uri));
    }*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0 && resultCode == RESULT_OK)
        {
            imageUri = data.getData();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),imageUri);
                addImageView.setImageBitmap(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
