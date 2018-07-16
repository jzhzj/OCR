package com.jzhzj.hocr.service;

import com.jzhzj.hocr.constant.MachineProps;
import com.jzhzj.hocr.exception.FailToUploadPicException;
import com.jzhzj.hocr.exception.FileSizeExceedsLimitationException;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

public class Poster {
    // TODO 加一个exception 未能生成appSign
    public static void postRequest(HttpURLConnection con, File pic) throws FileNotFoundException, FileSizeExceedsLimitationException, FailToUploadPicException {
        final String newLine = "\r\n";
        final String boundaryPrefix = "--";
        String BOUNDARY = "========7d4a6d158c9";

        // 获取appSign
        String appSign;
        long appId = Keys.getInstance().getAppId();
        String secretId = Keys.getInstance().getSecretId();
        String secretKey = Keys.getInstance().getSecretKey();
        try {
            appSign = Sign.appSign(appId, secretId, secretKey, "tencentyun", 3600 * 24 * 30);
        } catch (Exception e) {
            throw new RuntimeException("未能生成appSign!");
        }

        // 设置http请求方式
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new RuntimeException("设置http请求有误！");
        }
        con.setRequestProperty("Authorization", appSign);
        con.setRequestProperty("Host", "recognition.image.myqcloud.com");

        // 编辑content
        StringBuilder sb = new StringBuilder();
        sb.append(boundaryPrefix);
        sb.append(BOUNDARY);
        sb.append(newLine);
        sb.append("Content-Disposition: form-data; name=\"appid\";");
        sb.append(newLine);
        sb.append(newLine);
        sb.append(appId);
        sb.append(newLine);

        sb.append(boundaryPrefix);
        sb.append(BOUNDARY);
        sb.append(newLine);
        sb.append("Content-Disposition: form-data; name=\"bucket\";");
        sb.append(newLine);
        sb.append(newLine);
        sb.append("test");
        sb.append(newLine);

        sb.append(boundaryPrefix);
        sb.append(BOUNDARY);
        sb.append(newLine);
        sb.append("Content-Disposition: form-data; name=\"image\"; filename=\"test.jpg\"");
        sb.append(newLine);
        sb.append("Content-Type: image/jpeg");
        sb.append(newLine);
        sb.append(newLine);

        String content1 = sb.toString();
        byte[] buffCon1 = content1.getBytes();
        int len1 = buffCon1.length;


        long picLen = pic.length();
        byte[] buffPic;
        // 判断图片大小是否超出限制
        if (picLen < 5 * 1024 * 1024)
            buffPic = new byte[(int) picLen];
        else
            throw new FileSizeExceedsLimitationException();
        int len2 = buffPic.length;

        BufferedInputStream bis;
        try {
            bis = new BufferedInputStream(new FileInputStream(pic));
        } catch (FileNotFoundException e) {
            throw e;
        }
        try {
            bis.read(buffPic);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        sb = new StringBuilder();
        sb.append(newLine);
        sb.append(boundaryPrefix);
        sb.append(BOUNDARY);
        sb.append(boundaryPrefix);
        byte[] buffCon2 = sb.toString().getBytes();
        int len3 = buffCon2.length;


        con.setRequestProperty("Content-Length", (len1 + len2 + len3) + "");
        con.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + BOUNDARY);

        con.setDoOutput(true);
        con.setDoInput(true);

        // 向服务器输出请求
        BufferedOutputStream bos;
        try {
            bos = new BufferedOutputStream(con.getOutputStream());
            bos.write(buffCon1);
            bos.write(buffPic);
            bos.write(buffCon2);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            throw new FailToUploadPicException();
        }

    }
}
