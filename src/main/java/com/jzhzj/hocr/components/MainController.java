package com.jzhzj.hocr.components;

import com.jzhzj.hocr.constant.MachineProps;
import com.jzhzj.hocr.exception.*;
import com.jzhzj.hocr.service.Poster;
import com.jzhzj.hocr.service.Receiver;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
//        String path = this.getClass().getResource("/dd.jpg").getPath();
        InputStream is = this.getClass().getResourceAsStream("/dd.jpg");
        imageView.setImage(new Image(new BufferedInputStream(is)));
        imageView.setEffect(new DropShadow());
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
        try {
            imageView.setImage(new Image(new BufferedInputStream(new FileInputStream(picFile))));
        } catch (IOException e) {
            promptSomethingWrongError();
        }
    }

    @FXML
    void handleMouseEntered(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        DropShadow shadow = new DropShadow();
        btn.setEffect(shadow);
    }

    @FXML
    void handleMouseExited(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        btn.setEffect(null);
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
            throw new FileSizeExceedsLimitationException("图片过大请压缩后再重新上传");

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
}
