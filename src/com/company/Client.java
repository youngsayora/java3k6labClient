package com.company;

import java.io.*;
import java.net.Socket;

public class Client {

    public static String ipAddr = "localhost";
    public static int port = 4004;

    public static void main(String[] args) {
        new ClientActions(ipAddr, port);
    }

}
