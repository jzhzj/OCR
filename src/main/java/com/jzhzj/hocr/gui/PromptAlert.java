package com.jzhzj.hocr.gui;

import javafx.scene.control.Alert;

/**
 * 这个类用于在发生异常时，在用户界面抛出各种提示。
 *
 * @author jzhzj
 */
class PromptAlert {
    /**
     * 弹出Info会话
     *
     * @param title       会话标题
     * @param headerText  头部文本
     * @param contentText 内容文本
     */
    static void promptInfo(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * 弹出Error会话
     *
     * @param title       会话标题
     * @param headerText  头部文本
     * @param contentText 内容文本
     */
    static void promptError(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    /**
     * 弹出Warning会话
     *
     * @param title       会话标题
     * @param headerText  头部文本
     * @param contentText 内容文本
     */
    static void promptWarning(String title, String headerText, String contentText) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }
}
