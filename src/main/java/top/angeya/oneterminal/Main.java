package top.angeya.oneterminal;

import atlantafx.base.theme.Dracula;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * @Author: angeya
 * @Date: 2025/8/10 17:14
 */
public class Main extends Application {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(Main.class);

    @Override
    public void start(Stage primaryStage) {
        TerminalManager terminalManager = new TerminalManager();
        TabPane rightTabPane = terminalManager.initView();
        CommandManager commandManager = new CommandManager(primaryStage, terminalManager);
        VBox leftBox = commandManager.initView();
        BorderPane container = new BorderPane();

        // 创建自定义标题栏并放到顶部
        HBox titleBar = new TitleBar().initView(primaryStage);
        container.setTop(titleBar);
        container.setLeft(leftBox);
        container.setCenter(rightTabPane);

        // 初始化页面并设置主题样式
        primaryStage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(container, 1000, 640);
        scene.getStylesheets().add(new Dracula().getUserAgentStylesheet());
        primaryStage.setScene(scene);
        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icons/cmd.png"))));
        primaryStage.show();

        // 初始化刷新命令列表
        commandManager.refreshCommandTree();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
