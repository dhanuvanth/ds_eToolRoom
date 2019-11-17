package com.example.etoolroom;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class ContactList extends AppCompatActivity {
    ListView lv;
    Button show;
    public static final int PERMISSION_REQUEST_CODE = 1;
    Cursor c;
    ArrayList<String> list = new ArrayList<>();
    ArrayList<String> list_2 = new ArrayList<>();
    HashSet<String> hashSet = new HashSet<>();

    String contactName;
    String phoneNo;
    String items,list_num,list_name;
    String[] separate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        //permission for read contact
        if (ContextCompat.checkSelfPermission(ContactList.this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            //Display contact in list
            showContact();
        } else {
            ActivityCompat.requestPermissions(ContactList.this, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSION_REQUEST_CODE);
        }
        //Display selected contact in list
       contact();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Display contact in list
                showContact();
            } else {
                //if permission denied
                Toast.makeText(getApplicationContext(),
                        "Contact failed, please try again.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showContact() {
        //show contact
        c = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");
        hashSet.clear();
        //each contact will stored in list
        while (c.moveToNext()) {
            contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            phoneNo = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            //To remove duplicate contact
            hashSet.add(contactName + "\n" + phoneNo);
        }
        list.clear();
        list.addAll(hashSet);
        //arrange contact in order
        Collections.sort(list);
        c.close();
    }



    private void contact(){
        //ListView
        lv = findViewById(R.id.recycle_contact_list);
        final ContactListAdapter adapter = new ContactListAdapter(this,list);
        lv.setFastScrollEnabled(true);
        lv.setAdapter(adapter);

        //Toggle for Checkbox
        lv.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //on item click
                String selectedItem = adapter.getItem(position);

                //add to list_2 if it is in list_2 remove from the list_2
                if (list_2.contains(selectedItem)) {
                    list_2.remove(selectedItem);
                } else {
                    list_2.add(selectedItem);
                }
            }
        });

        adapter.notifyDataSetChanged();

        show = findViewById(R.id.show);
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Display selestedItems as Dialog
                items = "";
                list_name = "";
                list_num = "";
                for (String names : list_2) {
                    items += "-" + names + "\n";

                    //To split contact into name and number
                    separate = names.split("\n");
                    list_num += separate[1] + "\n";
                    list_name += separate[0] + "," +"\n";
                }
                if (!(items.equals(null))) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContactList.this);
                    builder.setTitle("Add")
                            .setMessage(items)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent i = new Intent(ContactList.this, SmsMessaging.class);
                                    i.putExtra("name",list_name);
                                    i.putExtra("number",list_num);
                                    startActivity(i);
                                    Toast.makeText(ContactList.this, "Successfully Added", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            })
                            .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).create().show();
                }else{
                    Toast.makeText(ContactList.this, "Nothing has selected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
