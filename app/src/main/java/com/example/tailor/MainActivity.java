package com.example.tailor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    FloatingActionButton f;
    Button btn,deleteButton;
    SQLiteDatabase db;
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
        btn=(Button) findViewById(R.id.viewData);
        deleteButton = findViewById(R.id.deleteButton);
        btn.setOnClickListener(this);
        deleteButton.setOnClickListener(this);


    }
    public void show(View view)
    {
        startActivity(new Intent(MainActivity.this, AddBottonActivity.class));
    }

    @Override
    public void onClick(View v1) {
        if(v1==btn)
        {
            Cursor c=db.rawQuery("SELECT * FROM tailordb", null);
            if(c.getCount()==0)
            {
                showMessage("Error", "No records found");
                return;
            }
            StringBuffer buffer=new StringBuffer();
            while(c.moveToNext())
            {
                buffer.append("Id: "+c.getString(0)+"\n");
                buffer.append("Name: "+c.getString(1)+"\n");
                buffer.append("Reg_no: "+c.getString(2)+"\n");
                buffer.append("Mobile_no: "+c.getString(3)+"\n");
                buffer.append("Start_data: "+c.getString(4)+"\n");
                buffer.append("end_date: "+c.getString(5)+"\n\n");
            }
            showMessage("Customer Details", buffer.toString());
        }
        if(v1==deleteButton)
        {
            showDeleteDialog();
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
        db.execSQL("DELETE FROM tailordb WHERE reg_no = '" + regNo + "'");
        showMessage("Success", "Record with registration number " + regNo + " deleted successfully");
    }
    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

}

