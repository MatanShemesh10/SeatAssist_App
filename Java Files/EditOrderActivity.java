package com.example.iot_proj;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.NumberPicker;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditOrderActivity extends AppCompatActivity {
    private CalendarView datePicker;
    private NumberPicker floorPicker;
    private NumberPicker chairPicker;
    private Button cancelButton;
    private Button submitButton;
    private String BookId="";
    private String UserId="";
    private String orderDate="";
    private int Floor=0;
    private int Chair=0;
    private List<List<Integer>> seats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        setContentView(R.layout.activity_edit_order);
        Intent iin= getIntent();
        Bundle b = iin.getExtras();

        datePicker = findViewById(R.id.datePicker);
        floorPicker = findViewById(R.id.floorPicker);
        chairPicker = findViewById(R.id.chairPicker);
        cancelButton = findViewById(R.id.CancelOrder);
        submitButton = findViewById(R.id.submitChanges);
        final boolean[] Ready = {false};

        if(b!=null) {
            BookId = (String) b.get("BookId");
            UserId = (String) b.get("UserId");
            orderDate = (String) b.get("Date");
            Floor = (int) b.get("Floor");
            Chair = (int) b.get("Chair");
        }
        Date date = null;
        try {
            date = new SimpleDateFormat("MM/dd/yyyy").parse(orderDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        long OrderDateMillis = date.getTime();

        datePicker.setDate(OrderDateMillis);
        floorPicker.setMinValue(1);
        floorPicker.setMaxValue(3);
        floorPicker.setValue(Floor);
        chairPicker.setMinValue(Chair);
        chairPicker.setMaxValue(Chair);
        chairPicker.setValue(Chair);
        chairPicker.setEnabled(false);
        datePicker.setMinDate(System.currentTimeMillis());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date()); // Set the calendar's time to the current date and time
        calendar.add(Calendar.DAY_OF_MONTH, 30); // Add 30 days to the calendar
        long updatedTimeMillis = calendar.getTimeInMillis();  // Get the updated time in milliseconds
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
                    Toast.makeText(EditOrderActivity.this,"You didnt select floor and chair!", Toast.LENGTH_SHORT).show();
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
                                if(selectedDate.equals(orderDate) && userId.equals(UserId))
                                {
                                    continue;
                                }
                                if(selectedDate.equals(dbDate) && userId.equals(UserId)) {
                                    System.out.println("dates equals");
                                    Toast.makeText(EditOrderActivity.this,"Can't order 2 chairs in the same date!", Toast.LENGTH_SHORT).show();
                                    Ready[0] = false;
                                }
                            }
                        }
                        System.out.println(Ready[0]);
                        if(Ready[0] == false) {
                            return;
                        }
                        DatabaseReference updateRef = database.getReference("Books/" + BookId);

                        Map<String, Object> updates = new HashMap<>();
                        updates.put("date", selectedDate);
                        updates.put("chair", selectedChair);
                        updates.put("floor", selectedFloor);
                        updateRef.updateChildren(updates).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(EditOrderActivity.this,"Order Changes Submited", Toast.LENGTH_SHORT).show();
                                        Ready[0] = false;
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(EditOrderActivity.this,"Data could not be saved", Toast.LENGTH_SHORT).show();
                                        Ready[0] = false;
                                    }
                                });
                        Intent intent = new Intent();
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = database.getReference("Books/" + BookId);

                ref.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditOrderActivity.this,"You have cancel the order!", Toast.LENGTH_SHORT).show();
                                Ready[0] = false;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditOrderActivity.this,"Could not cancel the order!", Toast.LENGTH_SHORT).show();
                            }
                        });

                Intent intent = new Intent();
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });

    }

}