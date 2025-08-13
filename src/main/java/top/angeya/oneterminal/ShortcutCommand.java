package top.angeya.oneterminal;

import java.util.Collection;
import java.util.TreeSet;

/**
 * 快捷命令实体
 * @Author: angeya
 * @Date: 2025/8/10 16:57
 */
public class ShortcutCommand {

    /**
     * 命令名称
     */
    private String name;

    /**
     * 命令内容
     */
    private String command;

    /**
     * 命令标签
     */
    private TreeSet<String> tags;

    /**
     * 序列化反序列化时使用
     */
    public ShortcutCommand() {}

    public ShortcutCommand(String name, String command, Collection<String> tags) {
        this.name = name;
        this.command = command;
        this.tags = tags == null ? new TreeSet<>() : new TreeSet<>(tags);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public TreeSet<String> getTags() {
        return tags;
    }

    public void setTags(TreeSet<String> tags) {
        this.tags = tags;
    }
}
