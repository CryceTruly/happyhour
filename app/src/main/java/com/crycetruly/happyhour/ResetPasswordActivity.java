package com.crycetruly.happyhour;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crycetruly.happyhour.utils.Tools;
import com.crycetruly.happyhour.utils.Utils;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
private TextInputEditText mainedit;
private Button send;
private TextView textView;
ProgressDialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        initToolbar();
        initSecond();
textView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        startActivity(new Intent(getBaseContext(),MainActivity.class));
    }
});
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mail=mainedit.getText().toString().toLowerCase().trim();


                if (!TextUtils.isEmpty(mail)){
                    if (Utils.isValidEmail(mail)){
                        dialog.setMessage("Please wait");
                        dialog.show();
                        dialog.setCanceledOnTouchOutside(false);
                        FirebaseAuth.getInstance().sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dialog.dismiss();
                                Toast.makeText(ResetPasswordActivity.this, "Password reset email was sent to "+mail,
                                        Toast.LENGTH_SHORT).show();
                                Toast.makeText(ResetPasswordActivity.this, "Please vist your mail box", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(ResetPasswordActivity.this, "Failed: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }else {
                        Toast.makeText(ResetPasswordActivity.this, "Invalid Email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void initSecond() {
        mainedit=findViewById(R.id.enter);
        dialog=new ProgressDialog(this);
        send=findViewById(R.id.ok);
        textView=findViewById(R.id.neww);
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(), item.getTitle(), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

}