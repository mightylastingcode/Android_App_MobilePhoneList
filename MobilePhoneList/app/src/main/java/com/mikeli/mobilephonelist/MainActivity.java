//===============================================================================================
//    The MIT License (MIT)
//    Copyright ©2020 Michael Chi Li
//
//    Permission is hereby granted, free of charge, to any person obtaining a copy of
//    this software and associated documentation files (the “Software”),to deal in the Software
//    without restriction, including without limitation the rights to use, copy, modify, merge,
//    publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons
//    to whom the Software is furnished to do so, subject to the following conditions:
//
//    The above copyright notice and this permission notice shall be included in all copies
//    or substantial portions of the Software.
//
//    THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
//    INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
//    PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE
//    FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
//    OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
//    DEALINGS IN THE SOFTWARE.
//===============================================================================================
//    Project Name: Mobile Phone Contact List App
//    Author: Michael C Li
//    Created Date: 11-17-2020
//    Version: First Beta Version
//
//    The app is to store the user's phone contact list.  The user can add, remove, and update any
//    contact record in the list.  The list can be saved.   Whenever the app is re-started by
//    the user, the saved list in the file will initially be retrieved.  There is a status text
//    box to confirm whether the user's command such as add is carried successfully.
//
//    SDK: Android 4.2 (Jelly Bean) API 17
//    Tool: Android Studio 4.0.1
//    Language: Java
//
//    Tested device: Motorola G Power (2010)  OS Android 10.0 (Android Q)
//
//===============================================================================================


package com.mikeli.mobilephonelist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    MobilePhone mobilePhone = new MobilePhone();

    private TextView statusTextView;  // display the status for each command issued.
    private TextView listTextView;  // display the whole list
    private EditText nameInput;
    private EditText phoneInput;
    private Button addButton;   // add a new contact
    private Button delButton;   // remove an existing contact
    private Button chgButton;   // update an existing contact
    private Button findButton;  // search for an existing contact
    private Button listButton;  // list the entire contact list
    private Button saveButton;  // write the contact list to the internal storage area.

    // Use Find button to get the values for the following variables.
    private String  searchName = ""; // used for updating the selected contact.
    private int     searchPosition = -1;  // used for updating the selected contact
    private String filename = "phonelist.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: In");

        statusTextView = (TextView) findViewById(R.id.statusTextView);
        listTextView = (TextView) findViewById(R.id.listTextView);
        nameInput = (EditText) findViewById(R.id.nameInput);
        phoneInput = (EditText) findViewById(R.id.phoneInput);
        addButton = (Button) findViewById(R.id.addButton);
        delButton = (Button) findViewById(R.id.delButton);
        chgButton = (Button) findViewById(R.id.chgButton);
        findButton = (Button) findViewById(R.id.findButton);
        listButton = (Button) findViewById(R.id.listButton);
        saveButton = (Button) findViewById(R.id.saveButton);

        listTextView.setMovementMethod(new ScrollingMovementMethod()); // enable the vertical scroll bar

        final Context context = this.getApplicationContext();  // final required for access within
                                                               // the inner class method.


        Log.d(TAG, "onCreate: Read the phone contact from " + filename);
        mobilePhone.readPhoneContact(filename,context);              // Read the saved list.
        listTextView.setText(mobilePhone.printPhoneContactList());   // Display the saved list.

        View.OnClickListener addOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick add button: in.");
                Log.d(TAG, "onClick add button: name " + nameInput.getText().toString());
                Log.d(TAG, "onClick add button: phone " + phoneInput.getText().toString());
                if (mobilePhone.addPhoneContact(new Contacts(nameInput.getText().toString(),phoneInput.getText().toString()))==0) {
                    statusTextView.setText("New contact is added.");
                    listTextView.setText(mobilePhone.printPhoneContactList());
                } else {
                    statusTextView.setText("New contact already exists.");
                }
                Log.d(TAG, "onClick add butqton: out.");
            }
        };

        View.OnClickListener delOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick delete button: in.");
                Log.d(TAG, "onClick delete button: name " + nameInput.getText().toString());
                if (mobilePhone.removePhoneContact(nameInput.getText().toString()) == 0) {
                    statusTextView.setText("The contact is deleted.");
                    listTextView.setText(mobilePhone.printPhoneContactList());
                } else {
                    statusTextView.setText("The contact cannot be found.");
                }
                Log.d(TAG, "onClick delete button: out.");
            }
        };

        View.OnClickListener findOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick find button: in.");
                Log.d(TAG, "onClick find button: name " + nameInput.getText().toString());
                String searchName = nameInput.getText().toString();
                searchPosition = mobilePhone.findPhoneContactPosition(nameInput.getText().toString());
                Log.d(TAG, "onClick find button: position " + searchPosition);
                if (searchPosition >= 0) {
                    statusTextView.setText("The contact is found. Change it?");
                    Contacts contact = mobilePhone.getPhoneContact(searchPosition);
                    String msg = (searchPosition + 1) + ". " + contact.getName() + ", " + contact.getPhoneNumber();
                    phoneInput.setText(contact.getPhoneNumber());
                    listTextView.setText(msg);
                } else {
                    searchPosition = -1;
                    statusTextView.setText("The contact cannot be found.");
                    listTextView.setText("");  // Nothing to be displayed.
                }
                Log.d(TAG, "onClick find button: out.");
            }
        };

        View.OnClickListener chgOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick change button: in.");
                if (searchPosition >= 0 && searchPosition < mobilePhone.getPhoneContactSize()) {
                    Contacts contact = mobilePhone.getPhoneContact(searchPosition);
                    Log.d(TAG, "onClick change button: old name " + contact.getName());
                    Log.d(TAG, "onClick change button: old phone " + contact.getPhoneNumber());
                    Log.d(TAG, "onClick change button: new name " + nameInput.getText().toString());
                    Log.d(TAG, "onClick change button: new phone " + phoneInput.getText().toString());
                    Log.d(TAG, "onClick change button: search contact position " + searchPosition);
                    Log.d(TAG, "onClick change button: contact size " + mobilePhone.getPhoneContactSize());

                    mobilePhone.updatePhoneContact(searchPosition,nameInput.getText().toString(),phoneInput.getText().toString());
                    statusTextView.setText("The contact is changed.");
                    listTextView.setText(mobilePhone.printPhoneContactList());
                } else {
                    statusTextView.setText("Error: Unable to change.");
                    listTextView.setText(mobilePhone.printPhoneContactList());
                }
                Log.d(TAG, "onClick change button: out.");
            }
        };


        View.OnClickListener listOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick list button: in.");
                listTextView.setText(mobilePhone.printPhoneContactList());
                statusTextView.setText("Display all the contacts");
                Log.d(TAG, "onClick list button: out.");
            }
        };

        View.OnClickListener saveOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick save button: in.");
                Log.d(TAG, "onCreate: Write the phone contact to " + filename);
                mobilePhone.savePhoneContact(filename,context);
                listTextView.setText(mobilePhone.printPhoneContactList());
                statusTextView.setText("Save the contacts into a file");
                Log.d(TAG, "onClick save button: out.");
            }
        };

        saveButton.setOnClickListener(saveOnClickListener);
        chgButton.setOnClickListener(chgOnClickListener);
        findButton.setOnClickListener(findOnClickListener);
        delButton.setOnClickListener(delOnClickListener);
        addButton.setOnClickListener(addOnClickListener);
        listButton.setOnClickListener(listOnClickListener);
        Log.d(TAG, "onCreate: Out");
    }

    // For Testing purpose
//        writeToFile("This is a test.",context);
//        readFromFile(context);


//    private void writeToFile(String data,Context context) {
//        try {
//            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput("config.txt", Context.MODE_PRIVATE));
//            outputStreamWriter.write(data);
//            outputStreamWriter.close();
//        }
//        catch (IOException e) {
//            Log.e("Exception", "File write failed: " + e.toString());
//        }
//    }
//
//    private String readFromFile(Context context) {
//
//        String ret = "";
//
//        try {
//            InputStream inputStream = context.openFileInput("config.txt");
//
//            if ( inputStream != null ) {
//                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                String receiveString = "";
//                StringBuilder stringBuilder = new StringBuilder();
//
//                while ( (receiveString = bufferedReader.readLine()) != null ) {
//                    stringBuilder.append("\n").append(receiveString);
//                    Log.e("login activity", "line: " + receiveString);
//                }
//
//                inputStream.close();
//                ret = stringBuilder.toString();
//            }
//        }
//        catch (FileNotFoundException e) {
//            Log.e("login activity", "File not found: " + e.toString());
//        } catch (IOException e) {
//            Log.e("login activity", "Can not read file: " + e.toString());
//        }
//
//        return ret;
//    }
}