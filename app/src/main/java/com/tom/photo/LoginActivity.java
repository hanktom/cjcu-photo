package com.tom.photo;

import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void login(View v){
        EditText edEmail = (EditText) findViewById(R.id.ed_email);
        EditText edPasswd = (EditText) findViewById(R.id.ed_passwd);
        String email = edEmail.getText().toString();
        String passwd = edPasswd.getText().toString();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, passwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "登入成功", Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    new AlertDialog.Builder(LoginActivity.this)
                            .setMessage("登入失敗")
                            .setPositiveButton("OK", null)
                            .show();
                }
            }
        });
    }
}
