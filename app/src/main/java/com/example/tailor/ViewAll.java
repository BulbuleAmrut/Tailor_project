package com.example.tailor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ViewAll extends AppCompatActivity {
    SQLiteDatabase db;
    EditText e1;
    Button search;
    ListView listView;
    ArrayList<String> customerList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_all);

        db = openOrCreateDatabase("TailorDB", Context.MODE_PRIVATE, null);
        e1 = findViewById(R.id.editTextText);
        search = findViewById(R.id.button);
        listView = findViewById(R.id.listView);
        customerList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, customerList);
        listView.setAdapter(adapter);

        // Hide the ListView initially
        listView.setVisibility(View.GONE);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enteredText = e1.getText().toString().trim();
                if (!enteredText.isEmpty()) {
                    // Query the database for details of the entered registration number or name
                    Cursor cursor = db.rawQuery("SELECT * FROM tailordb2 WHERE reg_no = ? OR Cname LIKE ?", new String[]{enteredText, "%" + enteredText + "%"});

                    if (cursor.getCount() == 0) {
                        showMessage("Error", "No record found for the entered information.");
                    } else {
                        // Clear the customer list before loading new data
                        customerList.clear();

                        while (cursor.moveToNext()) {
                            // Extract details from the cursor and add them to the customer list
                            String name = cursor.getString(cursor.getColumnIndex("Cname"));
                            String regNo = cursor.getString(cursor.getColumnIndex("reg_no"));
                            // Add more fields as needed

                            customerList.add(name + " - " + regNo);
                        }

                        // Notify the adapter that the data set has changed
                        adapter.notifyDataSetChanged();

                        // Show the ListView
                        listView.setVisibility(View.VISIBLE);
                    }
                    cursor.close();
                }
            }
        });

        // Handle item click event
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show delivery option for the selected customer
                String selectedCustomer = customerList.get(position);
                showDeliveryOption(selectedCustomer);
            }
        });
    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void showDeliveryOption(String selectedCustomer) {
        // Extract the registration number from the selected customer string
        String regNo = selectedCustomer.split(" - ")[1];

        // Query the database to fetch all details of the selected customer
        Cursor cursor = db.rawQuery("SELECT * FROM tailordb2 WHERE reg_no = ?", new String[]{regNo});
        if (cursor.moveToFirst()) {
            // Extract details from the cursor
            @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("Cname"));
            @SuppressLint("Range") String mobileNumber = cursor.getString(cursor.getColumnIndex("phone"));
            @SuppressLint("Range") String givenDate = cursor.getString(cursor.getColumnIndex("start_date"));
            @SuppressLint("Range") String dueDate = cursor.getString(cursor.getColumnIndex("end_date"));
            @SuppressLint("Range") String deliveryDate = cursor.getString(cursor.getColumnIndex("delivered_date"));
            @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("status"));

            // Create a message with all the details
            StringBuilder message = new StringBuilder();
            message.append("Name: ").append(name).append("\n");
            message.append("Reg No: ").append(regNo).append("\n");
            message.append("Mobile Number: ").append(mobileNumber).append("\n");
            message.append("Given Date: ").append(givenDate).append("\n");
            message.append("Due Date: ").append(dueDate).append("\n");
            message.append("Delivery Date: ").append(deliveryDate).append("\n");
            message.append("Status: ").append(status).append("\n");

            // Show the details in a dialog
            showMessageWithDeliveryOption("Customer Details", message.toString(), regNo);
        } else {
            showMessage("Error", "Failed to retrieve customer details.");
        }
        cursor.close();
    }

    private void showMessageWithDeliveryOption(String title, String message, String regNo) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message); // Set HTML formatted message

        // Add button to mark delivery if not delivered yet
        builder.setPositiveButton("Delivered", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                markAsDelivered(regNo);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void markAsDelivered(String regNo) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        // Update the database to mark the delivery status as delivered and store the current date
        ContentValues values = new ContentValues();
        values.put("status", "Delivered");
        values.put("delivered_date", currentDate);

        int rowsAffected = db.update("tailordb2", values, "reg_no = ?", new String[]{regNo});
        if (rowsAffected > 0) {
            Toast.makeText(getApplicationContext(), "Delivery recorded successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Failed to record delivery", Toast.LENGTH_SHORT).show();
        }
    }
}
