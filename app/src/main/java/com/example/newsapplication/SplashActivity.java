package com.example.newsapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.example.newsapplication.databinding.ActivitySplashBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SplashActivity extends AppCompatActivity {

    ActivitySplashBinding binding;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        binding= ActivitySplashBinding.inflate(getLayoutInflater());

        setupsignin();
    }

    private void setupsignin() {

//        service that handles user authentication
        auth = FirebaseAuth.getInstance();

//        setting options for sign in using Gmail, asking for email id
        signInOptions=new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

//      signInClient configuring
        signInClient= GoogleSignIn.getClient(this,signInOptions);
    }

    @Override
    protected void onStart() {
//        getting user data
        FirebaseUser currentUser = auth.getCurrentUser();


//
        if (currentUser!=null){ //if user is already logged in then, go to home page

            startActivity(new Intent(this,DrawerActivity.class));
            finish();
        }else{ //if not login, then login
            sigin();

        }
        super.onStart();
    }

    private void sigin() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==100){
            Task<GoogleSignInAccount> task=GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
                auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){ //if login successful then go to main page
                            Toast.makeText(getApplicationContext(), "Login Successful!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),DrawerActivity.class));
                            finish();
                        }else{ //else close the app
                            Toast.makeText(getApplicationContext(), "Login Failed!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            } catch (ApiException e) {
                e.printStackTrace();
            }
        }
    }
}