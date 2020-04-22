package com.example.ecommerce;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import io.paperdb.Paper;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.Model.Users;
import com.example.ecommerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

public class LoginActivity extends AppCompatActivity {
    private Button login_button;
    private EditText phone_input, password_input;
    private TextView admin_link, user_link;
    private ProgressDialog loadingbar;
    private String parentDatabase = "Users";
    private CheckBox remember_me_checkbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_button = (Button)findViewById(R.id.login_login);
        phone_input = (EditText)findViewById(R.id.login_phone_number);
        password_input = (EditText)findViewById(R.id.login_password);
        admin_link = (TextView)findViewById(R.id.login_I_am_admin);
        user_link = (TextView)findViewById(R.id.login_I_am_user);
        loadingbar = new ProgressDialog(this);
        remember_me_checkbox = (CheckBox)findViewById(R.id.login_checkbox);


        admin_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_button.setText("Login Admin");
                admin_link.setVisibility(View.INVISIBLE);
                user_link.setVisibility(View.VISIBLE);
                parentDatabase = "Admins";
            }
        });

        user_link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_button.setText("Login");
                admin_link.setVisibility(View.VISIBLE);
                user_link.setVisibility(View.INVISIBLE);
                parentDatabase = "Users";
            }
        });



        Paper.init(this);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    private void loginUser() {

        final String phone = phone_input.getText().toString();
        String password = password_input.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Enter the Phone Number...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Enter the Password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingbar.setTitle("Login Account");
            loadingbar.setMessage("please wait we are checking the credentials");
            loadingbar.setCanceledOnTouchOutside(false);
            loadingbar.show();

            allowAccessToAccount(phone, password);
        }

    }

    private void allowAccessToAccount(final String phone, final String password) {

        if(remember_me_checkbox.isChecked())
        {
            Paper.book().write(Prevalent.userPhoneKey, phone);
            Paper.book().write(Prevalent.userPasswordKey, password);
        }

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
                            if(parentDatabase.equals("Users"))
                            {
                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                Prevalent.currentOnlineUser = userData;
                                startActivity(intent);
                            }
                            else if(parentDatabase.equals("Admins"))
                            {
                                Toast.makeText(LoginActivity.this, "Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                                Intent intent = new Intent(LoginActivity.this, AdminCategoryActivity.class);
                                startActivity(intent);
                            }
                        }
                        else
                        {
                            loadingbar.dismiss();
                            Toast.makeText(LoginActivity.this, "Incorrect Password..Try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(LoginActivity.this, "User with phone number " + phone + "Not Exist Please Register first...", Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(LoginActivity.this, "Network Error please try after sometimes", Toast.LENGTH_SHORT).show();

            }
        });
    }
}
