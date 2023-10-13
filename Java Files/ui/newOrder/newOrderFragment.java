package com.example.iot_proj.ui.newOrder;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.CalendarView;
import android.widget.Toast;
import com.example.iot_proj.R;
import com.example.iot_proj.ui.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class newOrderFragment extends Fragment {
    private CalendarView datePicker;
    private NumberPicker floorPicker;
    private NumberPicker chairPicker;
    private Button submitButton;
    private List<List<Integer>> seats;

    public newOrderFragment() {
        // Required empty public constructor
    }
    public static newOrderFragment newInstance() {
        return new newOrderFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        NavController navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_activity_main);
        if (navController.getCurrentDestination().getId() == R.id.navigation_my_orders) {
            // If currently on the "navigation_my_orders" fragment, navigate to the previous fragment
            navController.navigate(R.id.newOrderFragment);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        SharedPreferences sp = getActivity().getSharedPreferences("MyApplication", Context.MODE_PRIVATE);
        SharedPreferences.Editor sedt = sp.edit();
        String UserId = sp.getString("UserId", null);
        View view = inflater.inflate(R.layout.fragment_new_order, container, false);

        datePicker = view.findViewById(R.id.datePicker);
        floorPicker = view.findViewById(R.id.floorPicker);
        chairPicker = view.findViewById(R.id.chairPicker);
        submitButton = view.findViewById(R.id.submitButton);
        final boolean[] Ready = {false};
        floorPicker.setEnabled(false);
        chairPicker.setEnabled(false);
        floorPicker.setValue(0);
        chairPicker.setValue(0);
        datePicker.setMinDate(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Set the calendar's time to the current date and time
        calendar.add(Calendar.DAY_OF_MONTH, 30); // Add 30 days to the calendar
        long updatedTimeMillis = calendar.getTimeInMillis(); // Get the updated time in milliseconds
        datePicker.setMaxDate(updatedTimeMillis);
        datePicker.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, dayOfMonth);
                long milliseconds = calendar.getTimeInMillis();
                datePicker.setDate(milliseconds);
                String selectedDate = String.format("%02d/%02d/%04d", month + 1, dayOfMonth, year);
                System.out.println(selectedDate);
                chairPicker.setMinValue(0);
                chairPicker.setMaxValue(0);
                chairPicker.setValue(0);
                floorPicker.setMinValue(0);
                floorPicker.setMaxValue(0);
                floorPicker.setValue(0);
                floorPicker.setEnabled(true);
                chairPicker.setEnabled(false);
                floorPicker.setMinValue(1);
                floorPicker.setMaxValue(3);
            }
        });

        floorPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int selectedFloor) {
                seats = new ArrayList<>();
                List<Integer> floor1Seats = new ArrayList<>();
                for (int i = 100; i <= 110; i++) {
                    floor1Seats.add(i);
                }
                seats.add(floor1Seats); // floor 1
                List<Integer> floor2Seats = new ArrayList<>();
                for (int i = 200; i <= 210; i++) {
                    floor2Seats.add(i);
                }
                seats.add(floor2Seats); // floor 2
                List<Integer> floor3Seats = new ArrayList<>();
                for (int i = 300; i <= 310; i++) {
                    floor3Seats.add(i);
                }
                seats.add(floor3Seats);

                DatabaseReference ref = database.getReference("Books");
                ref.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        long selectedDateMillis = datePicker.getDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(selectedDateMillis);
                        int selectedYear = calendar.get(Calendar.YEAR);
                        int selectedMonth = calendar.get(Calendar.MONTH);
                        int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
                        String selectedDate = String.format(Locale.US, "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);
                        //Get map of Books in datasnapshot
                        Map<String, Object> booksMap = (Map<String, Object>) dataSnapshot.getValue();
                        if(booksMap != null) {
                            for (Map.Entry<String, Object> entry : booksMap.entrySet()) {
                                String bookId = entry.getKey();
                                Map<String, Object> bookData = (Map<String, Object>) entry.getValue();
                                Long floorLong = (Long) bookData.get("floor");
                                int floor = floorLong.intValue();
                                String dbDate = (String) bookData.get("date");
                                if(floor == selectedFloor && selectedDate.equals(dbDate)) {
                                    Long chairLong = (Long) bookData.get("chair");
                                    int chair = chairLong.intValue();
                                    System.out.println(chair);
                                    List<Integer> floorSeats = seats.get(floor - 1);
                                    floorSeats.remove(Integer.valueOf(chair));
                                }
                            }
                        }
                        chairPicker.setEnabled(true);
                        chairPicker.setMinValue(0);
                        chairPicker.setMaxValue(0);
                        int selectedFloorIndex = selectedFloor - 1;
                        List<Integer> floorSeats = seats.get(selectedFloorIndex);
                        String[] stringArray = new String[floorSeats.size()];
                        System.out.println(floorSeats.size());
                        for (int i = 0; i < floorSeats.size(); i++) {
                            stringArray[i] = String.valueOf(floorSeats.get(i));
                        }
                        chairPicker.setDisplayedValues(stringArray);
                        chairPicker.setMinValue(0);
                        chairPicker.setMaxValue(floorSeats.size() - 1);
                        Ready[0] = true;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });

            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println(Ready[0]);
                if(Ready[0] == false){
                    Toast.makeText(requireContext(),"You didnt select floor and chair!", Toast.LENGTH_SHORT).show();
                    return;
                }
                int selectedFloor = floorPicker.getValue();
                int selectedChairIndex = chairPicker.getValue();
                List<Integer> floorSeats = seats.get(selectedFloor - 1);
                int selectedChair = floorSeats.get(selectedChairIndex);
                long selectedDateMillis = datePicker.getDate();
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(selectedDateMillis);
                int selectedYear = calendar.get(Calendar.YEAR);
                int selectedMonth = calendar.get(Calendar.MONTH);
                int selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
                String selectedDate = String.format(Locale.US, "%02d/%02d/%04d", selectedMonth + 1, selectedDay, selectedYear);

                DatabaseReference Ref = database.getReference("Books");
                Ref.addListenerForSingleValueEvent(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of Books in datasnapshot
                        Map<String, Object> booksMap = (Map<String, Object>) dataSnapshot.getValue();
                        if(booksMap != null) {
                            for (Map.Entry<String, Object> entry : booksMap.entrySet()) {
                                String bookId = entry.getKey();
                                Map<String, Object> bookData = (Map<String, Object>) entry.getValue();
                                String userId = (String) bookData.get("userId");
                                String dbDate = (String) bookData.get("date");
                                System.out.println(dbDate + "   " + selectedDate);
                                if(selectedDate.equals(dbDate) && userId.equals(UserId)) {
                                    System.out.println("dates equals");
                                    Toast.makeText(requireContext(),"Can't order 2 chairs in the same date!", Toast.LENGTH_SHORT).show();
                                    Ready[0] = false;
                                }
                            }
                        }
                        if(Ready[0] == false) {
                            return;
                        }
                        Ref.push().setValue(new Book(UserId, selectedDate, selectedFloor, selectedChair), new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    // Handle the error here
                                    Toast.makeText(requireContext(),"Data could not be saved", Toast.LENGTH_SHORT).show();
                                } else
                                {
                                    // Data saved successfully
                                    Toast.makeText(requireContext(),"Order submitted", Toast.LENGTH_SHORT).show();
                                    Ready[0] = false;
                                    navController.popBackStack();
                                    navController.navigate(R.id.navigation_my_orders);
                                }
                            }
                        });
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
                floorPicker.setMinValue(0);
                floorPicker.setMaxValue(0);
                floorPicker.setValue(0);
                floorPicker.setEnabled(false);
                chairPicker.setMinValue(0);
                chairPicker.setMaxValue(0);
                chairPicker.setValue(0);
                chairPicker.setEnabled(false);
            }
        });
        return view;
    }
}