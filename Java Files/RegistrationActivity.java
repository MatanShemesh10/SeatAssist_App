package com.example.iot_proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.example.iot_proj.ui.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity
{
    boolean isEmailExists=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        findViewById(R.id.submitButton_register).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                EditText userNameInput_register = findViewById(R.id.userNameInput_register);
                EditText userPasswordInput_register = findViewById(R.id.userPasswordInput_register);
                EditText userEmailInput_register = findViewById(R.id.userEmailInput_register);

                String enteredUserName = userNameInput_register.getText().toString();
                String enteredUserPassword = userPasswordInput_register.getText().toString();
                String enteredUserEmail = userEmailInput_register.getText().toString();
                System.out.println("Entered user name: " + enteredUserName);
                System.out.println("Entered user password: " + enteredUserPassword);
                System.out.println("Entered user email: " + enteredUserEmail);

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference ref = database.getReference("Users");
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        if (enteredUserEmail.equals("") || enteredUserPassword.equals("") || enteredUserName.equals(""))
                        {
                            Toast.makeText(RegistrationActivity.this, "Avoid entering empty fields please!", Toast.LENGTH_SHORT).show();
                            Intent intent3 = new Intent(RegistrationActivity.this, RegistrationActivity.class);
                            startActivity(intent3);
                            return;
                        }
                        for (DataSnapshot userSnapshot : dataSnapshot.getChildren())
                        {
                            String userId = userSnapshot.getKey();
                            Map<String, Object> userData = (Map<String, Object>) userSnapshot.getValue();
                            String email = (String) userData.get("email");
                            if (email.equals(enteredUserEmail))
                            {
                                // email match
                                isEmailExists = true;
                                break;
                            }
                        }
                        if (isEmailExists)
                        {
                            // email already exists
                            Toast.makeText(RegistrationActivity.this, "This email is already taken! try to sign in or use other email.", Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent(RegistrationActivity.this, RegistrationActivity.class);
                            startActivity(intent1);
                        }
                        else
                        {
                            // add new user
                            ref.push().setValue(new User(enteredUserEmail,enteredUserPassword,enteredUserName));
                            Toast.makeText(RegistrationActivity.this, "Registration done successfully! You can now login to your new user.", Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(intent2);
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
