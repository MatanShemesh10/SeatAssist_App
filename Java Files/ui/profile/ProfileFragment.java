package com.example.iot_proj.ui.profile;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import com.example.iot_proj.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;
import com.example.iot_proj.databinding.FragmentProfileBinding;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {
    private EditText usernameView;
    private EditText passwordView;
    private EditText emailView;

    private FragmentProfileBinding binding;
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        SharedPreferences sp = getActivity().getSharedPreferences("MyApplication", Context.MODE_PRIVATE);
        SharedPreferences.Editor sedt = sp.edit();
        String UserId = sp.getString("UserId", null);

        usernameView = root.findViewById(R.id.userNameInput);
        passwordView = root.findViewById(R.id.userPasswordInput);
        emailView = root.findViewById(R.id.userEmailInput);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Users/" + UserId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Object> userData = (Map<String, Object>)  snapshot.getValue();
                String oldUsername = (String) userData.get("name");
                String oldPassword = (String) userData.get("password");
                String oldEmail = (String) userData.get("email");
                usernameView.setText(oldUsername);
                passwordView.setText(oldPassword);
                emailView.setText(oldEmail);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        root.findViewById(R.id.submitButton2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newUserName = usernameView.getText().toString();
                String newPassword = passwordView.getText().toString();
                String newEmail = emailView.getText().toString();
                DatabaseReference updateRef = database.getReference("Users/"+UserId);

                Map<String, Object> updates = new HashMap<>();
                updates.put("name", newUserName);
                updates.put("password", newPassword);
                updates.put("email", newEmail);
                updateRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(requireContext(),"Profile Data Changed Successfully!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(requireContext(),"Could Not Update User Data!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        return root;
    }
}