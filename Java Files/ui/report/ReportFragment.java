package com.example.iot_proj.ui.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.example.iot_proj.R;
import com.example.iot_proj.databinding.FragmentReportBinding;
import java.util.ArrayList;
import java.util.List;

public class ReportFragment extends Fragment {
    private Spinner chooseFloorSpinner;
    private Spinner chooseChairSpinner;
    private Spinner chooseCategorySpinner;
    private EditText AdditionalInfo;
    private EditText phoneNumber;
    private String Floor;
    private String Chair;
    private String Category;

    public ReportFragment() {
        // Required empty public constructor
    }

    private FragmentReportBinding binding;
    private List<List<Integer>> seats;

    public static ReportFragment newInstance(String param1, String param2) {
        ReportFragment fragment = new ReportFragment();
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        binding = FragmentReportBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        SharedPreferences sp = getActivity().getSharedPreferences("MyApplication", Context.MODE_PRIVATE);
        SharedPreferences.Editor sedt = sp.edit();
        String UserId = sp.getString("UserId", null);
        String UserName = sp.getString("UserName", null);

        AdditionalInfo = root.findViewById(R.id.AdditionalInfo);
        phoneNumber = root.findViewById(R.id.phoneNumber);
        chooseFloorSpinner = root.findViewById(R.id.chooseFloorSpinner);
        chooseChairSpinner = root.findViewById(R.id.chooseChairSpinner);
        chooseCategorySpinner = root.findViewById(R.id.chooseCategorySpinner);

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

        List<String> floorList = new ArrayList<>();
        floorList.add("1");
        floorList.add("2");
        floorList.add("3");

        ArrayAdapter<String> floorAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, floorList);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseFloorSpinner.setAdapter(floorAdapter);
        chooseFloorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFloor = parent.getItemAtPosition(position).toString();
                List<Integer> floorSeats = seats.get((Integer.parseInt(selectedFloor) - 1));
                String[] chairArray = new String[floorSeats.size()];
                System.out.println(floorSeats.size());
                for (int i = 0; i < floorSeats.size(); i++) {
                    chairArray[i] = String.valueOf(floorSeats.get(i));
                }
                ArrayAdapter<String> chairAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, chairArray);
                chairAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                chooseChairSpinner.setAdapter(chairAdapter);
                Floor = selectedFloor;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });

        String[] categoryList = {"Network problems", "Peripheral equipment", "Furniture item", "Other"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryList);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        chooseCategorySpinner.setAdapter(categoryAdapter);

        chooseChairSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedChair = parent.getItemAtPosition(position).toString();
                Chair = selectedChair;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });

        chooseCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCategory = parent.getItemAtPosition(position).toString();
                Category = selectedCategory;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle the case when no item is selected
            }
        });


        root.findViewById(R.id.reportSubmitButton).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                String AdditionalInfoText = AdditionalInfo.getText().toString();
                String PhoneNumberText = phoneNumber.getText().toString();

                if (PhoneNumberText.equals(""))
                {
                    Toast.makeText(requireContext(), "You must enter phone number!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String smsText = "Hello " + UserName + ".\nYour report has been received.\n"+ "Details:\n"+ "Floor " + Floor + " chair "+Chair +",\nCategory: " + Category + "\nAdditional Info: " + AdditionalInfoText;
                try
                {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(PhoneNumberText, null, smsText, null, null);
                    Toast.makeText(requireContext(), "Your report has been sent!\nYou will get confirmation message to your phone.", Toast.LENGTH_LONG).show();
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
}