package com.example.librarymanagement;

import java.time.LocalDate;

public class rent {
    String studentname,bookname,phonenumber;
    java.sql.Date returndate;
    int status;

    public rent(String studentname, String bookname, String phonenumber, java.sql.Date returndate, int status) {
        this.studentname = studentname;
        this.bookname = bookname;
        this.phonenumber = phonenumber;
        this.returndate = returndate;
        this.status = status;
    }

    public String getStudentname() {
        return studentname;
    }

    public void setStudentname(String studentname) {
        this.studentname = studentname;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public java.sql.Date getReturndate() {
        return returndate;
    }

    public void setReturndate(java.sql.Date returndate) {
        this.returndate = returndate;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
