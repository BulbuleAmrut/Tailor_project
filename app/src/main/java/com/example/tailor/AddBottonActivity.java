package com.example.tailor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.opengl.GLDebugHelper;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class AddBottonActivity extends AppCompatActivity implements View.OnClickListener {
    EditText name, Reg_no, phone;
    TextView t1, t2;
     SQLiteDatabase db;
    String selectedStartDate;
    String selectedEndDate;
    Button sub;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.add_botton);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = (EditText) findViewById(R.id.name);
        Reg_no = (EditText) findViewById(R.id.Reg_no);
        phone = (EditText) findViewById(R.id.phone);
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        sub=(Button)findViewById(R.id.sub);
        sub.setOnClickListener(this);

        db=openOrCreateDatabase("TailorDB", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS tailordb(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Cname VARCHAR," +
                "reg_no VARCHAR," +
                "phone VARCHAR," +
                "start_date VARCHAR," +
                "end_date VARCHAR);");
    }

    public void startDate(View view) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        // Create and show DatePickerDialog
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                    // Handle date selection
                    selectedStartDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    showToast(selectedStartDate);
                }, year, month, dayOfMonth);
        datePickerDialog.show();






    }

    public void endDate(View v2) {
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



    }
    public void showToast(String a)
    {
        Toast.makeText(AddBottonActivity.this, "Selected date:"+a, Toast.LENGTH_LONG).show();
    }


    public void clearText()
    {
        name.setText("");
        Reg_no.setText("");
        phone.setText("");

    }
    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


    @Override

    public void onClick(View v) {
        if(v == sub) {
            //String Cname = name.getText().toString().trim();
            String regNo = Reg_no.getText().toString().trim();
            String phoneNumber = phone.getText().toString().trim();
            String startDate = selectedStartDate;
            String endDate = selectedEndDate;

            // Check if any of the fields are empty
            if(name.getText().toString().trim().isEmpty() ||
                    Reg_no.getText().toString().trim().isEmpty() ||
                    phone.getText().toString().trim().isEmpty()) {
                showMessage("Error", "Please enter all values");
                return;

            }
            if (regNo.length() != 4) {
                Toast.makeText(this, "Registration number must be 4 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (phoneNumber.length() != 10) {
                Toast.makeText(this, "Phone number must be 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            if (startDate == null || startDate.isEmpty() || endDate == null || endDate.isEmpty()) {
                Toast.makeText(this, "Please select both start date and end date", Toast.LENGTH_SHORT).show();
                return;
            }



            // Perform database insertion
            ContentValues values = new ContentValues();
            values.put("Cname", name.getText().toString().trim());
            values.put("reg_no", Reg_no.getText().toString().trim());
            values.put("phone", phone.getText().toString().trim());
            values.put("start_date", selectedStartDate);
            values.put("end_date", selectedEndDate);

            long result = db.insert("tailordb", null, values);

            if(result == -1) {
                // Insertion failed
                showMessage("Error", "Failed to add record");
            } else {
                // Insertion successful
                showMessage("Success", "Record added");
                clearText();

            }
        }
        finish();
    }

}
