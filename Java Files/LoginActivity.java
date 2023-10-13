package com.example.iot_proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    boolean isUSERMatched = false;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SharedPreferences sp = getSharedPreferences("MyApplication", 0);
        SharedPreferences.Editor sedt = sp.edit();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users");

        findViewById(R.id.registerButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.LoginButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText userEmailView = findViewById(R.id.userEmail);
                EditText passwordView = findViewById(R.id.userPassword);
                String enteredEmail = userEmailView.getText().toString();
                String enteredPassword = passwordView.getText().toString();

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren()) {
                            String userId = userSnapshot.getKey();
                            Map<String, Object> userData = (Map<String, Object>) userSnapshot.getValue();
                            String email = (String) userData.get("email");
                            String name = (String) userData.get("name");
                            String password = (String) userData.get("password");
                            if (email.equals(enteredEmail) && password.equals(enteredPassword))
                            {
                                // Username and password match
                                isUSERMatched = true;
                                sedt.putString("UserName", name);
                                sedt.putString("UserId", userId);
                                sedt.commit();
                                break;
                            }
                        }

                        if (isUSERMatched) {
                            // Username and password are correct
                            Toast.makeText(LoginActivity.this, "Login success!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        } else {
                            // Username and password are incorrect
                            Toast.makeText(LoginActivity.this, "User email and password are incorrect", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        System.out.println("error");
                    }
                });
            }
        });
    }
}



