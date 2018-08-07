package com.jzhzj.hocr.util;

import com.jzhzj.hocr.constant.MachineProps;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 这个类用于生成Config模板
 *
 * @author jzhzj
 */
public class ConfigGenerator {
    /**
     * 在当前路径下生成一个默认Config文件。
     *
     * @throws IOException 当生成config失败时
     */
    public static void genConfig() throws IOException {
        File config = new File(MachineProps.CONFIG_PATH);
        config.createNewFile();
        StringBuilder sb = new StringBuilder();
        sb.append("# 以#号开头的行将被视为注释，软件将不会读取该行。");
        sb.append(System.lineSeparator());
        sb.append("# 这个文件用于存放您腾讯云的 AppId, SecretId 以及 SecretKey。");
        sb.append(System.lineSeparator());
        sb.append("# 每月每个腾讯云账号，将免费拥有1000次手写识别调用量");
        sb.append(System.lineSeparator());
        sb.append("# 超出的用量将收费，具体收费规则请访问 https://cloud.tencent.com/document/product/866/17619");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("# 请按如下方式填写腾讯云信息：");
        sb.append(System.lineSeparator());
        sb.append("# appid=1234567890");
        sb.append(System.lineSeparator());
        sb.append("# secretid=xxxxxxxxxxxxxxxx");
        sb.append(System.lineSeparator());
        sb.append("# secretkey=xxxxxxxxxxxxxxxx");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("appid=");
        sb.append(System.lineSeparator());
        sb.append("secretid=");
        sb.append(System.lineSeparator());
        sb.append("secretkey=");
        PrintWriter pw = new PrintWriter(config);
        pw.print(sb.toString());
        pw.flush();
        pw.close();
    }
}
