package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.ProgressView;

public class MainActivity extends AppCompatActivity {
    private Button loginbutton, registerbutton;
    private ProgressView circularProgress;
    private String parentDatabase = "Users";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginbutton=(Button)findViewById(R.id.welcome_login);
        registerbutton=(Button)findViewById(R.id.welcome_register);
        circularProgress = (ProgressView)findViewById(R.id.welcome_progressbar);
        circularProgress.setVisibility(View.INVISIBLE);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //allow Auto Acess to home activity by remeber password option
        Paper.init(this);

        String userDataPhone = Paper.book().read(Prevalent.userPhoneKey);
        String userDataPassword = Paper.book().read(Prevalent.userPasswordKey);

        if(userDataPassword != "" && userDataPhone != "")
        {
            if(!TextUtils.isEmpty(userDataPhone) && !TextUtils.isEmpty(userDataPassword))
            {
                circularProgress.setVisibility(View.VISIBLE);
                Toast.makeText(this, "Plese wait ... ", Toast.LENGTH_SHORT).show();
                allowAccess(userDataPhone,userDataPassword);
            }
        }
    }

    private void allowAccess(final String phone, final String password) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(parentDatabase).child(phone).exists())
                {
                    Users userData = dataSnapshot.child(parentDatabase).child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            circularProgress.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            circularProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Incorrect Password..Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                /* else if(dataSnapshot.child("Admins").child(phone).exists())
                {
                    Users userData = dataSnapshot.child("Admins").child(phone).getValue(Users.class);

                    if(userData.getPhone().equals(phone))
                    {
                        if(userData.getPassword().equals(password))
                        {
                            Toast.makeText(MainActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                            circularProgress.setVisibility(View.INVISIBLE);
                            Intent intent = new Intent(MainActivity.this, AdminAddNewProductActivity.class);
                            startActivity(intent);
                        }
                        else
                        {
                            circularProgress.setVisibility(View.INVISIBLE);
                            Toast.makeText(MainActivity.this, "Incorrect Password..Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                } */
                else
                {
                    circularProgress.setVisibility(View.INVISIBLE);
                    Toast.makeText(MainActivity.this, "User with phone number " + phone + "Not Exist Please Register first...", Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                circularProgress.setVisibility(View.INVISIBLE);
                Toast.makeText(MainActivity.this, "Network Error please try after sometimes", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
