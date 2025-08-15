package top.angeya.oneterminal;

import javafx.scene.image.Image;

import java.util.Objects;

/**
 * @Author: angeya
 * @Date: 2025/8/15 14:51
 */
public class ImageResources {

    public static final Image GITHUB_ICON = createImage("/icons/github.png");

    public static final Image MIN_ICON = createImage("/icons/minimize.png");

    public static final Image MAX_ICON = createImage("/icons/maximize.png");

    public static final Image RESTORE_ICON = createImage("/icons/restore.png");

    public static final Image CLOSE_ICON = createImage("/icons/close.png");

    public static final Image NEW_COMMAND_ICON = createImage("/icons/newCommand.png");

    public static final Image NEW_TERMINAL_ICON = createImage("/icons/newTerminal.png");

    public static final Image THEME_ICON = createImage("/icons/theme.png");

    /**
     * 创建图片
     * @param path 图片路径
     * @return 图片对象
     */
    private static Image createImage(String path) {
        return new Image(Objects.requireNonNull(ImageResources.class.getResourceAsStream(path)));
    }


}
