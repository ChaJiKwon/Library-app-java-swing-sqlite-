package code;

import common.PatronType;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Patron  implements Comparable<Patron>{
    private static int count = 1;
    private String patronID;
    private String name;
    private Date dob;
    private String email;
    private String phoneNumber;
    private PatronType patronType;

    public Patron(String name, Date dob, String email, String phoneNumber, PatronType patronType) {
        this.patronID = generatePatronID();
        this.name = name;
        this.dob = dob;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.patronType = patronType;
    }

    public String getPatronID() {
        return patronID;
    }

    public PatronType getPatronType() {
        return patronType;
    }

    public void setPatronType(PatronType patronType) {
        this.patronType = patronType;
    }

    public static int getCount() {
        return count;
    }

    public static void setCount(int count) {
        Patron.count = count;
    }

    public void setPatronID(String patronID) {
        this.patronID = patronID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDob() {
        return dob;
    }

    public void setDob(Date dob) {
        this.dob = dob;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String generatePatronID()
    {
        String paddedID = String.format("%03d", count);
        count++;
        return paddedID;
    }
    @Override
    public int compareTo(Patron p) {
        int thisPatronIDValue = Integer.parseInt(this.getPatronID().substring(1));
        int otherPatronIDValue = Integer.parseInt(p.getPatronID().substring(1));

        if (thisPatronIDValue < otherPatronIDValue) {
            return -1;
        } else if (thisPatronIDValue > otherPatronIDValue) {
            return 1;
        } else {
            return 0;
        }
    }

    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return "{Patron ID: "+ patronID+" ,Name:"+ name +" ,Dob:" + dateFormat.format(dob.toString())+ " ,Email:"+ email + " ,phone: "+ phoneNumber + " , "+patronType.toString()+ "}";
    }

}
