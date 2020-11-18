package com.mikeli.mobilephonelist;

public class Contacts {
    private String name;
    private String phoneNumber;

    @Override
    public String toString() {
//        return super.toString(); // Contacts@3f2a3a5
        return name + ", " + phoneNumber;
    }

    @Override
    public boolean equals(Object o) {  // Deep comparison overrides the default shallow comparison
        // (required for list.contains() method
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contacts contacts = (Contacts) o;
        return name.equals(contacts.name);   // only check for name
    }


    public Contacts(String name, String phoneNumber) {
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

}
