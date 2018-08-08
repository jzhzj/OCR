package com.jzhzj.hocr;

import com.jzhzj.hocr.constant.MachineProps;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 这个类是程序入口。
 * 基本是套路代码，不需要改动。
 * @author jzhzj
 */
public class App extends Application {

    /**
     * 继承自Application接口。
     * 主方法调用后，JVM将自动调用此方法。
     * @param primaryStage
     * @throws Exception
     * */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/jzhzj/hocr/gui/main.fxml"));
        primaryStage.setTitle(MachineProps.APP_NAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    /**
     * 这是主函数。
     * @param args 命令行接受的参数
     * */
    public static void main(String[] args) {
        launch(args);
    }
}
