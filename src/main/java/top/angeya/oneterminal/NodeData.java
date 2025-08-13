package top.angeya.oneterminal;

/**
 * @Author: angeya
 * @Date: 2025/8/10 17:21
 */
public class NodeData {
    private final boolean isTag;
    private final String tag;
    private final ShortcutCommand command;

    private NodeData(boolean isTag, String tag, ShortcutCommand command) {
        this.isTag = isTag;
        this.tag = tag;
        this.command = command;
    }

    public static NodeData forTag(String tag) {
        return new NodeData(true, tag, null);
    }

    public static NodeData forCommand(ShortcutCommand cmd) {
        return new NodeData(false, null, cmd);
    }

    public boolean isTag() {
        return isTag;
    }

    public String getTag() {
        return tag;
    }

    public ShortcutCommand getCommand() {
        return command;
    }
}
