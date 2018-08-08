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

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/jzhzj/hocr/gui/main.fxml"));
        primaryStage.setTitle(MachineProps.APP_NAME);
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
