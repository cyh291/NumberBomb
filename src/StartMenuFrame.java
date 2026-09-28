import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

/**
 * 启动界面：点击按钮弹出游戏窗口。
 */
public class StartMenuFrame extends JFrame {
    private final JLabel userLabel = new JLabel("未登录", SwingConstants.CENTER);

    public StartMenuFrame() {
        super("数字炸弹");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(460, 420);
        setLocationRelativeTo(null);
        setResizable(false);

        Color bg = new Color(18, 16, 28);
        Color card = new Color(36, 32, 52);
        Color accent = new Color(232, 93, 58);

        JPanel root = new JPanel(new BorderLayout(16, 16));
        root.setBackground(bg);
        root.setBorder(BorderFactory.createEmptyBorder(28, 32, 28, 32));
        setContentPane(root);

        JLabel title = new JLabel("数字炸弹", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 36));
        title.setForeground(accent);

        JLabel rules = new JLabel(
                "<html><div style='text-align:center;line-height:1.7'>"
                        + "第一行会显示当前炸弹范围<br>"
                        + "数字大了就往小调，小了就往大调<br>"
                        + "超出范围的输入无效，必须重新输入<br>"
                        + "猜中隐藏数字就会爆炸"
                        + "</div></html>",
                SwingConstants.CENTER
        );
        rules.setFont(new Font("Microsoft YaHei", Font.PLAIN, 16));
        rules.setForeground(new Color(230, 224, 245));
        rules.setOpaque(true);
        rules.setBackground(card);
        rules.setBorder(BorderFactory.createEmptyBorder(18, 16, 18, 16));

        userLabel.setFont(new Font("Microsoft YaHei", Font.BOLD, 15));
        userLabel.setForeground(new Color(255, 214, 102));
        userLabel.setOpaque(true);
        userLabel.setBackground(card);
        userLabel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JButton authButton = new JButton("登录 / 注册");
        styleActionButton(authButton, new Color(100, 181, 246));
        authButton.addActionListener(e -> openAuthDialog());

        JButton logoutButton = new JButton("退出登录");
        styleActionButton(logoutButton, new Color(189, 189, 189));
        logoutButton.addActionListener(e -> {
            AuthSession.logout();
            refreshUserStatus();
        });

        JPanel statusPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        statusPanel.setOpaque(false);
        statusPanel.add(userLabel);
        statusPanel.add(authButton);
        statusPanel.add(logoutButton);

        JButton startButton = new JButton("进入游戏");
        startButton.setUI(new BasicButtonUI());
        startButton.setOpaque(true);
        startButton.setContentAreaFilled(true);
        startButton.setFont(new Font("Microsoft YaHei", Font.BOLD, 20));
        startButton.setBackground(new Color(255, 193, 7));
        startButton.setForeground(Color.BLACK);
        startButton.setFocusPainted(false);
        startButton.setPreferredSize(new Dimension(180, 48));
        startButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        startButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(10, 24, 10, 24)
        ));
        startButton.addActionListener(e -> {
            if (!AuthSession.isLoggedIn()) {
                openAuthDialog();
                if (!AuthSession.isLoggedIn()) {
                    return;
                }
            }
            GameDialog dialog = new GameDialog(this);
            dialog.setVisible(true);
        });

        JPanel buttonWrap = new JPanel();
        buttonWrap.setOpaque(false);
        buttonWrap.add(startButton);

        JPanel center = new JPanel(new GridLayout(3, 1, 0, 16));
        center.setOpaque(false);
        center.add(rules);
        center.add(statusPanel);
        center.add(buttonWrap);

        root.add(title, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);

        refreshUserStatus();
    }

    private void refreshUserStatus() {
        String currentUser = AuthSession.getCurrentUser();
        if (currentUser == null || currentUser.trim().isEmpty()) {
            userLabel.setText("未登录");
            userLabel.setForeground(new Color(255, 214, 102));
        } else {
            userLabel.setText("当前用户：" + currentUser);
            userLabel.setForeground(new Color(120, 230, 160));
        }
    }

    private void openAuthDialog() {
        AuthDialog dialog = new AuthDialog(this);
        dialog.setVisible(true);
        refreshUserStatus();
    }

    private void styleActionButton(JButton button, Color background) {
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        button.setBackground(background);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }
}
