package com.example.tailor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    FloatingActionButton f;
    Button btn, deleteButton, selectDate,viewAll;
    SQLiteDatabase db;
    EditText inputRegNo;
    Spinner spinner;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        db = openOrCreateDatabase("TailorDB", Context.MODE_PRIVATE, null);
        btn = (Button) findViewById(R.id.viewData);
        deleteButton = findViewById(R.id.deleteButton);
        btn.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        selectDate = (Button) findViewById(R.id.selectDate);
        selectDate.setOnClickListener(this);
        inputRegNo = findViewById(R.id.input_reg_no);
        f = (FloatingActionButton) findViewById(R.id.addDetails);
        f.setOnClickListener(this);
        spinner = (Spinner) findViewById(R.id.spinner);
        viewAll=findViewById(R.id.viewAll);
        viewAll.setOnClickListener(this);

        inputRegNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // This method is called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // This method is called when the text is changed
                String enteredRegNo = s.toString().trim();
                if (enteredRegNo.length() == 4) {
                    // Query the database for details of the entered registration number
                    Cursor cursor = db.rawQuery("SELECT * FROM tailordb2 WHERE reg_no = ?", new String[]{enteredRegNo});
                    if (cursor.getCount() == 0) {
                        showMessage("Error", "No record found for the entered Registration Number.");
                    } else {

                        StringBuffer buffer = new StringBuffer();
                        while (cursor.moveToNext()) {
                            // Extract details from the cursor
                            buffer.append("Name: " + cursor.getString(1) + "\n");
                            buffer.append("Reg No: " + cursor.getString(2) + "\n");
                            buffer.append("Mobile Number: " + cursor.getString(3) + "\n");
                            buffer.append("Given Date: " + cursor.getString(4) + "\n");
                            buffer.append("Due Date: " + cursor.getString(5) + "\n\n");

                            // Display details in AlertDialog
                        }
                        showMessage("Customer Details", buffer.toString());
                    }
                }

            }


            @Override
            public void afterTextChanged(Editable s) {
                // This method is called after the text is changed
            }
        });
        setupSpinner();
    }

    @Override
    public void onClick(View v1) {
        if (v1 == btn) {
            Cursor c = db.rawQuery("SELECT * FROM tailordb2", null);
            if (c.getCount() == 0) {
                showMessage("Error", "No records found");
                return;
            }
            StringBuffer buffer = new StringBuffer();
            while (c.moveToNext()) {
                buffer.append("Id: " + c.getString(0) + "\n");
                buffer.append("Name: " + c.getString(1) + "\n");
                buffer.append("Reg_no: " + c.getString(2) + "\n");
                buffer.append("Mobile_no: " + c.getString(3) + "\n");
                buffer.append("Start_data: " + c.getString(4) + "\n");
                buffer.append("end_date: " + c.getString(5) + "\n\n");
            }
            showMessage("Customer Details", buffer.toString());
        }
        if (v1 == deleteButton) {
            showDeleteDialog();
        }
        if (v1 == selectDate) {
            startActivity(new Intent(MainActivity.this, selectDateActivity.class));
        }
        if (v1 == f) {
            startActivity(new Intent(MainActivity.this, AddBottonActivity.class));
        }
        if(v1==viewAll)
        {
             startActivity(new Intent(MainActivity.this, ViewAll.class));
        }


    }

    private void showDeleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Registration Number");

        // Set up the input
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String regNo = input.getText().toString();
                deleteRecord(regNo);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void deleteRecord(String regNo) {
        // Execute SQL DELETE query to delete the record with the given registration number
        db.execSQL("DELETE FROM tailordb2 WHERE reg_no = '" + regNo + "'");
        showMessage("Success", "Record with registration number " + regNo + " deleted successfully");
    }
    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message); // Set HTML formatted message
        builder.show();
    }
    private void fetchDataForNextDays(int numDays) throws ParseException {
        // Calculate the end date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Calculate the end date
        String endDate = calculateEndDate(numDays, currentDate);

        // Construct the query to fetch data for the selected number of days
        String query = "SELECT * FROM tailordb2 WHERE end_date BETWEEN ? AND ?";

        // Execute the query
        Cursor cursor = db.rawQuery(query, new String[]{currentDate, endDate});

        // Check if the cursor is valid and contains data
        if (cursor != null && cursor.moveToFirst()) {
            // Create a StringBuilder to store the fetched data
            StringBuilder dataBuilder = new StringBuilder();

            // Populate StringBuilder with the fetched data
            do {
                // Retrieve the name and registration number from the cursor
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Cname"));
                @SuppressLint("Range") String regNo = cursor.getString(cursor.getColumnIndex("reg_no"));
                // Append the data to the StringBuilder
                dataBuilder.append("Name: ").append(name).append(", Reg No: ").append(regNo).append("\n\n");
            } while (cursor.moveToNext());

            // Close the cursor
            cursor.close();

            // Create and show AlertDialog with the fetched data
            showMessage("Fetched Data", dataBuilder.toString());
        } else {
            // Show an error message if the cursor is null or empty
            showMessage("Error", "No data found for the selected period.");
        }
    }



    private String calculateEndDate(int days, String currentDate) throws ParseException {
        // Parse the current date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date currentDateObj = dateFormat.parse(currentDate);

        // Calculate the end date by adding the specified number of days
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDateObj);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return dateFormat.format(calendar.getTime());
    }



    private void setupSpinner() {
        String[] daysOptions = {"Select Date", "1 day", "2 days", "3 days", "4 days", "5 days"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, daysOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    int numDays = position;
                    try {
                        fetchDataForNextDays(numDays);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


}




