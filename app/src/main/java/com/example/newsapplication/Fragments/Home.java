package com.example.newsapplication.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.newsapplication.Adapter;
import com.example.newsapplication.Model;
import com.example.newsapplication.R;
import com.example.newsapplication.databinding.FragmentHomeBinding;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Home extends Fragment {

    FragmentHomeBinding binding;
    ArrayList<Model> list;
    Adapter adapter;
    Model model;

    public Home() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setupRv(); //setting up recycler view
        setupSearchView(); //for searching
        setupChipGroup(); // For category filtering
        super.onViewCreated(view, savedInstanceState);
    }

    private void setupChipGroup() {
        binding.filters.setOnCheckedStateChangeListener(new ChipGroup.OnCheckedStateChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull ChipGroup group, @NonNull List<Integer> checkedIds) {

                if (checkedIds.isEmpty()) {
                    // Handle case where no chip is selected

                } else {
                    // Assuming single selection mode, get the first checked id
                    filter(binding.searchview.getQuery().toString(), getSelectedCategory()); // Filter by text and selected category
                }
            }

        });

        // Make sure 'All' chip is selected by default
        Chip chipAll = binding.filters.findViewById(R.id.chipAll);
        if (chipAll != null) {
            chipAll.setChecked(true);
        }
    }

    private void setupSearchView() {
        binding.searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText,getSelectedCategory()); //whatever text user enter, search for this
                return false;
            }
        });
    }


    //Checking which chip is selected
    private String getSelectedCategory() {
        int selectedChipId = binding.filters.getCheckedChipId();
        if (selectedChipId == View.NO_ID) {
            return "All";
        } else {
            Chip selectedChip = binding.filters.findViewById(selectedChipId);
            return selectedChip != null ? selectedChip.getText().toString() : "All";
        }
    }

    //filter method required for searching
    private void filter(String newText, String selectedCategory) {
        ArrayList<Model> filtered_list = new ArrayList<>();
        for(Model item:list){
            //getting category
            boolean matchesCategory = selectedCategory.equals("All") || item.getCategory().equals(selectedCategory);
            //getting title
            boolean matchesText = item.getTittle().toLowerCase().contains(newText.toLowerCase());

            //searching on the basis of category and title
            if (matchesCategory && matchesText) {
                filtered_list.add(item);
            }
        }
        // Update RecyclerView adapter with filtered list
        if (filtered_list.isEmpty()) {
            // Show a toast or message indicating no news is available
            adapter.filter_list(new ArrayList<>());
            Toast.makeText(getContext(), "No news available for '"+selectedCategory+"' category or search", Toast.LENGTH_SHORT).show();
        } else {
            adapter.filter_list(filtered_list);
        }
    }

    private void setupRv() {
        list = new ArrayList<>();

//        fetching data from firebase
        FirebaseFirestore.getInstance().collection("News").orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                list.clear();

                for (DocumentSnapshot snapshot:value.getDocuments()){
                    model = snapshot.toObject(Model.class); //structuring data into model that we have created
                    model.setId(snapshot.getId());
                    list.add(model); //and storing that data into list
                }
                adapter.notifyDataSetChanged();
            }
        });
        adapter = new Adapter(list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true); //new news will be shown at top
        binding.rvNews.setLayoutManager(linearLayoutManager);
        binding.rvNews.setAdapter(adapter); //actual design put here
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding=null;
    }
}