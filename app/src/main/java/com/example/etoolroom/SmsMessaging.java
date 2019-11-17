package com.example.etoolroom;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.etoolroom.ContactList;
import com.example.etoolroom.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.annotation.Nullable;

public class SmsMessaging extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    TextView txtPlant;
    TextView txtMessage;
    TextView txtphoneNo;
    Button sendBtn;
    ImageView add_no;
    String message;
    String number;
    String name;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();


    private String htime;
    private String hdate;
    private String cdate;
    private String ctime;
    private String tool;
    private String plant;
    private String extr;
    private String size;
    private String radio;
    private String remarks1;
    private String remarks2;
    private String docId;
    private List<String> doc_list = new ArrayList<>();
    private String number1;
    private String name1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_messaging);
        sendBtn = findViewById(R.id.btn_send_sms);
        txtphoneNo = findViewById(R.id.phone);
        txtMessage = findViewById(R.id.msg);
        txtPlant = findViewById(R.id.tv_sms_plant);
        add_no = findViewById(R.id.add_person);

        //scroll textView
        txtphoneNo.setMovementMethod(new ScrollingMovementMethod());

        name = getIntent().getStringExtra("name");
        number = getIntent().getStringExtra("number");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                sendSMSMessage();
            }
        });

        //add person to grp
        add_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String key = getIntent().getStringExtra("Key");
                try {
                    if (key.equals("1")) {

                        //when permission denied
                        add_no.setVisibility(View.INVISIBLE);
                        Toast.makeText(SmsMessaging.this, "You don't have permission to add contact", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {

                    //when permission granted
                    add_no.setVisibility(View.VISIBLE);
                    Intent i = new Intent(getApplicationContext(), ContactList.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        ShowDetails();
    }

    private void ShowDetails() {
        db.collection("toolroomItems").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                //receive items from firebase
                for (QueryDocumentSnapshot queryDocument : queryDocumentSnapshots) {
                    Note note = queryDocument.toObject(Note.class);
                    note.setDocumentId(queryDocument.getId());

                    docId = note.getDocumentId();
                    tool = note.getTool_txt();
                    plant = note.getPlant_txt();
                    extr = note.getExtr_txt();
                    size = note.getSize();
                    radio = note.getRadio();
                    hdate = note.getHdate();
                    htime = note.getHtime();
                    cdate = note.getCdate();
                    ctime = note.getCtime();
                    remarks1 = note.getRemarks1();
                    remarks2 = note.getRemarks2();
                    doc_list.add(docId);
                }

                //items in descending order
                Collections.reverse(doc_list);
                //display details in textview
                message = (
                        tool + "\n" + radio + "\n" + size + "\n" + plant + "\n" + extr + "\n" +
                                "Hand over : " + hdate + ";" + htime + "\n" +
                                "Commitment : " + cdate + ";" + ctime
                );
                txtMessage.setText(message);
                txtPlant.setText(plant);
                txtphoneNo.setText(name);

                db.collection("Number").document(plant).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        if (documentSnapshot.exists()) {
                            name1 = documentSnapshot.getString("name");
                            number1 = documentSnapshot.getString("number");
                            if ((name == null) && (number == null)) {
                                //display no and name
                                txtphoneNo.setText(name1);
                                number = number1;
                            }
                        }
                    }
                });
            }
        });
    }

    protected void sendSMSMessage() {
        //check if permission is already granted
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            //program execute when btn is pressed
            save_numbers();
        } else {
            //show the permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        //if permission granted
        if (requestCode == MY_PERMISSIONS_REQUEST_SEND_SMS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                save_numbers();
            } else {
                Toast.makeText(getApplicationContext(),
                        "SMS failed, please try again.", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void save_numbers() {
        try {
            Map<String, Object> no = new HashMap<>();
            no.put("name", name);
            no.put("number", number);
            if ((name != null) && (number != null)) {
                db.collection("Number").document(plant).set(no, SetOptions.merge());
            }
            // this array contains mobile nos
            String[] separate;
            SmsManager sm = SmsManager.getDefault();
            separate = number.split("\n");
            for (int i = 0; i < separate.length; i++) {
                //send bulk sms
                sm.sendTextMessage(separate[i].trim(), null, "" + message, null, null);
            }
            Intent i = new Intent(getApplicationContext(), ReportActivity.class);
            startActivity(i);
            Toast.makeText(getApplicationContext(), "SMS sent.",
                    Toast.LENGTH_LONG).show();
            finish();
        } catch (Exception e) {
            Toast.makeText(this, "Check all fields!", Toast.LENGTH_SHORT).show();
        }
    }
}
