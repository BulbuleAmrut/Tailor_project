package com.example.tailor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddBottonActivity extends AppCompatActivity implements View.OnClickListener {
    EditText name, Reg_no, phone;
    SQLiteDatabase db;
    String selectedStartDate;
    String selectedEndDate;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_botton);

        name = findViewById(R.id.name);
        Reg_no = findViewById(R.id.Reg_no);
        phone = findViewById(R.id.phone);

        findViewById(R.id.start_btn).setOnClickListener(this);
        findViewById(R.id.end_btn).setOnClickListener(this);
        findViewById(R.id.sub).setOnClickListener(this);

        db = openOrCreateDatabase("TailorDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS tailordb2(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Cname VARCHAR," +
                "reg_no VARCHAR," +
                "phone VARCHAR," +
                "start_date DATE," +
                "end_date DATE," +
                "delivered_date DATE," +
                "status VARCHAR DEFAULT 'not delivered yet');");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.start_btn) {
            showDatePickerDialog(true);
        } else if (v.getId() == R.id.end_btn) {
            showDatePickerDialog(false);
        } else if (v.getId() == R.id.sub) {
            validateAndInsertData();

        }
    }

    private void showDatePickerDialog(final boolean isStartDate) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Handle date selection
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    if (isStartDate) {
                        selectedStartDate = selectedDate;
                    } else {
                        selectedEndDate = selectedDate;
                    }
                    showToast("Selected date: " + selectedDate);
                }, year, month, dayOfMonth);
        datePickerDialog.show();
    }

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void validateAndInsertData() {
        String regNo = Reg_no.getText().toString().trim();
        String phoneNumber = phone.getText().toString().trim();

        // Check if any of the fields are empty
        if (name.getText().toString().trim().isEmpty() ||
                regNo.isEmpty() ||
                phoneNumber.isEmpty() ||
                selectedStartDate == null || selectedStartDate.isEmpty() ||
                selectedEndDate == null || selectedEndDate.isEmpty()) {
            showMessage("Error", "Please enter all values and select start/end date");
            return;
        }

        // Validate registration number and phone number
        if (regNo.length() != 4) {
            showToast("Registration number must be 4 digits");
            return;
        }

        if (phoneNumber.length() != 10) {
            showToast("Phone number must be 10 digits");
            return;
        }

        // Convert dates to desired format
        String startDate = convertDate(selectedStartDate);
        String endDate = convertDate(selectedEndDate);

        // Perform database insertion
        ContentValues values = new ContentValues();
        values.put("Cname", name.getText().toString().trim());
        values.put("reg_no", regNo);
        values.put("phone", phoneNumber);
        values.put("start_date", startDate);
        values.put("end_date", endDate);

        long result = db.insert("tailordb2", null, values);

        if (result == -1) {
            // Insertion failed
            showMessage("Error", "Failed to add record");
        } else {
            // Insertion successful
            showMessage("Success", "Record added");
            clearText();
        }
        finish();
    }

    public static String convertDate(String inputDate) {
        String outputDate = "";
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = inputFormat.parse(inputDate);
            outputDate = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return outputDate;
    }

    private void clearText() {
        name.setText("");
        Reg_no.setText("");
        phone.setText("");
        selectedStartDate = null;
        selectedEndDate = null;
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        dialog = builder.create(); // Create the AlertDialog instance
        dialog.show();
        builder.show();
    }
    protected void onDestroy() {
        super.onDestroy();
        // Dismiss the dialog if it is showing to prevent window leaks
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }

    }
}
