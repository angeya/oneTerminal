package top.angeya.oneterminal;

import atlantafx.base.theme.Dracula;
import atlantafx.base.theme.NordDark;
import atlantafx.base.theme.NordLight;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.LoggerFactory;
import top.angeya.oneterminal.util.ImageUtil;
import top.angeya.oneterminal.util.RegionUtil;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Author: angeya
 * @Date: 2025/8/10 17:14
 */
public class CommandManager {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(CommandManager.class);

    private final ObservableList<ShortcutCommand> shortcuts = FXCollections.observableArrayList();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Path configFile = Path.of(System.getProperty("user.home"), "commands.tmx");

    private final Stage stage;

    private final TerminalManager terminalManager;

    private TreeView<NodeData> treeView;

    public CommandManager(Stage stage, TerminalManager terminalManager) {
        this.stage = stage;
        this.terminalManager = terminalManager;
    }

    public VBox initView() {
        this.loadShortcuts();
        Button addBtn = new Button("", ImageUtil.createImageView(ImageResources.NEW_COMMAND_ICON, 16));
        Button newTabBtn = new Button("", ImageUtil.createImageView(ImageResources.NEW_TERMINAL_ICON, 16));
        RegionUtil.setSize(24, addBtn, newTabBtn);

        ChoiceBox<String> choiceBox = new ChoiceBox<>();
        // ImageUtil.createImageView(ImageResources.THEME_ICON, 16);
        choiceBox.getItems().add("Dracula");
        choiceBox.getItems().add("Nord Dark");
        choiceBox.getItems().add("Nord Light");

        choiceBox.setPadding(Insets.EMPTY);
        RegionUtil.setSize(48, 24, choiceBox);

        choiceBox.setOnAction((event) -> {
            int selectedIndex = choiceBox.getSelectionModel().getSelectedIndex();
            String themeName = choiceBox.getSelectionModel().getSelectedItem();
            LOG.info("选择主题: {}", themeName);
            Scene scene = stage.getScene();
            switch (themeName) {
                case "Dracula":
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(new Dracula().getUserAgentStylesheet());
                    break;
                case "Nord Dark":
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(new NordDark().getUserAgentStylesheet());
                    break;
                case "Nord Light":
                    scene.getStylesheets().clear();
                    scene.getStylesheets().add(new NordLight().getUserAgentStylesheet());
                    break;
            }
        });


        HBox buttons = new HBox(8, addBtn, newTabBtn, choiceBox);
        buttons.setPadding(new Insets(6));

        // 左侧命令管理树
        TreeView<NodeData> treeView = this.initCommandTree();

        VBox vBox = new VBox(buttons, treeView);
        vBox.setPadding(new Insets(8));
        addBtn.setOnAction(e -> showCommandEditDialog(stage));
        newTabBtn.setOnAction(e -> this.terminalManager.createTerminalTab("终端", null));

        VBox.setVgrow(treeView, Priority.ALWAYS);
        return vBox;
    }

    public TreeView<NodeData> initCommandTree() {
        this.treeView = new TreeView<>();
        this.treeView.setShowRoot(false);
        this.treeView.setPrefWidth(180);
        // customize cell to show only command name for leaves, tag for parent
        this.treeView.setCellFactory(tv -> new TreeCell<>() {
            @Override
            protected void updateItem(NodeData item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                    setContextMenu(null);
                } else if (item.isTag()) {
                    setText(item.getTag());
                    setStyle("-fx-font-weight: bold; -fx-padding: 4 2 4 2; -fx-font-size: 13px;");
                    setTooltip(null);
                    setContextMenu(null);
                } else {
                    ShortcutCommand cmd = item.getCommand();
                    setText(cmd.getName());
                    Tooltip tt = new Tooltip(cmd.getCommand());
                    setTooltip(tt);

                    setStyle("-fx-font-size: 12px; -fx-font-family: Deng");

                    // 命令右键
                    ContextMenu contextMenu = new ContextMenu();
                    MenuItem editItem = new MenuItem("编辑");
                    MenuItem copyItem = new MenuItem("复制命令");
                    MenuItem deleteItem = new MenuItem("删除");
                    contextMenu.getItems().addAll(editItem, copyItem, deleteItem);

                    editItem.setOnAction(actionEvent -> showCommandEditDialog(stage, cmd));
                    copyItem.setOnAction(actionEvent -> copyToClipboard(cmd.getCommand()));
                    deleteItem.setOnAction(actionEvent -> {
                        boolean removed = shortcuts.removeIf(s -> s.equals(cmd));
                        if (removed) {
                            saveShortcutsAsync();
                            refreshCommandTree();
                        }
                    });
                    this.setContextMenu(contextMenu);
                }
            }
        });
        // 双击命令，将命令输入到终端
        this.treeView.setOnMouseClicked(mouseEvent -> {
            if (mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2) {
                TreeItem<NodeData> treeItem = treeView.getSelectionModel().getSelectedItem();
                if (treeItem != null && !treeItem.getValue().isTag()) {
                    this.terminalManager.runCommandInCurrentTerminal(treeItem.getValue().getCommand().getCommand());
                }
            }
        });
        return treeView;
    }

    /**
     * 显示命令编辑对话框
     */
    private void showCommandEditDialog(Stage stage) {
        Dialog<ShortcutCommand> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("新增快捷命令");

        TextField nameField = new TextField();
        TextField cmdField = new TextField();
        TextField tagsField = new TextField();

        nameField.setPromptText("请输入名称");
        cmdField.setPromptText("请输入命令内容");
        tagsField.setPromptText("请输入标签，支持,分隔");

        // 创建 GridPane 作为布局容器
        GridPane container = new GridPane();
        container.setPadding(new Insets(20)); // 容器内边距
        container.setHgap(10);
        container.setVgap(10);

        container.add(new Label("名称:"), 0, 0);
        container.add(nameField, 1, 0);
        container.add(new Label("命令:"), 0, 1);
        container.add(cmdField, 1, 1);
        container.add(new Label("标签:"), 0, 2);
        container.add(tagsField, 1, 2);

        dialog.getDialogPane().setContent(container);

        ButtonType ok = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ok) {
                String n = nameField.getText().trim();
                String c = cmdField.getText().trim();
                String tagsRaw = tagsField.getText().trim();
                if (n.isEmpty() || c.isEmpty()) {
                    return null;
                }
                List<String> tags = parseTags(tagsRaw);

                // 可选：显示简单提示
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("提示");
                alert.setHeaderText(null);
                alert.setContentText("保存成功！");
                alert.showAndWait();

                return new ShortcutCommand(n, c, tags);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(sc -> {
            shortcuts.add(sc);
            saveShortcutsAsync();
            refreshCommandTree();
        });
    }

    /**
     * 异步保存命令
     */
    private void saveShortcutsAsync() {
        CompletableFuture.runAsync(() -> {
            try {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile.toFile(), new ArrayList<>(shortcuts));
            } catch (Exception e) {
                LOG.error("命令保存到文件异常", e);
            }
        });
    }


    /**
     * 刷新树
     */
    public void refreshCommandTree() {
        Map<String, List<ShortcutCommand>> tagMap = new TreeMap<>();
        for (ShortcutCommand command : shortcuts) {
            if (command.getTags() == null || command.getTags().isEmpty()) {
                tagMap.computeIfAbsent("未分组", k -> new ArrayList<>()).add(command);
            } else {
                for (String tag : command.getTags()) {
                    tagMap.computeIfAbsent(tag, k -> new ArrayList<>()).add(command);
                }
            }
        }

        TreeItem<NodeData> root = new TreeItem<>();
        for (Map.Entry<String, List<ShortcutCommand>> e : tagMap.entrySet()) {
            String tag = e.getKey();
            TreeItem<NodeData> tagNode = new TreeItem<>(NodeData.forTag(tag));
            // sort commands by name
            List<ShortcutCommand> list = e.getValue().stream()
                    .sorted(Comparator.comparing(ShortcutCommand::getName, String.CASE_INSENSITIVE_ORDER))
                    .toList();
            for (ShortcutCommand sc : list) {
                TreeItem<NodeData> cmdNode = new TreeItem<>(NodeData.forCommand(sc));
                tagNode.getChildren().add(cmdNode);
            }
            root.getChildren().add(tagNode);
        }
        treeView.setRoot(root);
        // 展开第一个节点
        root.getChildren().forEach(n -> n.setExpanded(false));
    }


    private List<String> parseTags(String raw) {
        if (raw == null || raw.isBlank()) {
            return Collections.emptyList();
        }
        return Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 显示编辑对话框
     *
     * @param stage   父级窗体
     * @param command 快捷命令
     */
    private void showCommandEditDialog(Stage stage, ShortcutCommand command) {
        Dialog<ShortcutCommand> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("编辑快捷命令");

        TextField nameField = new TextField(command.getName());
        TextField cmdField = new TextField(command.getCommand());
        TextField tagsField = new TextField(String.join(",", command.getTags() == null ? Collections.emptyList() : command.getTags()));

        VBox v = new VBox(8, new Label("名称:"), nameField, new Label("命令:"), cmdField, new Label("标签:"), tagsField);
        v.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(v);

        ButtonType ok = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == ok) {
                String n = nameField.getText().trim();
                String c = cmdField.getText().trim();
                String tagsRaw = tagsField.getText().trim();
                if (n.isEmpty() || c.isEmpty()) {
                    return null;
                }
                List<String> tags = parseTags(tagsRaw);
                return new ShortcutCommand(n, c, tags);
            }
            return null;
        });

        dialog.showAndWait().ifPresent(newCommand -> {
            // 新命令赋值
            command.setName(newCommand.getName());
            command.setCommand(newCommand.getCommand());
            command.setTags(newCommand.getTags());
            saveShortcutsAsync();
            refreshCommandTree();
        });
    }

    /**
     * 加载命令
     */
    public void loadShortcuts() {
        try {
            if (Files.exists(configFile)) {
                List<ShortcutCommand> list = objectMapper.readValue(configFile.toFile(), new TypeReference<>() {
                });
                shortcuts.setAll(list);
            } else {
                // 给默认命令
                shortcuts.add(new ShortcutCommand("ipconfig", "ipconfig", List.of("网络")));
                this.saveShortcutsAsync();
            }
        } catch (Exception e) {
            LOG.error("命令加载异常", e);
        }
    }

    /**
     * 复制到剪贴板
     *
     * @param text 文本
     */
    private void copyToClipboard(String text) {
        ClipboardContent content = new ClipboardContent();
        content.putString(text);
        Clipboard.getSystemClipboard().setContent(content);
    }

}
