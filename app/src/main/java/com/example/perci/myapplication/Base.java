package com.example.perci.myapplication;

public class Base {
    public int server_status = 1;

    Base() {
        if (MainActivity.server_status ==0 ) {
            this.server_status = 0;
        }
    }
}
