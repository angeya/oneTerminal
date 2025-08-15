package top.angeya.oneterminal;

import javafx.application.HostServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.Objects;

/**
 * @Author: angeya
 * @Date: 2025/8/10 14:48
 */
public class TitleBar {

    private double xOffset = 0;

    private double yOffset = 0;

    private final HostServices hostServices;

    public TitleBar(HostServices hostServices) {
        this.hostServices = hostServices;
    }

    public HBox initTitleBar(Stage stage) {
        // 应用图标（标题栏最左）
        ImageView appIcon = new ImageView(new Image(getClass().getResourceAsStream("/icons/cmd.png")));
        appIcon.setFitHeight(16);
        appIcon.setFitWidth(16);

        // 标题
        Label titleLabel = new Label("TerminalX");
        titleLabel.setStyle("-fx-text-fill: gray; -fx-font-size: 14;");

        HBox leftBox = new HBox(8, appIcon, titleLabel);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // 创建按钮
        ImageView githubImageView = new ImageView(ImageResources.GITHUB_ICON);
        githubImageView.setFitWidth(16);
        githubImageView.setFitHeight(16);

        Button githubBtn = new Button("", githubImageView);
        githubBtn.setStyle("-fx-background-insets: 0; -fx-background-color: transparent; -fx-border-color: transparent; -fx-border-width: 0; -fx-padding: 5;");
        // 鼠标进入时设置抓手样式
        githubBtn.setOnMouseEntered(event -> {
            // 悬停时显示抓手
            githubBtn.setCursor(Cursor.HAND);
        });
        // 鼠标离开时恢复默认样式
        githubBtn.setOnMouseExited(event -> {
            githubBtn.setCursor(Cursor.DEFAULT); // 离开时恢复默认箭头
        });
        Button minBtn = createIconButton(ImageResources.MIN_ICON, "#00CD00");
        Button maxBtn = createIconButton(ImageResources.MAX_ICON, "#FFA500");
        Button closeBtn = createIconButton(ImageResources.CLOSE_ICON, "#FF4500");

        // 按钮事件
        githubBtn.setOnAction(e -> hostServices.showDocument(Constant.GITHUB_ADDRESS));
        minBtn.setOnAction(e -> stage.setIconified(true));
        maxBtn.setOnAction(e -> {
            stage.setMaximized(!stage.isMaximized());
            // 切换最大化 / 还原按钮图标
            ((ImageView) maxBtn.getGraphic()).setImage(stage.isMaximized() ? ImageResources.RESTORE_ICON : ImageResources.MAX_ICON);
        });
        closeBtn.setOnAction(e -> stage.close());

        HBox rightBox = new HBox(githubBtn, minBtn, maxBtn, closeBtn);
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
                ((ImageView) maxBtn.getGraphic()).setImage(stage.isMaximized() ? ImageResources.RESTORE_ICON : ImageResources.MAX_ICON);
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

    private static final int RESIZE_MARGIN = 5; // 检测边缘范围

    public void makeStageResizable(Stage stage, Node root) {
        root.setOnMouseMoved(event -> {
            double mouseX = event.getX();
            double mouseY = event.getY();
            double sceneWidth = root.getScene().getWidth();
            double sceneHeight = root.getScene().getHeight();

            boolean resizeH = mouseX > sceneWidth - RESIZE_MARGIN;
            boolean resizeV = mouseY > sceneHeight - RESIZE_MARGIN;
            boolean resizeHLeft = mouseX < RESIZE_MARGIN;
            boolean resizeVTop = mouseY < RESIZE_MARGIN;

            if (resizeH && resizeV) {
                root.setCursor(Cursor.SE_RESIZE);
            } else if (resizeHLeft && resizeV) {
                root.setCursor(Cursor.SW_RESIZE);
            } else if (resizeH && resizeVTop) {
                root.setCursor(Cursor.NE_RESIZE);
            } else if (resizeHLeft && resizeVTop) {
                root.setCursor(Cursor.NW_RESIZE);
            } else if (resizeH || resizeHLeft) {
                root.setCursor(Cursor.H_RESIZE);
            } else if (resizeV || resizeVTop) {
                root.setCursor(Cursor.V_RESIZE);
            } else {
                root.setCursor(Cursor.DEFAULT);
            }
        });

        root.setOnMousePressed(event -> {
            root.setUserData(new double[]{
                    event.getSceneX(),
                    event.getSceneY(),
                    stage.getWidth(),
                    stage.getHeight(),
                    stage.getX(),
                    stage.getY()
            });
        });

        root.setOnMouseDragged(event -> {
            double[] data = (double[]) root.getUserData();
            double offsetX = event.getSceneX() - data[0];
            double offsetY = event.getSceneY() - data[1];

            double mouseX = event.getX();
            double mouseY = event.getY();
            double sceneWidth = root.getScene().getWidth();
            double sceneHeight = root.getScene().getHeight();

            // 右侧调整宽度
            if (mouseX > sceneWidth - RESIZE_MARGIN) {
                stage.setWidth(data[2] + offsetX);
            }
            // 下方调整高度
            if (mouseY > sceneHeight - RESIZE_MARGIN) {
                stage.setHeight(data[3] + offsetY);
            }
            // 左侧调整宽度 + 位置
            if (mouseX < RESIZE_MARGIN) {
                double newWidth = data[2] - offsetX;
                if (newWidth > stage.getMinWidth()) {
                    stage.setWidth(newWidth);
                    stage.setX(data[4] + offsetX);
                }
            }
            // 上方调整高度 + 位置
            if (mouseY < RESIZE_MARGIN) {
                double newHeight = data[3] - offsetY;
                if (newHeight > stage.getMinHeight()) {
                    stage.setHeight(newHeight);
                    stage.setY(data[5] + offsetY);
                }
            }
        });
    }

}
