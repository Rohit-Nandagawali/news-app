package com.example.newsapplication.Fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.example.newsapplication.R;
import com.example.newsapplication.SplashActivity;
import com.example.newsapplication.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class Profile extends Fragment {
    FragmentProfileBinding binding;
    GoogleSignInAccount account;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;

    public Profile() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

//        initializing binding for
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        initVariables(); //to initialize variables
        super.onViewCreated(view, savedInstanceState);
    }

    private void initVariables() { //to initialize variables
        account = GoogleSignIn.getLastSignedInAccount(getContext()); //geting user data
        binding.uName.setText(account.getDisplayName()); //setting username
        binding.uEmail.setText(account.getEmail()); //setting user email
        Glide.with(getContext()).load(account.getPhotoUrl()).into(binding.profileDp); 
        //using glide to set photo from url

        logoutuser();
    }

    private void logoutuser() {
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.default_web_client_id))
                        .requestEmail()
                        .build();

                signInClient = GoogleSignIn.getClient(getContext(), signInOptions);

                //showing dialog to confirm logout
                new AlertDialog.Builder(getActivity())
                        .setTitle("Log Out?")
                        .setMessage("Are you sure to logout from app?")
                        .setCancelable(false)
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseAuth.getInstance().signOut();//logout from firebase

                                signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() { //logout from google-auth
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialog.dismiss(); //close dialog
//                                        navigate to SplashActivity
                                        startActivity(new Intent(getActivity().getApplicationContext(), SplashActivity.class));
                                        getActivity().finish();
                                    }
                                });
                            }
                        }).show();


            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}