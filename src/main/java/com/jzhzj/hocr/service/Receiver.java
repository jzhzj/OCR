package com.jzhzj.hocr.service;

import com.jzhzj.hocr.exception.FailToReceiveResultException;

import java.io.*;
import java.net.HttpURLConnection;

public class Receiver {
    public static String receive(HttpURLConnection con) throws FailToReceiveResultException {
        BufferedReader br;
        String json;
        try {
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            json = br.readLine();
        } catch (IOException e) {
            throw new FailToReceiveResultException("Failed to receive results from Tencent Cloud!");
        }
        StringBuilder sb = new StringBuilder();
        int itemStringIndex = 0;
        while (itemStringIndex < json.length()) {
            itemStringIndex = json.indexOf("itemstring", ++itemStringIndex);
            if (itemStringIndex == -1)
                break;
            int resIndex = itemStringIndex + 13;
            int endIndex = json.indexOf("\",", resIndex);
            String itemString = json.substring(resIndex, endIndex);
            sb.append(itemString);
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
