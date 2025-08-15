package top.angeya.oneterminal.util;


import javafx.scene.layout.Region;

/**
 * @Author: angeya
 * @Date: 2025/8/15 15:40
 */
public class RegionUtil {


    /**
     * 设置控件大小
     *
     * @param regions 控件
     * @param widthHeight 宽高
     */
    public static void setSize(double widthHeight, Region... regions) {
        setSize(widthHeight, widthHeight, regions);
    }

    /**
     * 设置控件大小
     *
     * @param regions 控件
     * @param width  宽度
     * @param height 高度
     */
    public static void setSize(double width, double height, Region... regions) {
        for (Region region : regions) {
            region.setMinWidth(width);
            region.setPrefWidth(width);
            region.setMaxWidth(width);
            region.setMinHeight(height);
            region.setPrefHeight(height);
            region.setMaxHeight(height);
        }
    }

}
