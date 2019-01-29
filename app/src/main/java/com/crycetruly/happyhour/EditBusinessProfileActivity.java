package com.crycetruly.happyhour;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.crycetruly.happyhour.utils.Utils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditBusinessProfileActivity extends AppCompatActivity {
    private static final int GALLERY_CODE = 1;
    private FloatingActionButton fabpick;
    private AppCompatEditText bname, bemail, baddress, bwebsite, bphone;
    private static final String TAG = "EditBusinessProfileActi";
    Uri uri;
    ProgressBar progressBar;
    TextView save;
    String type;
    ImageView bphoto;
    NestedScrollView nestedScrollView;
    AppCompatSpinner cat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_business_profile);


        initToolbar();
        initComponents();
        setData();
        String[] types={"Food Processing","Construction","Real estate","Small manufacturing industries","Tourism and hospitality","Trading","Transport","Computer and internet services","Fashion","Energy"};
      cat  =findViewById(R.id.categs);
        cat.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,types));
        cat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type=String.valueOf(parent.getItemAtPosition(position));

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fabpick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                            openGallery();

            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nametext, email, phone, address;
                nametext = bname.getText().toString().trim();
                email = bemail.getText().toString().trim();
                address = baddress.getText().toString();
                phone = bphone.getText().toString().trim();

                if (TextUtils.isEmpty(nametext)) {
                    Snackbar.make(nestedScrollView, "Business Name cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)) {
                    Snackbar.make(nestedScrollView, "Business email cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                } else {
                    if (!Utils.isValidEmail(email)) {
                        Snackbar.make(nestedScrollView, "Business email is not valid", Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                }

                if (TextUtils.isEmpty(address)) {
                    Snackbar.make(nestedScrollView, "Business Address cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(phone)) {
                    Snackbar.make(nestedScrollView, "Business Phone cannot be empty", Snackbar.LENGTH_SHORT).show();
                    return;
                } else if (phone.length() < 9) {
                    Snackbar.make(nestedScrollView, "Business Phone is not formatted properly", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                if (uri != null) {
                    progressBar.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                    save.setEnabled(false);

                    Snackbar.make(nestedScrollView,"Updating data",Snackbar.LENGTH_LONG).show();
                    StorageReference storage = FirebaseStorage.getInstance().getReference().child("businesses_photos").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    storage.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            Uri uri = task.getResult().getDownloadUrl();

                            Map<String, Object> map = new HashMap<String, Object>();
                            map.put("photo", String.valueOf(uri));
                            map.put("phone", phone);
                            map.put("semail", email);
                            map.put("address", address);
                            map.put("website", bwebsite.getText().toString().trim());
                            map.put("bname", nametext);
                            FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .update(map).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {

                                    progressBar.setVisibility(View.GONE);
                                    save.setVisibility(View.VISIBLE);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    });

                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    save.setVisibility(View.VISIBLE);
                    save.setEnabled(false);

                    Log.d(TAG, "onClick: No photo chosen");
                    Map<String, Object> map = new HashMap<>();
                    map.put("photo",String.valueOf( uri));
                    map.put("phone", phone);
                    map.put("semail", email);
                    map.put("category",String.valueOf(cat.getSelectedItem()));
                    map.put("address", address);
                    map.put("website", bwebsite.getText().toString().trim());
                    map.put("bname", nametext);
                    FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update(map).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {

                            progressBar.setVisibility(View.GONE);
                            save.setVisibility(View.VISIBLE);
                            save.setEnabled(true);
                            startActivity(new Intent(getBaseContext(), BusinessAccountConfigActivity.class));


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            save.setVisibility(View.VISIBLE);
                            Toast.makeText(EditBusinessProfileActivity.this, "Something went wrong"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }

                    });


                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_CODE) {
            uri = data.getData();
            bphoto.setImageURI(uri);

        }
    }

    private void initComponents() {
        bphoto = findViewById(R.id.bssphoto);
        baddress = findViewById(R.id.baddress);
        bemail = findViewById(R.id.bsemail);
        bphone = findViewById(R.id.bnumber);
        bwebsite = findViewById(R.id.bwebsite);
        bname=findViewById(R.id.bname);
        progressBar = findViewById(R.id.progress_bar);
        save = findViewById(R.id.btsave);
        nestedScrollView = findViewById(R.id.nested_scroll_view);
        fabpick=findViewById(R.id.fabpick);
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    private void openCamera() {
        Log.d(TAG, "openCamera: ");
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    private void setData() {
        FirebaseFirestore.getInstance().collection("businesses").document(FirebaseAuth.getInstance()
                .getCurrentUser().getUid()).addSnapshotListener((documentSnapshot, e) -> {
            bname.setText(documentSnapshot.get("name").toString());
            bemail.setText(documentSnapshot.get("semail").toString());

            uri=Uri.parse(documentSnapshot.get("photo").toString());
            Glide.with(getBaseContext()).load(uri).into(bphoto);
            bphone.setText(documentSnapshot.get("phone").toString());
            baddress.setText(documentSnapshot.get("address").toString());
            bwebsite.setText(documentSnapshot.get("website").toString());

        });
    }


}
