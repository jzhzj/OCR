package com.jzhzj.hocr.service;

import com.jzhzj.hocr.constant.MachineProps;
import com.jzhzj.hocr.exception.NullKeysException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Keys {
    private static Keys ourInstance = new Keys();

    public static Keys getInstance() {
        return ourInstance;
    }

    private Keys() {
    }

    private long appId;
    private String secretId;
    private String secretKey;

    public void initialize() throws IOException, NullKeysException {
        BufferedReader br = new BufferedReader(new FileReader(MachineProps.CONFIG_PATH));
        String line;
        while ((line = br.readLine()) != null) {
            line = line.replace(" ", "");
            if (line.startsWith("#"))
                continue;
            if (line.equals(""))
                continue;
            if (line.endsWith("="))
                throw new NullKeysException();
            int index;
            if ((index = line.indexOf("appid")) != -1) {
                appId = Long.parseLong(line.substring(index + 6));
            }
            if ((index = line.indexOf("secretid")) != -1) {
                secretId = line.substring(index + 9);
            }
            if ((index = line.indexOf("secretkey")) != -1) {
                secretKey = line.substring(index + 10);
            }
        }
    }

    public long getAppId() {
        return appId;
    }

    public String getSecretId() {
        return secretId;
    }

    public String getSecretKey() {
        return secretKey;
    }
}
