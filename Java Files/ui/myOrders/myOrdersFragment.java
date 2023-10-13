package com.example.iot_proj.ui.myOrders;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.iot_proj.EditOrderActivity;
import com.example.iot_proj.R;
import com.example.iot_proj.ui.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class myOrdersFragment extends Fragment {
    private static final int REQUEST_CODE = 1;
    public myOrdersFragment() {
        // Required empty public constructor
    }
    public static myOrdersFragment newInstance(String param1, String param2) {
        myOrdersFragment fragment = new myOrdersFragment();
        return fragment;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                // Refresh or re-render the fragment's UI
                View view = getView(); // Get the fragment's view
                if (view != null) {
                    fetchDataAndUpdateUI(view);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_orders, container, false);
        fetchDataAndUpdateUI(view);
        return view;
    }

    private void fetchDataAndUpdateUI(View view){
        SharedPreferences sp = getActivity().getSharedPreferences("MyApplication", Context.MODE_PRIVATE);
        SharedPreferences.Editor sedt = sp.edit();
        String UserId = sp.getString("UserId", null);
        Map<String,Book> arrayBooks = new LinkedHashMap<>();
        Map<String, Book> bookMap = new HashMap<>();
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1);
        ListView list = (ListView) view.findViewById(R.id.ListView);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("Books");
        Query query = ref.orderByChild("date");

        query.addListenerForSingleValueEvent(new ValueEventListener()
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
                        if(userId.equals(UserId)) {
                            Long floorLong = (Long) bookData.get("floor");
                            int floor = floorLong.intValue();
                            String dbDate = (String) bookData.get("date");
                            Long chairLong = (Long) bookData.get("chair");
                            int chair = chairLong.intValue();
                            bookMap.put(bookId,new Book(userId,dbDate,floor,chair));
                        }
                    }
                }
                int counter = 1;
                List<Map.Entry<String, Book>> entryList = new ArrayList<>(bookMap.entrySet());
                // Sort the list by date using a custom comparator
                Collections.sort(entryList, Comparator.comparing(entry -> entry.getValue().getDate()));
                for (Map.Entry<String, Book> entry : entryList)  {
                    String orderNumberView = "Order #" + counter;
                    String bookId = entry.getKey();
                    Book book = entry.getValue();
                    String userId = book.getUserId();
                    String dbDate = book.getDate();
                    int floor = book.getFloor();
                    int chair = book.getChair();
                    arrayBooks.put(bookId,new Book(userId,dbDate,floor,chair));
                    adapter.add(orderNumberView + ": Date: " + dbDate + " Floor: "+ floor + " Chair: " + chair);
                    counter++;
                }
                list.setAdapter(adapter);
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override public void onItemClick(AdapterView<?> parent, View view, int
                            position, long id) {
                        Book SelectedBook = (Book)arrayBooks.values().toArray()[position];
                        Intent intent = new Intent(requireActivity(), EditOrderActivity.class);
                        intent.putExtra("BookId", (String) arrayBooks.keySet().toArray()[position] );
                        intent.putExtra("UserId", (String) SelectedBook.getUserId());
                        intent.putExtra("Date", (String) SelectedBook.getDate());
                        intent.putExtra("Floor", (int) SelectedBook.getFloor());
                        intent.putExtra("Chair", (int) SelectedBook.getChair());
                        startActivityForResult(intent, REQUEST_CODE);
                    }});
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //handle databaseError
            }
        });
    }
}