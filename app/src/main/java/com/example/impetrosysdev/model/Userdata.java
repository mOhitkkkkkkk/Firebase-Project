package com.example.impetrosysdev.model;

public class Userdata {
    String firstName,lastName,email,mobileNumber;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return maskEmail(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileNumber() {
        return maskMobile(mobileNumber);
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
    private String maskEmail(String email) {
        // Implement your email masking logic here (e.g., hide half of the characters)
        // This is just a simple example; you may want to implement a more sophisticated algorithm.
        int length = email.length();
        int hideLength = length / 2;
        String maskedEmail = "*****"+email.substring(hideLength) ;
        return maskedEmail;
    }

    private String maskMobile(String mobileNumber) {
        // Implement your mobile number masking logic here (e.g., hide half of the characters)
        // This is just a simple example; you may want to implement a more sophisticated algorithm.
        int length = mobileNumber.length();
        int hideLength = length / 2;
        String maskedMobile = mobileNumber.substring(0, length - hideLength) + "*****";
        return maskedMobile;
    }
    public Userdata(String firstName, String lastName, String email, String mobileNumber) {
        this.firstName=firstName;
        this.lastName=lastName;
        this.email=email;
        this.mobileNumber=mobileNumber;
    }


}

