package com.jzhzj.hocr.service;

import com.google.gson.Gson;
import com.jzhzj.hocr.exception.FailToReceiveResultException;
import com.jzhzj.hocr.service.resultDataStucture.Item;
import com.jzhzj.hocr.service.resultDataStucture.Result;
import com.jzhzj.hocr.service.resultDataStucture.Word;

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
        Gson gson = new Gson();
        Result res = gson.fromJson(json, Result.class);

        StringBuilder sb = new StringBuilder();
        for (Item item : res.data.items) {
            for (Word word : item.words) {
                sb.append(word.character);
            }
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append("~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
