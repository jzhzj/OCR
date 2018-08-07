package com.jzhzj.hocr.service;

import com.jzhzj.hocr.exception.FailToReceiveResultException;

import java.io.*;
import java.net.HttpURLConnection;

/**
 * 本类用于从Tencent Cloud获取返回结果。
 *
 * @author jzhzj
 */
public class Receiver {
    /**
     * 返回一个字符串，字符串的内容是Tencent Cloud返回的json文件。
     *
     * @param con 与Tencent Cloud的HTTP连接
     * @return 字符串 从Tencent Cloud返回的json文件
     * @throws FailToReceiveResultException 当未能从Tencent Cloud获取返回结果时
     */
    public static String receive(HttpURLConnection con) throws FailToReceiveResultException {
        BufferedReader br;
        String json;
        try {
            // 获取http连接中的输入流
            br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            // 读取流中的json文件
            json = br.readLine();
        } catch (IOException e) {
            throw new FailToReceiveResultException();
        }
        // 其实这个方法并没有真正的解析json文件
        // 由于我们只关心原图片中有那些手写的字
        // 所以这里的操作相当于从json文件中定位关键字"itemstring"的index
        // 从index往后13位，将是识别的结果
        //
        // 其实json文件里不仅有识别结果，还有相应字符串在原图中的坐标，以及每个识别结果的置信度
        // 如果以后想继续开发利用坐标或置信度的话，建议使用Google的开源json解析库--Gson
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
