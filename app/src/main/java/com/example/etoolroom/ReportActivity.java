package com.example.etoolroom;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nullable;

public class ReportActivity extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference noteRef = db.collection("toolroomItems");

    private NoteAdapter adapter;
    private List<CardList> doc_list;
    private List<String> menu_list;
    private String Id;
    private String size;
    private String plant;
    private RecyclerView recyclerView;
    private ArrayAdapter<String> menu_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        //this line for creating custom back button
        //getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_add_circle_outline_black_24dp);
        setTitle("Report");
        FirebaseApp.initializeApp(this);
        setUpRecyclerView();

        //floating btn search by plant
        FloatingActionButton actionButton = findViewById(R.id.floatingAction);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu_array = new ArrayAdapter<>(ReportActivity.this, android.R.layout.simple_list_item_1,menu_list);
                AlertDialog.Builder builder = new AlertDialog.Builder(ReportActivity.this);
                builder.setTitle("Plant");
                builder.setAdapter(menu_array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.getFilter().filter(menu_array.getItem(which));
                    }
                }).create().show();
            }
        });


    }

    private void setUpRecyclerView() {
        doc_list = new ArrayList<>();
        menu_list = new ArrayList<>();
        noteRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                doc_list.clear();
                HashSet<String> hashSet = new HashSet<>();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        Note note = documentSnapshot.toObject(Note.class);
                        note.setDocumentId(documentSnapshot.getId());

                        //arrange items in cardlist
                        Id = note.getDocumentId();
                        size = note.getSize();
                        plant = note.getPlant_txt();
                        doc_list.add(new CardList(Id, size, plant));
                        hashSet.add(plant);
                        menu_list.clear();
                    }
                    menu_list.addAll(hashSet);
                }
                recyclerView = findViewById(R.id.recyclerView);
                recyclerView.setHasFixedSize(true);
                recyclerView.setLayoutManager(new LinearLayoutManager(ReportActivity.this));

                //transition anim
                LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(ReportActivity.this, R.anim.layout_faded);
                recyclerView.setLayoutAnimation(controller);

                adapter = new NoteAdapter(doc_list, ReportActivity.this);
                Collections.reverse(doc_list);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //search menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.floating_menu, menu);

        MenuItem search = menu.findItem(R.id.search);
        android.support.v7.widget.SearchView searchView = (android.support.v7.widget.SearchView) search.getActionView();

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (adapter != null) {
                    adapter.getFilter().filter(s);
                }
                return false;
            }
        });
        return true;
    }
}
