package com.example.auth2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity {

    static int GOOGLE_SIGN = 123;
    FirebaseAuth mAuth;
    Button btnLogin, btnLogout;
    TextView text;
    ImageView image;
    ProgressBar progressBar;
    GoogleSignInClient mGoogleClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLogin = findViewById(R.id.button_login);
        btnLogout = findViewById(R.id.button_logout);
        text = findViewById(R.id.TextTitle);
        image = findViewById(R.id.image_firebase);
        progressBar = findViewById(R.id.progress_circular);

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions
                .Builder()
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleClient = GoogleSignIn.getClient(MainActivity.this, googleSignInOptions);

        btnLogin.setOnClickListener(v -> SignInGoogle());
        btnLogout.setOnClickListener(v -> Logout());

        if (mAuth.getCurrentUser() != null){
            FirebaseUser user = mAuth.getCurrentUser();
            updateUI(user);
        }
    }

    void SignInGoogle(){
        progressBar.setVisibility(View.VISIBLE);
        Intent signInIntent = mGoogleClient.getSignInIntent();
        startActivityForResult(signInIntent, GOOGLE_SIGN);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GOOGLE_SIGN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + account.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.d("TAG", "signInWithCredential:success");

                        FirebaseUser user = mAuth.getCurrentUser();
                        updateUI(user);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);

                        Log.w("TAG", "signInWithCredential:failure", task.getException());

                        Toast.makeText(MainActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photo = String.valueOf(user.getPhotoUrl());

            for (UserInfo userInfo : user.getProviderData()) {
                if (name == null && userInfo.getDisplayName() != null) {
                    name = userInfo.getDisplayName();
                }
            }

            text.append("Info : \n");
            text.append(name + "\n");
            text.append(email);
            Picasso.get().load(photo).into(image);
            btnLogout.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.INVISIBLE);
        } else if (user == null){
            text.setText("Firebase Login \n");
            image.setImageResource(R.drawable.ic_firebase_logo);
//            Picasso.get().load(R.drawable.ic_firebase_logo).into(image);
            btnLogout.setVisibility(View.INVISIBLE);
            btnLogin.setVisibility(View.VISIBLE);
        }
    }

    private void Logout() {
        FirebaseAuth.getInstance().signOut();
        mGoogleClient.signOut().addOnCompleteListener(this,
                task -> updateUI(null));
    }

}
