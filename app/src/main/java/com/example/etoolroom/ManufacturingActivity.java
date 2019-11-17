package com.example.etoolroom;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

public class ManufacturingActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private EditText e_c_remarks;
    private TextView e_c_size;
    private TextView h_date;
    private TextView c_date;
    private TextView h_time;
    private TextView c_time;
    private Button b_c_send;
    private TextView c_tool, c_plant, c_extr;
    private Spinner s_docid;

    private FirebaseFirestore db;

    int a, b, value = 0, rm = 1;
    Snackbar snackbar;


    private String htime;
    private String hdate;
    private String cdate;
    private String ctime;
    private String tool = null;
    private String plant = null;
    private String extr = null;
    private String size = null;
    private String radio = null;
    private String remarks1;
    private String docId;
    private String Id;

    private List<String> doc_list;
    private ArrayAdapter<String> doc_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufacturing);

        FirebaseApp.initializeApp(this);

        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        db = FirebaseFirestore.getInstance();
        Initiallize();
        date();
        time();
        b_c_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database(v);
                doc_list.remove(docId);
                doc_array.notifyDataSetChanged();
            }
        });
    }

    private void Initiallize() {
        c_tool = findViewById(R.id.tv_c_tool);
        c_plant = findViewById(R.id.tv_c_plant);
        c_extr = findViewById(R.id.tv_c_extr);
        e_c_size = findViewById(R.id.et_c_size);
        e_c_remarks = findViewById(R.id.et_c_remarks);
        c_date = findViewById(R.id.tv_commitdate);
        c_time = findViewById(R.id.tv_committime);
        h_date = findViewById(R.id.tv_handeddate);
        h_time = findViewById(R.id.tv_handedtime);
        b_c_send = findViewById(R.id.btn_c_send);
        s_docid = findViewById(R.id.spin_docid);
    }

    private void date() {
        h_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogDateFragment();
                dialogFragment.show(getSupportFragmentManager(), "Date picker");
                a = 1;
            }
        });
        c_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogDateFragment();
                dialogFragment.show(getSupportFragmentManager(), "Date picker");
                a = 0;
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDate = DateFormat.getDateInstance(DateFormat.LONG).format(c.getTime());
        if (a == 1) {
            h_date.setText(currentDate);
            hdate = h_date.getText().toString();
        } else {
            c_date.setText(currentDate);
            cdate = c_date.getText().toString();
        }
    }

    private void time() {
        h_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogTimeFragment();
                dialogFragment.show(getSupportFragmentManager(), "Time Picker");
                b = 1;
            }
        });
        c_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new DialogTimeFragment();
                dialogFragment.show(getSupportFragmentManager(), "Time Picker");
                b = 0;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (b == 1) {
            h_time.setText("" + hourOfDay + ":" + "" + minute);
            htime = h_time.getText().toString();
        } else {
            c_time.setText("" + hourOfDay + ":" + "" + minute);
            ctime = c_time.getText().toString();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setText();
    }

    private void setText() {
        doc_list = new ArrayList<>();
        doc_array = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, doc_list);

        db.collection("toolroomItems").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                doc_list.clear();
                if (e != null) {
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {
                        Note note = DocumentSnapshot.toObject(Note.class);
                        note.setDocumentId(DocumentSnapshot.getId());

                        docId = note.getDocumentId();

                        doc_list.add(docId);
                    }
                }
                Collections.reverse(doc_list);
                doc_array.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s_docid.setAdapter(doc_array);
            }
        });

        s_docid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, final View view, int position, final long id) {
                Id = doc_array.getItem(position);

                db.collection("toolroomItems").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots) {
                            Note note = queryDocument.toObject(Note.class);
                            note.setDocumentId(queryDocument.getId());

                            docId = note.getDocumentId();
                            if (docId.equals(Id)) {
                                tool = note.getTool_txt();
                                plant = note.getPlant_txt();
                                extr = note.getExtr_txt();
                                size = note.getSize();
                                radio = note.getRadio();
                                hdate = note.getHdate();
                                htime = note.getHtime();
                                remarks1 = note.getRemarks1();
                            }
                        }
                        c_tool.setText(tool);
                        c_plant.setText(plant);
                        c_extr.setText(extr);
                        e_c_size.setText(size);
                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void database(View v) {
        if ((cdate != null) && (ctime != null)) {
            String size = e_c_size.getText().toString();
            String remarks2 = e_c_remarks.getText().toString();

            Note note = new Note();
            note.setCdate(cdate);
            note.setCtime(ctime);
            note = new Note(tool, plant, extr, size, remarks1, remarks2, htime, hdate, radio, cdate, ctime);
            db.collection("toolroomItems").document(Id).set(note);


            snackbar = Snackbar.make(v, "Update Successful", Snackbar.LENGTH_LONG);
            snackbar.show();
            value = 1;

            Intent a = new Intent(getApplicationContext(), SmsMessaging.class);
            a.putExtra("Key","1");
            startActivity(a);
        } else {
            snackbar = Snackbar.make(v, "Enter all Field...", Snackbar.LENGTH_LONG);
            snackbar.show();
            value = 0;
        }
    }
}
