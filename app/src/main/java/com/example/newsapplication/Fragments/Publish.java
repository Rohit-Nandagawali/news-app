package com.example.newsapplication.Fragments;

import static android.app.Activity.RESULT_OK;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.newsapplication.DrawerActivity;
import com.example.newsapplication.R;
import com.example.newsapplication.databinding.FragmentPublishBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Date;
import java.util.HashMap;

public class Publish extends Fragment {
    FragmentPublishBinding binding;
    Uri filepath;


    public Publish() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentPublishBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        selectimage();//to open gallery to select image
        // Adding cancel button functionality
        binding.btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigating to DrawerActivity
                Intent intent = new Intent(getActivity(), DrawerActivity.class);
                startActivity(intent);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }

    private void selectimage() {
        //on click open gallery, added 2 permission for this in manifest file
        binding.view2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Your Image"), 101);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            binding.imgThumbnail.setVisibility(View.VISIBLE);
            binding.imgThumbnail.setImageURI(filepath);
//            to show the upload image
            binding.view2.setVisibility(View.INVISIBLE);
            binding.bSelectImage.setVisibility(View.INVISIBLE);
//            uploading file to firebase
            uploaddata(filepath);
        }
    }

    private void uploaddata(Uri filepath) {
        binding.btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (binding.bTittle.getText().toString().equals("")) {
                    binding.bTittle.setError("Title is Required!");
                } else if (binding.bDesc.getText().toString().equals("")) {
                    binding.bDesc.setError("Description is Required!");
                } else if (binding.bAuthor.getText().toString().equals("")) {
                    binding.bAuthor.setError("Author is Required!");
                }else if (binding.spinnerCategory.getSelectedItem().toString().equals("Select Category")) {
                    Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                }else{

                    // Getting the selected category
                    String category = binding.spinnerCategory.getSelectedItem().toString();


                    // if all fields are filled
                    ProgressDialog pd = new ProgressDialog(getContext());
                    pd.setTitle("Uploading...");
                    pd.setMessage("We are uploading your news please wait..");
                    pd.setCancelable(false);
                    pd.show();

                    //converting data into string
                    String title = binding.bTittle.getText().toString();
                    String desc = binding.bDesc.getText().toString();
                    String author = binding.bAuthor.getText().toString();

                    //if file is correctly selected

                    if (filepath != null) {
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference reference = storage.getReference().child("images/" + filepath.toString() + ".jpg");
                        reference.putFile(filepath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                reference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        String file_url = task.getResult().toString();
                                        String date = (String) DateFormat.format("dd", new Date());
                                        String month = (String) DateFormat.format("MMM", new Date());
                                        String year = (String) DateFormat.format("yyyy", new Date());  // Year
                                        String final_date = date + " " + month + " " + year;

                                        //creating object of data
                                        HashMap<String, String> map = new HashMap<>();
                                        map.put("tittle", title);
                                        map.put("desc", desc);
                                        map.put("author", author);
                                        map.put("category", category);
                                        map.put("date", final_date);
                                        map.put("img", file_url);
                                        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                                        map.put("share_count", "0");

                                        //storing data to firebase
                                        FirebaseFirestore.getInstance().collection("News").document().set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                //news uploaded successfully
                                                if (task.isSuccessful()) {
                                                    pd.dismiss();
                                                    Toast.makeText(getContext(), "News Uploaded Successfully !", Toast.LENGTH_SHORT).show();


                                                    //reseting everything after uploading news
                                                    binding.imgThumbnail.setVisibility(View.INVISIBLE);
                                                    binding.view2.setVisibility(View.VISIBLE);
                                                    binding.bSelectImage.setVisibility(View.VISIBLE);
                                                    binding.bTittle.setText("");
                                                    binding.bDesc.setText("");
                                                    binding.bAuthor.setText("");
                                                    binding.spinnerCategory.setSelection(0);

                                                    // Navigating to DrawerActivity
                                                    Intent intent = new Intent(getActivity(), DrawerActivity.class);
                                                    startActivity(intent);


                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }

                }
            }
        });

    }//uploaddata ends
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}