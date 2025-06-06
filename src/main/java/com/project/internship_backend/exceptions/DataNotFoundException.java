package com.project.internship_backend.exceptions;

// Mục đích tạo ra casi này để quản lý các loại Exception để phân loại
public class DataNotFoundException extends Exception{
    public DataNotFoundException(String message){
        super(message);
    }
}
