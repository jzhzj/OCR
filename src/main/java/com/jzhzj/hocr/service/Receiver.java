package com.jzhzj.hocr.service;

import com.jzhzj.hocr.constant.MachineProps;
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
        // 返回的json中有多个结果字符串
        // anchor将被用来记录 当前关键字 在json中的index
        int anchor = 0;
        while (anchor < json.length()) {
            // 如果指针到了字符串的最后一位，跳出循环，防止越界
            if (anchor == json.length() - 1)
                break;
            // 查找下一个关键字的index
            anchor = json.indexOf(MachineProps.RESULT_ANCHOR, anchor);
            // 如果未能找到下一个关键字，跳出循环
            if (anchor == -1)
                break;
            // 结果的起始index
            int resIndex = anchor + 13;
            // 结果的结尾index
            int endIndex = json.indexOf("\",", resIndex);
            // anchor增1，为了下一个循环中查找下一个关键字的index
            anchor++;
            // 获取结果子串
            String itemString = json.substring(resIndex, endIndex);
            // 将结果append到StringBuilder中
            sb.append(itemString);
            // 换行
            sb.append(System.lineSeparator());
        }
        sb.append(System.lineSeparator());
        sb.append(MachineProps.PARAGRAPH_SEPARATOR);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
