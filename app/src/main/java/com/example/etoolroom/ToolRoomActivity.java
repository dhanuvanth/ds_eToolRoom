package com.example.etoolroom;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.annotation.Nullable;

public class ToolRoomActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private Spinner s_tool, s_plant, s_extr;
    private ImageView add_tool, add_plant, add_extr;
    private EditText e_size, e_remarks;
    private TextView h_date, h_time;
    private Button b_send;
    private TextView t_tool, t_plant, t_extr;
    private RadioGroup r_group;
    private RadioButton r_btn;
    private Snackbar snackbar;

    private FirebaseFirestore db;

    int a, b, value;
    List<String> lst_tool, lst_plant, lst_extr;
    HashSet<String> set_tool, set_plant, set_extr;
    ArrayAdapter<String> array_tool;
    ArrayAdapter<String> array_plant;
    ArrayAdapter<String> array_extra;

    private String tool_txt;
    private String plant_txt;
    private String extr_txt;
    private String htime;
    private String hdate;
    private String radio;
    private String docId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool_room);

        //firebase init
        FirebaseApp.initializeApp(this);

        Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        db = FirebaseFirestore.getInstance();
        Initialize();
        date();
        time();
        tool();
        plant();
        extr();

        //radio btn
        r_group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int selectId = group.getCheckedRadioButtonId();
                r_btn = findViewById(selectId);
                r_btn.setChecked(true);
                radio = r_btn.getText().toString();
            }
        });

        b_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database(v);
            }
        });
    }

    private void Initialize() {
        s_tool = findViewById(R.id.spin_tool);
        s_plant = findViewById(R.id.spin_plant);
        s_extr = findViewById(R.id.spin_ext);
        add_tool = findViewById(R.id.iv_tool);
        add_plant = findViewById(R.id.iv_plant);
        add_extr = findViewById(R.id.iv_extr);
        e_size = findViewById(R.id.et_size);
        e_remarks = findViewById(R.id.et_remarks);
        h_date = findViewById(R.id.tv_handeddate);
        h_time = findViewById(R.id.tv_handedtime);
        b_send = findViewById(R.id.btn_send);
        t_tool = findViewById(R.id.tv_tool);
        t_plant = findViewById(R.id.tv_plant);
        t_extr = findViewById(R.id.tv_extr);
        t_extr = findViewById(R.id.tv_extr);
        r_group = findViewById(R.id.rd_group);
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
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        if (b == 1) {
            h_time.setText("" + hourOfDay + ":" + "" + minute);
            htime = h_time.getText().toString();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        tool();
        plant();
        extr();
        getid();
    }

    private void tool() {
        //list all items
        lst_tool = new ArrayList<>();
        array_tool = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, lst_tool);
        set_tool = new HashSet<>();
        final String Name = t_tool.getText().toString();

        //insert data to firebase
        db.collection(Name).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                lst_tool.clear();
                set_tool.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {

                        //custom object class
                        Note note = DocumentSnapshot.toObject(Note.class);

                        //add to list
                        if (note.getName().length() != 0) {
                            String name = note.getName().substring(0, 1).toUpperCase() + note.getName().substring(1).toLowerCase();
                            set_tool.add(name);
                        }
                        lst_tool.clear();

                        //delete duplicate items
                        lst_tool.addAll(set_tool);
                    }
                }
                array_tool.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                array_tool.notifyDataSetChanged();
                s_tool.setAdapter(array_tool);
                Collections.reverse(lst_tool);
            }
        });

        //add new items to Spinner
        add_tool.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox(Name);
            }
        });

        //Spinner item click
        s_tool.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tool_txt = array_tool.getItem(position);

                //onLongClick to delete item
                longClick(tool_txt,Name,s_tool,array_tool);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void plant() {
        lst_plant = new ArrayList<>();
        set_plant = new HashSet<>();
        array_plant = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, lst_plant);
        final String Name = t_plant.getText().toString();

        registerForContextMenu(s_plant);

        //insert plant
        db.collection(Name).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                set_plant.clear();
                lst_plant.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {

                        //custom object class
                        Note note = DocumentSnapshot.toObject(Note.class);

                        //get id from database
                        note.setDocumentId(DocumentSnapshot.getId());

                        if (note.getName().length() != 0) {
                            String name = note.getName().substring(0, 1).toUpperCase() + note.getName().substring(1).toLowerCase().trim();
                            set_plant.add(name);
                        }
                        lst_plant.clear();

                        //delete duplicate items
                        lst_plant.addAll(set_plant);
                    }
                }
                array_plant.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                array_plant.notifyDataSetChanged();
                s_plant.setAdapter(array_plant);

                //arrange in descending order
                Collections.reverse(lst_plant);
            }
        });

        //add to list
        add_plant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox(Name);
            }
        });

        //on list item selected
        s_plant.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                plant_txt = array_plant.getItem(position);

                //onLongClick to delete
                longClick(plant_txt,Name,s_plant,array_plant);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void extr() {
        lst_extr = new ArrayList<>();
        set_extr = new HashSet<>();
        array_extra = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_layout, lst_extr);
        final String Name = t_extr.getText().toString();

        registerForContextMenu(s_extr);

        db.collection(Name).addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                lst_extr.clear();
                set_extr.clear();
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {
                        Note note = DocumentSnapshot.toObject(Note.class);
                        if (note.getName().length() != 0) {
                            String name = note.getName().substring(0, 1).toUpperCase() + note.getName().substring(1).toLowerCase().trim();
                            set_extr.add(name);
                        }
                        lst_extr.clear();
                        lst_extr.addAll(set_extr);
                    }
                }
                array_extra.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                array_extra.notifyDataSetChanged();
                s_extr.setAdapter(array_extra);
                Collections.reverse(lst_extr);
            }
        });

        add_extr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogbox(Name);
            }
        });

        s_extr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                extr_txt = array_extra.getItem(position);

                //onLongClick
                longClick(extr_txt,Name,s_extr,array_extra);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void dialogbox(final String name) {

        //enter new item in dialog box
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(ToolRoomActivity.this);
        @SuppressLint("InflateParams") final View inflater = getLayoutInflater().inflate(R.layout.login_dialog, null);

        //init
        TextView tv_name = inflater.findViewById(R.id.tv_title);
        final EditText itemAdd = inflater.findViewById(R.id.password);

        tv_name.setText(name);
        itemAdd.setHint("Enter the name...");
        itemAdd.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(inflater).setPositiveButton("add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String item = itemAdd.getText().toString();

                Note note = new Note(item);
                db.collection(name).add(note);

            }
        }).setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).setCancelable(true).create().show();
    }

    private void getid() {
        db.collection("toolroomItems").addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                assert queryDocumentSnapshots != null;
                for (QueryDocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {

                    //getid of doc
                    Note note = DocumentSnapshot.toObject(Note.class);
                    note.setDocumentId(DocumentSnapshot.getId());
                    docId = note.getDocumentId();
                }
            }
        });
    }

    private void database(View v) {
        String size = e_size.getText().toString();
        String remarks = e_remarks.getText().toString();

        if (!(size.equals("")) && (radio != null) && (hdate != null) && (htime != null)) {
            int i, x;
            SharedPreferences prefs1 = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            try {
                x = prefs1.getInt("int", Integer.parseInt(docId));
            } catch (Exception e) {
                x = prefs1.getInt("int", 0);
            }

            for (i = -1; i < x; i++) {
                x = ++x;

                //save previous id for ref
                SharedPreferences prefs = getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt("int", x);
                editor.apply();
                Note note = new Note(tool_txt, plant_txt, extr_txt, size, remarks, htime, hdate, radio);
                db.collection("toolroomItems").document("a" + x).set(note);
                e_size.setImeOptions(EditorInfo.IME_ACTION_DONE);
                e_remarks.setImeOptions(EditorInfo.IME_ACTION_DONE);

                e_size.setText("");
                h_date.setText("Select Date");
                h_time.setText("Select Time");

                //notification
                snackbar = Snackbar.make(v, "Successfully added.", Snackbar.LENGTH_LONG);
                snackbar.show();
                value = 1;
                break;
            }
            Intent a = new Intent(getApplicationContext(), SmsMessaging.class);
            startActivity(a);
        } else {
            snackbar = Snackbar.make(v, "Enter all Field...", Snackbar.LENGTH_LONG);
            snackbar.show();

            value = 0;
        }
    }

    private void longClick(final String text, final String name, Spinner spin, final ArrayAdapter array){

        //no onlongclick listener so use the ontouchlistener for long click
        //for long click
        final Handler actionHandler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ToolRoomActivity.this, ""+text, Toast.LENGTH_SHORT).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ToolRoomActivity.this);
                builder.setTitle("Delete")
                        .setMessage("Do you want to delete?")
                        .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.collection(name).addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                                        if (queryDocumentSnapshots != null) {
                                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                                String id = documentSnapshot.getId();
                                                if (text.toLowerCase().trim().equals(documentSnapshot.get("name").toString().toLowerCase().trim())) {

                                                    //delete item from list
                                                    db.collection(name)
                                                            .document(id).delete();
                                                    array.notifyDataSetChanged();
                                                }
                                            }
                                        }
                                    }
                                });
                            }
                        })
                        .setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        };

        spin.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //handle touch for 1sec
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    actionHandler.postDelayed(runnable, 1000);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    actionHandler.removeCallbacks(runnable);
                }
                return false;

            }
        });
    }

}