package com.jzhzj.hocr.components;

import com.jzhzj.hocr.constant.MachineProps;
import com.jzhzj.hocr.exception.*;
import com.jzhzj.hocr.service.Keys;
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
import javafx.scene.layout.BorderPane;
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

import static com.jzhzj.hocr.components.PromptAlert.*;

public class MainController extends BorderPane implements Initializable {
    private File picFile;
    private File outPath;
    private String result;


    @FXML
    private ImageView imageView;
    @FXML
    private TextArea textArea;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        InputStream is = this.getClass().getResourceAsStream("/dd.jpg");
        imageView.setImage(new Image(new BufferedInputStream(is)));
        imageView.setEffect(new InnerShadow());
        try {
            Keys.getInstance().initialize();
        } catch (IOException e) {
            promptConfigNotFoundError();
            try {
                autoGenerateConfig();
                Keys.getInstance().initialize();
            } catch (IOException ie) {
                promptFailToGenerateConfigError();
            } catch (NullKeysException ne) {
                promptNoKeysInfo();
            }
        } catch (NullKeysException e) {
            promptNoKeysInfo();
        }
    }

    @FXML
    void handleMenuItem(ActionEvent actionEvent) {
        MenuItem mi = (MenuItem) actionEvent.getSource();
        switch (mi.getId()) {
            case "menuItemSave":
                try {
                    saveTxt();
                } catch (FileAlreadyExistsException e) {
                    promptFileAlreadyExistsWarning(e.getMessage());
                }
                break;
            case "menuItemConfigure":
                try {
                    openConfig();
                } catch (IOException e) {
                    promptConfigNotFoundError();
                    try {
                        autoGenerateConfig();
                        openConfig();
                    } catch (IOException ie) {
                        promptFailToGenerateConfigError();
                    }
                }
                break;
            default:
        }
    }

    @FXML
    void handleButtonClick(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        switch (btn.getId()) {
            case "chooseFile":
                try {
                    openFile();
                } catch (FileSizeExceedsLimitationException e) {
                    promptFileExceedsLimitationWarning();
                }
                break;
            case "recognize":
                if (picFile == null) {
                    promptLoadFileFirstInfo();
                    return;
                }
                post_receive();
                break;
            case "copyText":
                copyToClipBoard();
                break;
            case "generateTxtFile":
                try {
                    saveTxt();
                } catch (FileAlreadyExistsException e) {
                    promptFileAlreadyExistsWarning(e.getMessage());
                }
                break;
            case "clearText":
                textArea.clear();
                break;
            default:
        }
    }

    @FXML
    void handleDragOver(DragEvent event) {
        event.acceptTransferModes(TransferMode.ANY);
    }

    @FXML
    void handleDrop(DragEvent event) {
        List<File> files = event.getDragboard().getFiles();
        try {
            if (files.size() > 1)
                throw new DropMoreThanOneFileException();
        } catch (DropMoreThanOneFileException e) {
            promptDropMoreThanOneFileWarning();
        }
        picFile = files.get(0);
        String fileName = picFile.getName().toLowerCase();
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp"))) {
            promptWrongFileFormatError();
            picFile = null;
            return;
        }
        long picLen = picFile.length();
        if (picLen > 5 * 1024 * 1024) {
            promptFileExceedsLimitationWarning();
            return;
        }
        try {
            imageView.setImage(new Image(new BufferedInputStream(new FileInputStream(picFile))));
            imageView.setEffect(new DropShadow());
        } catch (IOException e) {
            promptSomethingWrongError();
        }
    }

    @FXML
    void handleMouseEntered(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(new DropShadow());
    }

    @FXML
    void handleMouseExited(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(null);
    }

    @FXML
    void handleMousePressed(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(new InnerShadow());
    }

    @FXML
    void handleMouseReleased(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(new DropShadow());
    }

    private void openFile() throws FileSizeExceedsLimitationException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Picture");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("image files", "*.jpg", "*.jpeg", "*.png", "*.bmp"));
        picFile = null;
        picFile = fileChooser.showOpenDialog(new Stage());
        if (picFile == null)
            return;

        // 判断图片大小是否超出限制
        long picLen = picFile.length();
        if (picLen > 5 * 1024 * 1024)
            throw new FileSizeExceedsLimitationException();

        try {
            imageView.setImage(new Image(new BufferedInputStream(new FileInputStream(picFile))));
        } catch (IOException e) {
            promptFailToShowImageError();
        }
    }

    private void post_receive() {
        URL url;
        try {
            url = new URL(MachineProps.OCR_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            Poster.postRequest(con, picFile);
            try {
                result = Receiver.receive(con);
                textArea.setEditable(false);
                textArea.appendText(result);
                picFile = null;
            } catch (FailToReceiveResultException e) {
                promptFailToReceiveError();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (FileSizeExceedsLimitationException e) {
            promptFileExceedsLimitationWarning();
        } catch (FailToUploadPicException e) {
            promptFailToUploadError();
        } catch (IOException e) {
            promptFailToConnectToServerError();
        } catch (FailToGenAppSignException e) {
            promptFailToGenAppSignError();
        }
    }

    private void copyToClipBoard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(result);
        clipboard.setContent(clipboardContent);
        promptCopySuccessfullyInfo();
    }

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

    private void openConfig() throws IOException {
        File config = new File(MachineProps.CONFIG_PATH);
        if (!config.exists())
            throw new IOException();
        Desktop.getDesktop().open(config);
    }

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

    private void stopCopy() {
        outPath = null;
    }

    private void output(File outPath) {
        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));
            pw.print(textArea.getText());
            pw.flush();
            pw.close();
        } catch (IOException e) {
            promptFailToSaveTxtError();
        }
    }

    private void autoGenerateConfig() throws IOException {
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
