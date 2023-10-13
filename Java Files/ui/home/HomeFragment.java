package com.example.iot_proj.ui.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.iot_proj.ExplanationVideoActivity;
import com.example.iot_proj.R;
import com.example.iot_proj.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private TextView userNameTextBox;
    private TextView Info;
    private TextView upcomingOrder;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences sp = getActivity().getSharedPreferences("MyApplication", Context.MODE_PRIVATE);
        SharedPreferences.Editor sedt = sp.edit();
        String UserId = sp.getString("UserId", null);
        String UserName = sp.getString("UserName", null);

        userNameTextBox = root.findViewById(R.id.userName);
        upcomingOrder = root.findViewById(R.id.upcomingOrder);
        Info = root.findViewById(R.id.InfoHome);
        userNameTextBox.setText("Welcome " + UserName+" !");
        Info.setText("Start your booking right now.\n\nThe process is simple, you choose a date and an available seat at the office and wait for approval.\nEnjoy!");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Books");
        Query query = ref.orderByChild("userId").equalTo(UserId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                Date MinDate = null;
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                Map<String, Object> booksMap = (Map<String, Object>) dataSnapshot.getValue();
                String floor = null;
                String chair = null;
                String finalDate = null;
                if (booksMap != null)
                {
                    for (Map.Entry<String, Object> entry : booksMap.entrySet())
                    {
                        String bookId = entry.getKey();
                        Map<String, Object> bookData = (Map<String, Object>) entry.getValue();
                        String dbDate = (String) bookData.get("date");
                        Date date;
                        try {
                            date = dateFormat.parse(dbDate);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                        if (MinDate == null || date.compareTo(MinDate) < 0) {
                            MinDate = date;
                            finalDate = dbDate;
                            Long floorLong = (Long) bookData.get("floor");
                            floor = floorLong.toString();
                            Long chairLong = (Long) bookData.get("chair");
                            chair = chairLong.toString();
                        }

                    }

                    upcomingOrder.setText("Your nearest booking:\nDate: " + finalDate + "\nFloor: " + floor + "\nChair:" + chair);
                }
                else
                {
                    upcomingOrder.setText("You don't have an upcoming order!");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });

        root.findViewById(R.id.show_explanation_video).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Intent intent = new Intent(requireActivity(), ExplanationVideoActivity.class);
                    startActivity(intent);
                }
                catch (Exception ex)
                {
                    Toast.makeText(requireContext(),ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                    ex.printStackTrace();
                }
            }
        });

        return root;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}