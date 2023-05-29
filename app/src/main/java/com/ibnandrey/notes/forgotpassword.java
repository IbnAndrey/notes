package com.ibnandrey.notes;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

public class forgotpassword extends AppCompatActivity {

    private EditText mforgotpassword;
    private Button mpasswordrecoverbutton;
    private TextView mgobacktologin;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fogotpassword);

        getSupportActionBar().hide();

        mforgotpassword=findViewById(R.id.forgotpassword);
        mpasswordrecoverbutton=findViewById(R.id.passwordrecoverbutton);
        mgobacktologin=findViewById(R.id.gobacktologin);

        firebaseAuth= FirebaseAuth.getInstance();


        mgobacktologin.setOnClickListener(v -> {
            Intent intent=new Intent(forgotpassword.this,MainActivity.class);
            startActivity(intent);
        });

        mpasswordrecoverbutton.setOnClickListener(v -> {
            String mail=mforgotpassword.getText().toString().trim();
            if(mail.isEmpty())
            {
                Toast.makeText(getApplicationContext(),"Введіть Email",Toast.LENGTH_SHORT).show();
            }
            else
            {

                firebaseAuth.sendPasswordResetEmail(mail).addOnCompleteListener(task -> {

                    if (task.isSuccessful())
                    {
                        Toast.makeText(getApplicationContext(),"Лист з відновленням пароля надіслано",Toast.LENGTH_SHORT).show();
                        finish();
                        startActivity(new Intent(forgotpassword.this,MainActivity.class));
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),"Невірний Email",Toast.LENGTH_SHORT).show();
                    }


                });

            }
        });


    }
}