package com.example.newsapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import com.bumptech.glide.Glide;
import com.example.newsapplication.databinding.ActivityNewsDetailBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class NewsDetail extends AppCompatActivity {
    ActivityNewsDetailBinding binding;
    String id; //id that we got from previous screen
    String  title, desc,count,date,category,author;

    String appLink="https://www.google.com/";
    int n_count;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);
        binding = ActivityNewsDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        
        //function to show data
        showdata();
    }

    private void showdata() {
        id = getIntent().getStringExtra("id"); //getting id from previous id


        //fetching news with that 'id' from firebase
        FirebaseFirestore.getInstance().collection("News").document(id).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                Glide.with(getApplicationContext()).load(value.getString("img")).into(binding.imageView3);
                binding.textView4.setText(Html.fromHtml("<font color='B7B7B7'>Published By </font> <font color='#000000'>"+value.getString("author")));
                binding.textView5.setText(value.getString("tittle"));
                binding.textView6.setText(value.getString("desc"));
                binding.tDate.setText(value.getString("date"));
                binding.category.setText(value.getString("category"));

                //storing this for passing latter
                title= value.getString("tittle");
                desc= value.getString("desc");
                count= value.getString("share_count");
                author= value.getString("author");
                date=value.getString("date");

                //incresing share count
                int i_count=Integer.parseInt(count);
                n_count=i_count+1;

            }
        });

        //handle share button
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener(
        ) {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_SEND);
                String shareBody = "*"+title+"*\n\n"+desc+"\n- Published by "+author+"\n"+date+"\n\n\nDownload News App Now\n"+appLink;
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, title);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent,"Share News Using"));


                //saving share count in database
                HashMap<String,Object> map = new HashMap<>();
                map.put("share_count", String.valueOf(n_count));
                FirebaseFirestore.getInstance().collection("News").document(id).update(map);
            }
        });

        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}