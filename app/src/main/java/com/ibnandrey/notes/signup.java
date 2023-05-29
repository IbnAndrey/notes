package com.ibnandrey.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class signup extends AppCompatActivity {


    private EditText msignupemail,msignuppassword;
    private RelativeLayout msignup;
    private TextView mgotologin;


    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        msignupemail=findViewById(R.id.signupemail);
        msignuppassword=findViewById(R.id.signuppassword);
        msignup=findViewById(R.id.signup);
        mgotologin=findViewById(R.id.gotologin);

        firebaseAuth= FirebaseAuth.getInstance();





        mgotologin.setOnClickListener(v -> {
            Intent intent=new Intent(signup.this,MainActivity.class);
            startActivity(intent);
        });

        msignup.setOnClickListener(v -> {

            String mail=msignupemail.getText().toString().trim();
            String password=msignuppassword.getText().toString().trim();

            if(mail.isEmpty() || password.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Заповніть всі поля",Toast.LENGTH_SHORT).show();
            }
            else if(password.length()<8)
            {
                Toast.makeText(getApplicationContext(),"Пароль повинен мати не менше 8 символів",Toast.LENGTH_SHORT).show();
            }
            else
            {

                firebaseAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(task -> {

                    if(task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Реєстрація успішна",Toast.LENGTH_SHORT).show();
                        sendEmailVerification();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Помилка реєстрації",Toast.LENGTH_SHORT).show();
                    }


                });

            }

        });


    }

    private void sendEmailVerification()
    {
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if(firebaseUser!=null)
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
                Toast.makeText(getApplicationContext(),"Перевірте поштову скриньку для підтвердження",Toast.LENGTH_SHORT).show();
                firebaseAuth.signOut();
                finish();
                startActivity(new Intent(signup.this,MainActivity.class));
            });
        }

        else
        {
            Toast.makeText(getApplicationContext(),"Помилка підтвердження пошти",Toast.LENGTH_SHORT).show();
        }
    }


}