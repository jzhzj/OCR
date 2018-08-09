package com.jzhzj.hocr.util;

import com.jzhzj.hocr.constant.MachineProps;
import com.jzhzj.hocr.exception.NullKeysException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 这个类用来获取配置文件中的appID，secretID和secretKey。
 *
 * @author jzhzj
 */
public class Keys {
    private static Keys ourInstance = new Keys();

    /**
     * Key类采用了单例模式，本方法用于获取Keys类对象。
     *
     * @return 本类对象
     */
    public static Keys getInstance() {
        return ourInstance;
    }

    private Keys() {
    }

    private long appId;
    private String secretId;
    private String secretKey;

    /**
     * 这个方法用于初始化各项key值。
     * 这个方法将从当前路径中读取配置文件，并初始化各项key值。
     * 获取到的值，可用相应的getter获取。
     *
     * @throws IOException       如果读取配置文件失败
     * @throws NullKeysException 如果用户未配置config文件
     */
    public void initialize() throws IOException, NullKeysException {
        BufferedReader br = new BufferedReader(new FileReader(MachineProps.CONFIG_PATH));
        String line;
        while ((line = br.readLine()) != null) {
            // 以"#"开头的行将被视为注释
            if (line.startsWith("#"))
                continue;
            // 为防止用户写配置文件时加入了空格，顾在此将空格删掉
            line = line.replace(" ", "");
            if (line.equals(""))
                continue;
            // 判断用户是否已经将各种key信息填写到配置文件中
            // 判断的逻辑为：判断此行是否以"="结尾
            // 以"="结尾的，将被视为未对配置文件进行填写
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

    /**
     * 用于获取appId
     *
     * @return appId
     */
    public long getAppId() {
        return appId;
    }

    /**
     * 用于获取secretId
     *
     * @return secretId
     */
    public String getSecretId() {
        return secretId;
    }

    /**
     * 用于获取secretKey
     *
     * @return secretKey
     */
    public String getSecretKey() {
        return secretKey;
    }
}
