package com.example.root.medium;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int RC_SIGN_IN = 100 ;
    private static final String TAG = "signin";
    ImageView Notification;
    private FirebaseAuth mAuth;
    GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;
    private ImageView profilepic;
    private NavigationView navigationView;
    private View hView;
    private TextView userName;
    private RecyclerView mRecyclerView;
    private BlogAdapter mAdapter;
    private List<Blog> blogList = new ArrayList<>();

    //  @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         navigationView = (NavigationView) findViewById(R.id.nav_view);
         hView =  navigationView.getHeaderView(0);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Home");

        }

        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        Notification = (ImageView) findViewById(R.id.imageview_edit);
        Notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "new notifications", Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser!=null)
                {
                    Intent intent = new Intent(MainActivity.this,New_Post.class);
                    startActivity(intent);
                }
                else
                {
                    Snackbar.make(findViewById(R.id.drawer_layout),"Please SignIn to Write a Post",Snackbar.LENGTH_LONG).show();
                }

            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("984432365812-5medd8tjlp1te33mkmgtt74m9i1ea8tb.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();

        callBlankFragment();




    }

    private void callBlankFragment() {

        BlankFragment blankFragment = new BlankFragment();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.Content_main,blankFragment).commit();
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null)
        {
           // Snackbar.make(findViewById(R.id.drawer_layout),currentUser.getDisplayName(), Snackbar.LENGTH_SHORT).show();
            setprofilepic();
        }


    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.exit)
        {
            finish();
        }
        if(id == R.id.signout)
        {
            FirebaseAuth.getInstance().signOut();
            removeProfilePic();
            Toast.makeText(getApplicationContext(),"Signed out",Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.google_signin){

                signIn();
        }
        else if(id == R.id.userPosts)
        {
            if(mAuth.getCurrentUser()!=null)
            {
                callUserPosts();
                if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("My Posts");

            }
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Please SignIn to See Your Posts",Toast.LENGTH_SHORT).show();
            }

        }
        else if(id == R.id.Home)
        {
            callBlankFragment();

            if (getSupportActionBar() != null) {
                getSupportActionBar().setTitle("Home");
            }

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void callUserPosts() {
        UserPosts userPostsFragment = new UserPosts();
        android.support.v4.app.FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.Content_main,userPostsFragment).commit();
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {

                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
                Log.d(TAG, "Google sign in partially");
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);

            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Snackbar.make(findViewById(R.id.drawer_layout), user.getDisplayName().toString(), Snackbar.LENGTH_SHORT).show();
                            addUserToFirebase(user);
                            setprofilepic();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Snackbar.make(findViewById(R.id.drawer_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();

                        }


                    }
                });
    }

    private void setprofilepic() {
        if(mAuth.getCurrentUser()!=null)
        {
            FirebaseUser user = mAuth.getCurrentUser();
            String ImageUrl =user.getPhotoUrl().toString();
            try
            {

                profilepic = (ImageView)hView.findViewById(R.id.imageView);
                userName = (TextView) hView.findViewById(R.id.username);
                Picasso.with(this).load(ImageUrl).transform(new CropCircleTransformation()).placeholder(getResources().getDrawable(R.mipmap.ppic1)).resize(150,150).into(profilepic);
                userName.setText(capitalize(user.getDisplayName().toString().toLowerCase()));
            }
            catch (Exception e)
            {

            }

        }
    }

    private void addUserToFirebase(FirebaseUser user) {
        User Luser = new User(user.getDisplayName().toString(),user.getEmail().toString(),user.getUid().toString(),user.getPhotoUrl().toString());
        String userId = user.getDisplayName();
        String id = mDatabase.push().getKey();
        mDatabase.child(user.getUid()).setValue(Luser);
    }

    private void removeProfilePic() {

        profilepic = (ImageView)hView.findViewById(R.id.imageView);
        userName = (TextView) hView.findViewById(R.id.username);
        profilepic.setImageDrawable(getResources().getDrawable(R.mipmap.ppic1));
        userName.setText(" ");
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
    
}
