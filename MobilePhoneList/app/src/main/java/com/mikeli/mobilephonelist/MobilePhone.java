package com.mikeli.mobilephonelist;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class MobilePhone {
    private ArrayList<Contacts> contactList = new ArrayList<Contacts>();
    private static final String TAG = "MobilePhone";
    private String msg;
    private static final String filename = "phonelist.txt";

    // Use default constructor.

    public String printPhoneContactList() {
        Contacts item;
        String phoneList = "";

        for (int i=0; i< contactList.size(); i++) {
            item = contactList.get(i);  // get the object.
//            System.out.println((i+1) + " --> " + item.toString());
            msg = (i+1) + ". " + item.toString() + "\n";
            Log.d(TAG, msg);
            phoneList += msg;
        }
        return phoneList;
    }

    public int addPhoneContact(Contacts item) {
        if (contactList.contains(item) == false) {
            contactList.add(item);
            return 0;
        } else {
//            System.out.println("Error: The person's contact info is already in the list.");
            msg = "Error: The person's contact info is already in the list.";
            Log.d(TAG, msg);
            return -1;
        }
    }

    public void updatePhoneContact(int position, String name, String phone) {
        Contacts newItem = new Contacts(name,phone);
        contactList.set(position, newItem);
    }

    public int removePhoneContact(String name){
        Contacts item = new Contacts(name,"1111");  // phone # is not used in comparison.
        if (contactList.contains(item) == true) {
            contactList.remove(item);
            return 0;
        } else {
//            System.out.println("Error: The person is not in the contact list.");
            msg = "Error: The person is not in the contact list.";
            Log.d(TAG, msg);
            return -1;
        }
    }


    public int findPhoneContactPosition(String name) {
        Contacts item = new Contacts(name,"1111");  // phone # is not used in comparison.
        return contactList.indexOf(item);
    }

    public Contacts getPhoneContact(int position) {
        return contactList.get(position);
    }

    public int getPhoneContactSize() {
        return contactList.size();
    }


    public int savePhoneContact(String filename,  Context context) {
        Contacts item;

        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(filename, Context.MODE_PRIVATE));
            for (int i=0; i< contactList.size(); i++) {
                item = contactList.get(i);  // get the object.
                outputStreamWriter.write(item.toString() + "\n");
            }
            outputStreamWriter.close();
        } catch (Exception e) {
            Log.d(TAG, "Exception Error!" + e);
            e.printStackTrace();
            return 1;
        } finally {
            Log.d(TAG, "Successful file save!");
            return 0;
        }
    }



    public int readPhoneContact(String filename,Context context) {
        Contacts item;

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    String[] contactInfoArray = receiveString.split(",");
                    Log.e("login activity", "line: " + receiveString);
                    addPhoneContact(new Contacts(contactInfoArray[0],contactInfoArray[1]));
                }

                inputStream.close();
                return 0;
            }
            return -1;
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
            return -1;
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
            return -1;
        }
    }
}
