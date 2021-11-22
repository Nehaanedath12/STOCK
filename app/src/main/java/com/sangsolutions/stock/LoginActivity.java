package com.sangsolutions.stock;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.sangsolutions.stock.Database.DatabaseHelper;
import com.sangsolutions.stock.Database.User;
import com.sangsolutions.stock.databinding.ActivityLoginBinding;
import com.sangsolutions.stock.databinding.ActivitySetIPBinding;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    SharedPreferences preferences;
    DatabaseHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        preferences = getSharedPreferences("sync",MODE_PRIVATE);
        helper=new DatabaseHelper(this);

        binding.settings.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this,SetIPActivity.class)));

        if (helper.GetLoginStatus()) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();

        }

        binding.passHide.setOnClickListener(v -> {
            binding.passShow.setVisibility(View.VISIBLE);
            binding.passHide.setVisibility(View.GONE);
            binding.password.setTransformationMethod(PasswordTransformationMethod.getInstance());

        });
        binding.passShow.setOnClickListener(v -> {
            binding.passHide.setVisibility(View.VISIBLE);
            binding.passShow.setVisibility(View.GONE);
            binding.password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        });
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("preferences1",preferences.getString(Commons.PRODUCT_FINISHED, "false")+"  "+
                        preferences.getString(Commons.WAREHOUSE_FINISHED, "false"));
                if(!(new Tools().getIP(LoginActivity.this)).isEmpty()) {

                if(preferences.getString(Commons.PRODUCT_FINISHED, "false").equals("true") &&
                        preferences.getString(Commons.WAREHOUSE_FINISHED, "false").equals("true") ){
                    if(!binding.userName.getText().toString().trim().isEmpty()) {
                        if (!binding.password.getText().toString().trim().isEmpty()) {

                                User u = new User();
                                u.setsLoginName(binding.userName.getText().toString().trim());
                                u.setsPassword(binding.password.getText().toString().trim());

                                if (helper.GetUser()) {
                                    if (helper.loginUser(u)!=null&& helper.loginUser(u).getCount()>0) {

                                        boolean status = helper.InsertCurrentLoginUser(u);
                                        if(status){
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(LoginActivity.this, "An unexpected error occurred!", Toast.LENGTH_SHORT).show();
                                        }

                                    } else {
                                            Toast.makeText(LoginActivity.this, "enter correct username and password", Toast.LENGTH_SHORT).show();
                                        }
                                } else {
                                    Toast.makeText(LoginActivity.this, "Check your network or IP", Toast.LENGTH_SHORT).show();
                                }


                        }
                        else {
                            binding.password.setError("enter Password");
                        }
                    }
                    else {
                        binding.userName.setError("enter Username");
                    }
                }else {
                    Toast.makeText(LoginActivity.this, "Sync Not Completed!", Toast.LENGTH_SHORT).show();
                }
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter IP Address", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}