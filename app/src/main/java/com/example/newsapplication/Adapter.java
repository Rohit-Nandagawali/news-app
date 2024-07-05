package com.example.newsapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    ArrayList<Model> list;

    public Adapter(ArrayList<Model> list) {
        this.list = list;
        this.notifyDataSetChanged(); //when new data added it will be refreshed
    }


    //for search functionality
    public void filter_list(ArrayList<Model> filter_list) {
        list = filter_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter.ViewHolder holder, int position) {
        Model model = list.get(position);
        holder.title.setText(model.getTittle());
        holder.date.setText(model.getDate());
        holder.share_count.setText(model.getShare_count());
        holder.author.setText(model.getAuthor());
        holder.category.setText(model.getCategory());

        Glide.with(holder.author.getContext()).load(model.getImg()).into(holder.img);

        //on single press open new in new screen
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.author.getContext(), NewsDetail.class);
                intent.putExtra("id", model.getId());
                holder.author.getContext().startActivity(intent);
            }
        });


        //on long press open dialog
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.author.getContext());
                builder.setTitle("What you want to do?");
                builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final Dialog u_dialog = new Dialog(holder.author.getContext());
                        u_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        u_dialog.setCancelable(false);
                        u_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        u_dialog.setContentView(R.layout.update_dialog);
                        u_dialog.show();

                        //getting edittext

                        EditText title = u_dialog.findViewById(R.id.b_tittle);
                        EditText desc = u_dialog.findViewById(R.id.b_desc);
                        EditText author = u_dialog.findViewById(R.id.b_author);

                        //setting data from firebase
                        title.setText(model.getTittle());
                        desc.setText(model.getDesc());
                        author.setText(model.getAuthor());


                        TextView dialogbutton = u_dialog.findViewById(R.id.btn_publish);
                        TextView closebutton = u_dialog.findViewById(R.id.btn_cancle);


                        //close dialogue when clicked on cancel
                        closebutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                u_dialog.dismiss();
                            }
                        });

                        dialogbutton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //check is something is empty
                                if (title.getText().toString().equals("")) {
                                    title.setError("Field is Required!!");
                                } else if (desc.getText().toString().equals("")) {
                                    desc.setError("Field is Required!!");
                                } else if (author.getText().toString().equals("")) {
                                    author.setError("Field is Required!!");
                                } else {
                                    HashMap<String, Object> map = new HashMap<>();
                                    map.put("tittle", title.getText().toString());
                                    map.put("desc", desc.getText().toString());
                                    map.put("author", author.getText().toString());

                                    //changing it into firebase
                                    FirebaseFirestore.getInstance().collection("News").document(model.getId()).update(map)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                        u_dialog.dismiss();
                                                    }
                                                }
                                            });
                                }

                            }
                        });
                    }
                });
                builder.setNegativeButton("DELETE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AlertDialog.Builder builders = new AlertDialog.Builder(holder.author.
                                getContext());
                        builders.setTitle("Are you sure to Delete it?");
                        builders.setPositiveButton("Yes! I am Sure", new
                                DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        //deleting it from firebase
                                        FirebaseFirestore.getInstance().collection("News").
                                                document(model.getId()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                // Deletion successful, navigate to drawer activity
                                                Intent intent = new Intent(holder.author.getContext(), DrawerActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                holder.author.getContext().startActivity(intent);
                                            }
                                        });
                                        dialog.dismiss();
                                    }
                                });
                        AlertDialog dialogs = builders.create();
                        dialogs.show();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView date, title, share_count, author, category;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            date = itemView.findViewById(R.id.t_date);
            title = itemView.findViewById(R.id.textView9);
            share_count = itemView.findViewById(R.id.textView10);
            author = itemView.findViewById(R.id.textView8);
            category = itemView.findViewById(R.id.category);
        }
    }
}
