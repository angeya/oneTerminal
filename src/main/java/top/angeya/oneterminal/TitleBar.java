package top.angeya.oneterminal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

/**
 * @Author: angeya
 * @Date: 2025/8/10 14:48
 */
public class TitleBar {

    private double xOffset = 0;

    private double yOffset = 0;

    public HBox initView(Stage stage) {
        return createTitleBar(stage);
    }


    private HBox createTitleBar(Stage stage) {
        // 应用图标（标题栏最左）
        ImageView appIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/cmd.png")));
        appIcon.setFitHeight(16);
        appIcon.setFitWidth(16);

        // 标题
        Label titleLabel = new Label("TerminalX");
        titleLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14;");

        HBox leftBox = new HBox(8, appIcon, titleLabel);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // 按钮图标占位符（替换成你的图片路径）
        Image minIcon = new Image(getClass().getResourceAsStream("/icons/minimize.png"));
        Image maxIcon = new Image(getClass().getResourceAsStream("/icons/maximize.png"));
        Image restoreIcon = new Image(getClass().getResourceAsStream("/icons/restore.png"));
        Image closeIcon = new Image(getClass().getResourceAsStream("/icons/close.png"));

        // 创建按钮
        Button minBtn = createIconButton(minIcon, "#00CD00");
        Button maxBtn = createIconButton(maxIcon, "#FFA500");
        Button closeBtn = createIconButton(closeIcon, "#FF4500");

        // 按钮事件
        minBtn.setOnAction(e -> stage.setIconified(true));
        maxBtn.setOnAction(e -> {
            stage.setMaximized(!stage.isMaximized());
            // 切换最大化 / 还原按钮图标
            ((ImageView) maxBtn.getGraphic()).setImage(stage.isMaximized() ? restoreIcon : maxIcon);
        });
        closeBtn.setOnAction(e -> stage.close());

        HBox rightBox = new HBox(minBtn, maxBtn, closeBtn);
        rightBox.setAlignment(Pos.CENTER_RIGHT);
        rightBox.setSpacing(5);

        Region region = new Region();
        HBox.setHgrow(region, Priority.ALWAYS);

        HBox wrapper = new HBox(leftBox, region, rightBox);
        wrapper.setPadding(new Insets(4, 8, 4, 8));
        wrapper.setStyle("-fx-background-color: #2B2B2B;");

        // 拖拽移动
        wrapper.setOnMousePressed((MouseEvent event) -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });
        wrapper.setOnMouseDragged((MouseEvent event) -> {
            if (!stage.isMaximized()) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }
        });

        // 双击最大化/还原
        wrapper.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                stage.setMaximized(!stage.isMaximized());
                ((ImageView) maxBtn.getGraphic()).setImage(stage.isMaximized() ? restoreIcon : maxIcon);
            }
        });
        return wrapper;
    }


    private Button createIconButton(Image icon, String bgColor) {
        ImageView imageView = new ImageView(icon);
        imageView.setFitWidth(10);
        imageView.setFitHeight(10);

        Button btn = new Button("", imageView);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 0;");
        btn.setOnMouseEntered(e -> btn.setStyle("-fx-background-color: #555; -fx-padding: 0;"));
        btn.setOnMouseExited(e -> btn.setStyle("-fx-background-color: " + bgColor + "; -fx-padding: 0;"));
        return btn;
    }
}
