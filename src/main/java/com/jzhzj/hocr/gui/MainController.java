package com.jzhzj.hocr.gui;

import com.jzhzj.hocr.constant.MachineProps;
import com.jzhzj.hocr.exception.*;
import com.jzhzj.hocr.util.Keys;
import com.jzhzj.hocr.service.Poster;
import com.jzhzj.hocr.service.Receiver;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import static com.jzhzj.hocr.gui.PromptAlert.*;
import static com.jzhzj.hocr.util.ConfigGenerator.*;

/**
 * Controller类，将视图层与业务层解耦。
 * 本类通过捕捉用户对GUI的操作，调用业务层。
 * 业务层不需要因视图层的变化而变化。
 *
 * @author jzhzj
 */
public class MainController implements Initializable {
    private File picFile;
    private File outPath;
    private String result;


    @FXML
    private ImageView imageView;
    @FXML
    private TextArea textArea;

    /**
     * 由于本类实现了Initializable接口，所以在加载本类的时候，JVM将自动调用initialize()方法。
     *
     * @param location
     * @param resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // 获取drag and drop引导图图片的输入流
        InputStream is = this.getClass().getResourceAsStream("/dd.jpg");
        // 在gui上显示图片
        imageView.setImage(new Image(new BufferedInputStream(is)));
        // 设置该图片的阴影效果
        imageView.setEffect(new InnerShadow());
        // 将TextArea设置为不可编辑
        textArea.setEditable(false);
        try {
            // 初始化Keys类
            Keys.getInstance().initialize();
        } catch (IOException e) {
            // 如果在当前目录中未能找到config文件的话，弹出Error
            promptError("Config not found!", null,
                    "Could not found the configuration. " +
                            "We'll automatically generate one for ya. " +
                            "Please complete the configuration before using the APP.");
            try {
                // 自动生成config文件模板
                genConfig();
                // 初始化Keys类
                Keys.getInstance().initialize();
            } catch (IOException ie) {
                promptError("Fail to Generate Config File", null,
                        "Oops! Something goes wrong! " +
                                "Failed to generate config file automatically. Please build one manually.");
            } catch (NullKeysException ne) {
                promptInfo("Keys Not Found", null,
                        "Please fill out the configuration file before using the App.");
            }
        } catch (NullKeysException e) {
            promptInfo("Keys Not Found", null,
                    "Please fill out the configuration file before using the App.");
        }
    }

    /**
     * 处理鼠标对menu的点击事件。
     *
     * @param actionEvent
     */
    @FXML
    void handleMenuItem(ActionEvent actionEvent) {
        MenuItem mi = (MenuItem) actionEvent.getSource();
        switch (mi.getId()) {
            // 如果鼠标点击的是"save"
            case "menuItemSave":
                try {
                    saveTxt();
                } catch (FileAlreadyExistsException e) {
                    promptFileAlreadyExistsWarning(e.getMessage());
                }
                break;
            // 如果点击的是"Configure"
            case "menuItemConfigure":
                try {
                    // 打开config文件
                    openConfig();
                } catch (IOException e) {
                    // 如果config文件不存在，染出Error
                    promptError("Config not found!", null,
                            "Could not found the configuration. " +
                                    "We'll automatically generate one for ya. " +
                                    "Please complete the configuration before using the APP.");
                    try {
                        // 自动生成配置文件
                        genConfig();
                        // 打开配置文件
                        openConfig();
                    } catch (IOException ie) {
                        promptError("Fail to Generate Config File", null,
                                "Oops! Something goes wrong! " +
                                        "Failed to generate config file automatically. Please build one manually.");
                    }
                }
                break;
            default:
        }
    }

    /**
     * 处理鼠标对按钮的点击事件。
     *
     * @param mouseEvent
     */
    @FXML
    void handleButtonClick(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        switch (btn.getId()) {
            // 如果鼠标点击的是"choose File"
            case "chooseFile":
                try {
                    openFile();
                } catch (FileSizeExceedsLimitationException e) {
                    promptWarning("Image too large", null,
                            "The image you choose exceeds the size limitation, which is 5 MB! " +
                                    "Please choose another image or compress the image before reload it again.");
                }
                break;
            // 如果鼠标点击的是"recognize"
            case "recognize":
                // 如果用户并未选择文件，弹出提醒
                if (picFile == null) {
                    promptInfo("Please choose image", null,
                            "Please choose image before recognition");
                    return;
                }
                // 向Tencent Cloud发送请求并接收结果
                post_receive();
                break;
            // 如果用户点击的是"copy text"
            case "copyText":
                // 将TextField中的字符串复制到系统剪贴板
                copyToClipBoard();
                break;
            // 如果用户点击的是"generate text file"
            case "generateTxtFile":
                try {
                    // 将TextField中的字符串以.txt文件保存
                    saveTxt();
                } catch (FileAlreadyExistsException e) {
                    promptFileAlreadyExistsWarning(e.getMessage());
                }
                break;
            // 如果用户点击的是"clear"
            case "clearText":
                // 将TextField中的字符串清空
                textArea.clear();
                break;
            default:
        }
    }

    /**
     * 处理拖拽文件事件。
     *
     * @param event
     */
    @FXML
    void handleDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
    }

    /**
     * 处理拖拽后释放文件的事件。
     *
     * @param event
     */
    @FXML
    void handleDrop(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        try {
            // 每次只能拖入1个图片文件，如果文件数量大于1，将抛出Warning
            if (files.size() > 1)
                throw new DropMoreThanOneFileException();
        } catch (DropMoreThanOneFileException e) {
            promptWarning("Drop more than one file", null,
                    "Please drop one file each time.");
        }
        File inputFile = files.get(0);
        // 获取文件名
        String fileName = inputFile.getName().toLowerCase();
        // 判断文件是否是图片文件。判断方式是查看文件后缀名
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp"))) {
            promptError("Wrong Format", null,
                    "Please load image file.");
            return;
        }
        // 获取文件大小
        long picLen = inputFile.length();
        // 如果文件超过5MB，将抛出提醒。
        // （腾讯云规定每次请求的报文不能超过6MB，包括请求头、请求体、图片二进制文件，这里为了方便，规定图片不能超过5MB）
        if (picLen > 5 * 1024 * 1024) {
            promptWarning("Image too large", null,
                    "The image you choose exceeds the size limitation, which is 5 MB! " +
                            "Please choose another image or compress the image before reload it again.");
            return;
        }
        // 将picFile指向新输入的图片
        picFile = inputFile;
        try {
            // 在UI上显示新的图片
            imageView.setImage(new Image(new BufferedInputStream(new FileInputStream(picFile))));
            imageView.setEffect(new DropShadow());
        } catch (IOException e) {
            promptInfo("Something Wrong", null, "Oops. Something goes wrong :-(");
        }
    }

    /**
     * 处理鼠标进入按钮上方时按钮的效果。
     *
     * @param mouseEvent
     */
    @FXML
    void handleMouseEntered(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(new DropShadow());
    }

    /**
     * 处理鼠标离开按钮时的效果。
     *
     * @param mouseEvent
     */
    @FXML
    void handleMouseExited(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(null);
    }

    /**
     * 处理鼠标点击按钮时按钮的效果。
     *
     * @param mouseEvent
     */
    @FXML
    void handleMousePressed(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(new InnerShadow());
    }

    /**
     * 处理鼠标点击按钮并释放后按钮的效果。
     *
     * @param mouseEvent
     */
    @FXML
    void handleMouseReleased(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(new DropShadow());
    }

    /**
     * 打开被选中文件。
     */
    private void openFile() throws FileSizeExceedsLimitationException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("image files", "*.jpg", "*.jpeg", "*.png", "*.bmp"));
        File inputFile = fileChooser.showOpenDialog(new Stage());
        if (inputFile == null)
            return;

        // 如果文件超过5MB，将抛出提醒。
        // （腾讯云规定每次请求的报文不能超过6MB，包括请求头、请求体、图片二进制文件，这里为了方便，规定图片不能超过5MB）
        long picLen = inputFile.length();
        if (picLen > 5 * 1024 * 1024)
            throw new FileSizeExceedsLimitationException();
        picFile = inputFile;
        try {
            imageView.setImage(new Image(new BufferedInputStream(new FileInputStream(picFile))));
        } catch (IOException e) {
            promptError("Failed to show Image", null,
                    "Oops. Failed to show the image you chose :-(. If shows, ignore this :-).");
        }
    }

    /**
     * 处理发送和接收。
     */
    private void post_receive() {
        URL url;
        try {
            // 从配置中获取Tencent Cloud的URL
            url = new URL(MachineProps.OCR_URL);
            // 获取与该URL的HTTP连接
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            // 发送POST请求
            Poster.postRequest(con, picFile);
            try {
                // 获取返回结果
                result = Receiver.receive(con);
                // 将获取的结果append到textArea中
                textArea.appendText(result);
                picFile = null;
            } catch (FailToReceiveResultException e) {
                promptError("Failed to receive results", null,
                        "Oops. Failed to receive results from the cloud :-(. " +
                                "Might be something wrong with the cloud.");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileSizeExceedsLimitationException e) {
            promptWarning("Image too large", null,
                    "The image you choose exceeds the size limitation, which is 5 MB! " +
                            "Please choose another image or compress the image before reload it again.");
        } catch (FailToUploadPicException e) {
            promptError("Failed to Upload image", null,
                    "Oops. Failed to upload the image you chose :-(. " +
                            "Might be something wrong with the network.");
        } catch (IOException e) {
            promptError("Failed to Connect to Server", null,
                    "Oops. Failed to connect to server :-(. " +
                            "Please check the network.");
        } catch (FailToGenAppSignException e) {
            promptError("Fail to Generate AppSign", null,
                    "This error occurs, might because you didn't fill out the configuration file correctly.");
        }
    }

    /**
     * 复制TextField中的文本到系统剪贴板。
     */
    private void copyToClipBoard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(result);
        clipboard.setContent(clipboardContent);
        promptInfo("Success", null, "Text has already copied to clipboard!");
    }

    /**
     * 将TextField中的文本生成.txt文件。
     */
    private void saveTxt() throws FileAlreadyExistsException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save");
        File outDir = fileChooser.showSaveDialog(new Stage());
        if (outDir == null)
            return;
        outPath = new File(outDir.getPath() + ".txt");
        if (outPath.exists())
            throw new FileAlreadyExistsException(outPath.getName());
        output(outPath);
    }

    /**
     * 打开config文件
     */
    private void openConfig() throws IOException {
        File config = new File(MachineProps.CONFIG_PATH);
        if (!config.exists())
            throw new IOException();
        Desktop.getDesktop().open(config);
    }

    /**
     * 弹出"文件已存在"提醒
     *
     * @param name 已存在的文件名
     */
    private void promptFileAlreadyExistsWarning(String name) {
        String contentText = "An item named \"" + name + "\" already exists in this location. Do you want to replace it with the one you are saving?";
        ButtonType[] buttonTypes = new ButtonType[2];
        buttonTypes[0] = new ButtonType("Stop");
        buttonTypes[1] = new ButtonType("Replace");
        Alert alert = new Alert(Alert.AlertType.WARNING, contentText, buttonTypes);
        alert.setTitle("File already exists");
        alert.setHeaderText(null);
        Optional<ButtonType> result = alert.showAndWait();
        if (!result.isPresent())
            stopCopy();
        else {
            switch (result.get().getText()) {
                case "Stop":
                    stopCopy();
                    break;
                case "Replace":
                    output(outPath);
                    break;
                default:
            }
        }
    }

    /**
     * 停止复制
     */
    private void stopCopy() {
        outPath = null;
    }

    /**
     * 输出.txt文件。
     *
     * @param outPath 输出路径
     */
    private void output(File outPath) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
            pw.print(textArea.getText());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            promptError("Failed to save txt", null, "Oops. Failed to save the txt :-(.");
        }
    }
}
