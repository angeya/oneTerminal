package top.angeya.oneterminal;

import com.kodedu.terminalfx.TerminalBuilder;
import com.kodedu.terminalfx.TerminalTab;
import com.kodedu.terminalfx.config.TerminalConfig;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.paint.Color;
import javafx.scene.web.WebView;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

/**
 * @Author: angeya
 * @Date: 2025/8/10 17:15
 */
public class TerminalManager {

    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(TerminalManager.class);

    private TabPane tabPane;

    public TabPane initView() {
        this.tabPane = new TabPane();
        this.createTerminalTab("默认终端", null);
        return this.tabPane;
    }

    /**
     * 创建一个命令行窗口
     *
     * @param title       标题
     * @param initCommand 初始命令
     */
    public void createTerminalTab(String title, String initCommand) {
        TerminalConfig config = new TerminalConfig();
        config.setBackgroundColor(Color.rgb(16, 16, 16));
        config.setForegroundColor(Color.rgb(80, 180, 80));
        config.setCursorColor(Color.rgb(255, 255, 255, 0.6));
        config.setFontFamily("Consolas");
        config.setFontSize(14);
        TerminalTab terminalTab = new TerminalBuilder(config).newTerminal();
        terminalTab.setText(title);
        this.tabPane.getTabs().add(terminalTab);
        this.tabPane.getSelectionModel().select(terminalTab);

        // 如果初始命令不为空，则执行
        if (StringUtils.isNotBlank(initCommand)) {
            Platform.runLater(() -> terminalTab.getTerminal().command(initCommand));
        }
    }

    public void runCommandInCurrentTerminal(String command) {
        if (command == null || command.isBlank()) {
            return;
        }
        Tab selected = tabPane.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        if (selected instanceof TerminalTab) {
            TerminalTab ttab = (TerminalTab) selected;
            // send command + enter
            Platform.runLater(() -> {
                ttab.getTerminal().command(command);
                ttab.getTerminal().setFocusTraversable(true);
                ttab.getTerminal().requestFocus();
                Platform.runLater(() -> {
                    // 找到底层 WebView，并让它获取焦点
                    Node terminalNode = ttab.getTerminal(); // 实际是 TerminalView
                    if (terminalNode.lookup(".web-view") instanceof WebView webView) {
                        webView.requestFocus();
                        System.out.println("执行命令：" + command);
                    }
                });
            });
        }
    }

}
