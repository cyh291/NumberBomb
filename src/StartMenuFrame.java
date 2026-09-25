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
            GameDialog dialog = new GameDialog(this);
            dialog.setVisible(true);
        });

        JPanel buttonWrap = new JPanel();
        buttonWrap.setOpaque(false);
        buttonWrap.add(startButton);

        JPanel center = new JPanel(new GridLayout(2, 1, 0, 16));
        center.setOpaque(false);
        center.add(rules);
        center.add(buttonWrap);

        root.add(title, BorderLayout.NORTH);
        root.add(center, BorderLayout.CENTER);
    }
}
