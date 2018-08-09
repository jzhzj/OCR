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
        // 读取com.jzhzj.hocr.resources.prop.properties文件
        ResourceBundle rb = ResourceBundle.getBundle("prop");
        // App properties
        APP_NAME = rb.getString("APP_NAME");
        OCR_URL = rb.getString("OCR_URL");

        // Poster properties
        BOUNDARY_PREFIX = rb.getString("BOUNDARY_PREFIX");
        BOUNDARY = rb.getString("BOUNDARY");

        // Receiver properties
        PARAGRAPH_SEPARATOR = rb.getString("PARAGRAPH_SEPARATOR");
        RESULT_ANCHOR = rb.getString("RESULT_ANCHOR");

        // config path
        CONFIG_PATH = System.getProperty("user.dir") + "/config.config";
    }

    public static final String APP_NAME;
    public static final String OCR_URL;
    public static final String CONFIG_PATH;
    public static final String BOUNDARY_PREFIX;
    public static final String BOUNDARY;
    public static final String PARAGRAPH_SEPARATOR;
    public static final String RESULT_ANCHOR;
}
