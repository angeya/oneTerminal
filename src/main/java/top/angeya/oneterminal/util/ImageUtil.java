package top.angeya.oneterminal.util;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * @Author: angeya
 * @Date: 2025/8/15 15:21
 */
public class ImageUtil {

    public static ImageView createImageView(String path, double width, double height) {
        Image image = new Image(path);
        return createImageView(image, width, height);
    }

    /**
     * 创建ImageView的同事设置宽高
     * @param image 图片对象
     * @param widthHeight 宽高
     * @return ImageView 对象
     */
    public static ImageView createImageView(Image image, double widthHeight) {
        return createImageView(image, widthHeight, widthHeight);
    }

    /**
     * 创建ImageView的同事设置宽高
     * @param image 图片对象
     * @param width 宽度
     * @param height 高度
     * @return ImageView 对象
     */
    public static ImageView createImageView(Image image, double width, double height) {
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        return imageView;
    }

}
