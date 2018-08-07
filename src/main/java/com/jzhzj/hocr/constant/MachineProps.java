package com.jzhzj.hocr.constant;

import java.util.ResourceBundle;

/**
 * 这个类用来从properties中读取软件的属性。
 * 读取到的值将以常量的形式，提供给其他需要这些属性的类。
 *
 * @author jzhzj
 */
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
