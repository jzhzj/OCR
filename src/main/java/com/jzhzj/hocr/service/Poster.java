package com.jzhzj.hocr.service;

import com.jzhzj.hocr.exception.FailToGenAppSignException;
import com.jzhzj.hocr.exception.FailToUploadPicException;
import com.jzhzj.hocr.exception.FileSizeExceedsLimitationException;
import com.jzhzj.hocr.util.Keys;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;

/**
 * 本类将用于向Tencent Cloud发送Post Request。
 *
 * @author jzhzj
 */
public class Poster {
    /**
     * 接收一个HTTP连接和一个图片文件，将向目的服务器发起Post请求。
     *
     * @param con 与Tencent Cloud的HTTP连接
     * @param pic 将被上传的图片文件
     * @throws FileNotFoundException              当未找到图片文件时
     * @throws FileSizeExceedsLimitationException 当文件大小超过腾讯限制时
     * @throws FailToUploadPicException           当上传失败时
     * @throws FailToGenAppSignException          当获取Authorization（鉴权签名）时
     */
    public static void postRequest(HttpURLConnection con, File pic) throws FileNotFoundException, FileSizeExceedsLimitationException, FailToUploadPicException, FailToGenAppSignException {
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
            throw new FailToGenAppSignException();
        }

        // 设置http请求方式
        try {
            con.setRequestMethod("POST");
        } catch (ProtocolException e) {
            throw new RuntimeException("设置http请求有误！");
        }
        // 设置http请求头属性
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
        // 将图片的二进制文件读取到byte[]数组中
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


        // 将需要上传的图片读取到byte[]中后
        // 再设置请求头的Content-Length属性
        // 因为没读出图片之前，无法获悉http请求报文的长度
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
