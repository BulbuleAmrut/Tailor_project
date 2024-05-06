package com.example.tailor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class selectDateActivity extends AppCompatActivity implements View.OnClickListener {
    Button startDate,endDate,submit;
    String selectedStartDate,selectedEndDate;
    SQLiteDatabase db;
    ListView listView;
    TextView t1;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_select_date);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        endDate=(Button) findViewById(R.id.end_btn);
        submit=(Button) findViewById(R.id.sub);

        endDate.setOnClickListener(this);
        submit.setOnClickListener(this);
         listView = findViewById(R.id.listview);
         t1=(TextView)findViewById(R.id.t1);
        db = openOrCreateDatabase("TailorDB", Context.MODE_PRIVATE, null);
       // fetchDataAndDisplay();

    }

    @Override
    public void onClick(View v) {

            if(v == endDate) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                // Create and show DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                            // Handle date selection
                            selectedEndDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                            showToast(selectedEndDate);
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
                endDate.setText(selectedEndDate);
            }
            if(v == submit) {
                // Check if selectedStartDate and selectedEndDate are not null
                if(selectedEndDate != null) {
                    fetchDataAndDisplay();
                    t1.setText("Selected items within specified Dates");
                } else {
                    // Handle the case where either selectedStartDate or selectedEndDate is null
                    Toast.makeText(this, "Please select start and end dates", Toast.LENGTH_SHORT).show();
                }
            }
        }

    public static String convertDate(String inputDate) {
        String outputDate = "";
        if (inputDate != null) {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date date = inputFormat.parse(inputDate);
                outputDate = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return outputDate;
    }

    public void showToast(String a)
    {
        Toast.makeText(selectDateActivity.this, "Selected date:"+a, Toast.LENGTH_LONG).show();
    }
    private void fetchDataAndDisplay() {

        String inputEnd = selectedEndDate;
        String EndDate = convertDate(inputEnd);



        String query = "SELECT * FROM tailordb2 WHERE end_date = '" + EndDate + "'";




        Cursor cursor = db.rawQuery(query, null);

        // Populate ListView with the fetched data
        ArrayList<String> dataList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                // Assuming "Cname" and "reg_no" are columns in your table
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Cname"));
                @SuppressLint("Range") String regNo = cursor.getString(cursor.getColumnIndex("reg_no"));
                dataList.add(name + " -------- " + regNo);
            } while (cursor.moveToNext());
        }

        // Close cursor after fetching data
        cursor.close();

        // Create custom adapter and set it to ListView
        CustomAdapter adapter = new CustomAdapter(this, dataList);
        listView.setAdapter(adapter);

        // Set item click listener for ListView items
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Handle item click here, you can display details of the selected item
            String selectedItem = dataList.get(position);
            String[] parts = selectedItem.split(" -------- "); // Split the selected item to get name and regNo
            String selectedName = parts[0]; // Extract name
            String selectedRegNo = parts[1]; // Extract registration number

            // Fetch additional details from the database based on the selected name and regNo
            Cursor cursorDetails = db.rawQuery("SELECT * FROM tailordb2 WHERE Cname = ? AND reg_no = ?", new String[]{selectedName, selectedRegNo});
            if (cursorDetails.moveToFirst()) {
                // Extract details from the cursor
                @SuppressLint("Range") String name = cursorDetails.getString(cursorDetails.getColumnIndex("Cname"));
                @SuppressLint("Range") String regNo = cursorDetails.getString(cursorDetails.getColumnIndex("reg_no"));
                @SuppressLint("Range") String phone = cursorDetails.getString(cursorDetails.getColumnIndex("phone"));
                @SuppressLint("Range") String startDate = cursorDetails.getString(cursorDetails.getColumnIndex("start_date"));
                @SuppressLint("Range") String endDate = cursorDetails.getString(cursorDetails.getColumnIndex("end_date"));

                // Prepare message with the fetched details
                String message = "Name: " + name + "\n" +
                        "Reg No: " + regNo + "\n" +
                        "Phone: " + phone + "\n" +
                        "Start Date: " + startDate + "\n" +
                        "End Date: " + endDate;

                // Show alert dialog with the details
                showMessage("Selected Item Details", message);
            } else {
                // Handle case when no details are found
                Toast.makeText(selectDateActivity.this, "No details found for the selected item", Toast.LENGTH_SHORT).show();
            }

            // Close cursor after fetching details
            cursorDetails.close();
        });
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message); // Set HTML formatted message
        builder.show();
    }


}