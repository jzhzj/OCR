package com.jzhzj.hocr.constant;

import java.util.ResourceBundle;

public class MachineProps {
    static {
        ResourceBundle rb = ResourceBundle.getBundle("prop");
        APP_NAME = rb.getString("APP_NAME");
        OCR_URL = rb.getString("OCR_URL");
        CONFIG_PATH = System.getProperty("user.dir") + "/config.config";
    }

    public static final String APP_NAME;
    public static final String OCR_URL;
    public static final String CONFIG_PATH;
}
