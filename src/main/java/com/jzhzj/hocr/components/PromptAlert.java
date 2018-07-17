package com.jzhzj.hocr.components;

import javafx.scene.control.Alert;


class PromptAlert {
    static void promptCopySuccessfullyInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText("Text has already copied to clipboard!");
        alert.showAndWait();
    }

    static void promptLoadFileFirstInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Please choose image");
        alert.setHeaderText(null);
        alert.setContentText("Please choose image before recognition");
        alert.showAndWait();
    }

    static void promptSomethingWrongError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Something Wrong");
        alert.setHeaderText(null);
        alert.setContentText("Oops. Something goes wrong :-(");
        alert.showAndWait();
    }

    static void promptFailToConnectToServerError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to Connect to Server");
        alert.setHeaderText(null);
        alert.setContentText("Oops. Failed to connect to server :-(. Please check the network.");
        alert.showAndWait();
    }

    static void promptFailToUploadError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to Upload image");
        alert.setHeaderText(null);
        alert.setContentText("Oops. Failed to upload the image you chose :-(. Might be something wrong with the network.");
        alert.showAndWait();
    }

    static void promptFailToReceiveError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to receive results");
        alert.setHeaderText(null);
        alert.setContentText("Oops. Failed to receive results from the cloud :-(. Might be something wrong with the cloud.");
        alert.showAndWait();
    }

    static void promptFailToShowImageError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to show Image");
        alert.setHeaderText(null);
        alert.setContentText("Oops. Failed to show the image you chose :-(. If shows, ignore this :-).");
        alert.showAndWait();
    }

    static void promptFailToSaveTxtError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Failed to save txt");
        alert.setHeaderText(null);
        alert.setContentText("Oops. Failed to save the txt :-(.");
        alert.showAndWait();
    }

    static void promptWrongFileFormatError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Wrong Format");
        alert.setHeaderText(null);
        alert.setContentText("Please load image file.");
        alert.showAndWait();
    }

    static void promptFileExceedsLimitationWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Image too large");
        alert.setHeaderText(null);
        alert.setContentText("The image you choose exceeds the size limitation, which is 5 MB! Please choose another image or compress the image before reload it again.");
        alert.showAndWait();
    }

    static void promptDropMoreThanOneFileWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Drop more than one file");
        alert.setHeaderText(null);
        alert.setContentText("Please drop one file each time.");
        alert.showAndWait();
    }

    static void promptConfigNotFoundError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Config not found!");
        alert.setHeaderText(null);
        alert.setContentText("Could not found the configuration. We'll automatically generate one for ya. Please complete the configuration before using the APP.");
        alert.showAndWait();
    }

    static void promptFailToGenerateConfigError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fail to Generate Config File");
        alert.setHeaderText(null);
        alert.setContentText("Oops! Something goes wrong! Failed to generate config file automatically. Please build one manually.");
        alert.showAndWait();
    }

    static void promptNoKeysInfo() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Keys Not Found");
        alert.setHeaderText(null);
        alert.setContentText("Please fill out the configuration file before using the App.");
        alert.showAndWait();
    }

    static void promptFailToGenAppSignError() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Fail to Generate AppSign");
        alert.setHeaderText(null);
        alert.setContentText("This error occurs, might because you didn't fill out the configuration file correctly.");
        alert.showAndWait();
    }
}
