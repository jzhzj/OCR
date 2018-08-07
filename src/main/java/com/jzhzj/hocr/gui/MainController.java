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

public class MainController implements Initializable {
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
            promptError("Config not found!", null,
                    "Could not found the configuration. " +
                            "We'll automatically generate one for ya. " +
                            "Please complete the configuration before using the APP.");
            try {
                genConfig();
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
                    promptError("Config not found!", null,
                            "Could not found the configuration. " +
                                    "We'll automatically generate one for ya. " +
                                    "Please complete the configuration before using the APP.");
                    try {
                        genConfig();
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

    @FXML
    void handleButtonClick(MouseEvent mouseEvent) {
        Button btn = (Button) mouseEvent.getSource();
        switch (btn.getId()) {
            case "chooseFile":
                try {
                    openFile();
                } catch (FileSizeExceedsLimitationException e) {
                    promptWarning("Image too large", null,
                            "The image you choose exceeds the size limitation, which is 5 MB! " +
                                    "Please choose another image or compress the image before reload it again.");
                }
                break;
            case "recognize":
                if (picFile == null) {
                    promptInfo("Please choose image", null,
                            "Please choose image before recognition");
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
            promptWarning("Drop more than one file", null,
                    "Please drop one file each time.");
        }
        picFile = files.get(0);
        String fileName = picFile.getName().toLowerCase();
        if (!(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".bmp"))) {
            promptError("Wrong Format", null,
                    "Please load image file.");
            picFile = null;
            return;
        }
        long picLen = picFile.length();
        if (picLen > 5 * 1024 * 1024) {
            promptWarning("Image too large", null,
                    "The image you choose exceeds the size limitation, which is 5 MB! " +
                            "Please choose another image or compress the image before reload it again.");
            return;
        }
        try {
            imageView.setImage(new Image(new BufferedInputStream(new FileInputStream(picFile))));
            imageView.setEffect(new DropShadow());
        } catch (IOException e) {
            promptInfo("Something Wrong", null, "Oops. Something goes wrong :-(");
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
            promptError("Failed to show Image", null,
                    "Oops. Failed to show the image you chose :-(. If shows, ignore this :-).");
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

    private void copyToClipBoard() {
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(result);
        clipboard.setContent(clipboardContent);
        promptInfo("Success", null, "Text has already copied to clipboard!");
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
            promptError("Failed to save txt", null, "Oops. Failed to save the txt :-(.");
        }
    }
}