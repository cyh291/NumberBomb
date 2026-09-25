import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

/**
 * 弹出的游戏界面：第一行始终显示当前炸弹范围。
 */
public class GameDialog extends JDialog {
    private static final Color BG = new Color(28, 18, 18);
    private static final Color PANEL = new Color(48, 26, 26);
    private static final Color ACCENT = new Color(232, 93, 58);
    private static final Color TEXT = new Color(255, 236, 220);
    private static final Font TITLE_FONT = new Font("Microsoft YaHei", Font.BOLD, 26);
    private static final Font BODY_FONT = new Font("Microsoft YaHei", Font.PLAIN, 16);
    private static final Font RANGE_FONT = new Font("Microsoft YaHei", Font.BOLD, 22);

    private final NumberBombEngine engine = new NumberBombEngine();
    private final JLabel rangeLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel hintLabel = new JLabel("在范围内输入一个整数，点确认拆弹。", SwingConstants.CENTER);
    private final JTextField inputField = new JTextField();
    private final JTextArea logArea = new JTextArea();
    private final JButton confirmButton = new JButton("确认");

    public GameDialog(JFrame owner) {
        super(owner, "数字炸弹", false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(520, 560);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(BG);
        root.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        setContentPane(root);

        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.setOpaque(false);
        JLabel title = new JLabel("拆除数字炸弹", SwingConstants.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(ACCENT);
        rangeLabel.setFont(RANGE_FONT);
        rangeLabel.setForeground(TEXT);
        hintLabel.setFont(BODY_FONT);
        hintLabel.setForeground(new Color(255, 210, 170));
        header.add(title, BorderLayout.NORTH);
        header.add(rangeLabel, BorderLayout.CENTER);
        header.add(hintLabel, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setOpaque(false);

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 20));
        inputField.setBackground(PANEL);
        inputField.setForeground(TEXT);
        inputField.setCaretColor(TEXT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        styleButton(confirmButton, new Color(255, 213, 79));
        confirmButton.setPreferredSize(new Dimension(96, 44));
        inputRow.add(inputField, BorderLayout.CENTER);
        inputRow.add(confirmButton, BorderLayout.EAST);

        logArea.setEditable(false);
        logArea.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        logArea.setBackground(PANEL);
        logArea.setForeground(TEXT);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        logArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scroll = new JScrollPane(logArea);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(90, 45, 40)));

        center.add(inputRow, BorderLayout.NORTH);
        center.add(scroll, BorderLayout.CENTER);
        root.add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        bottom.setOpaque(false);
        JButton restart = new JButton("再来一局");
        JButton close = new JButton("关闭窗口");
        styleButton(restart, new Color(129, 199, 132));
        styleButton(close, new Color(189, 189, 189));
        bottom.add(restart);
        bottom.add(close);
        root.add(bottom, BorderLayout.SOUTH);

        confirmButton.addActionListener(this::onConfirm);
        inputField.addActionListener(this::onConfirm);
        restart.addActionListener(e -> startNewRound());
        close.addActionListener(e -> dispose());

        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW
        );

        startNewRound();
    }

    private void startNewRound() {
        engine.reset(0, 100);
        rangeLabel.setText(engine.rangeText());
        hintLabel.setText("在范围内输入一个整数，点确认拆弹。");
        hintLabel.setForeground(new Color(255, 210, 170));
        logArea.setText("新对局开始。炸弹藏在开区间 (" + engine.getLow() + ", " + engine.getHigh() + ") 里。\n"
                + "超出当前范围的输入无效，需要重新输入。\n");
        inputField.setText("");
        inputField.setEnabled(true);
        confirmButton.setEnabled(true);
        inputField.requestFocusInWindow();
    }

    private void onConfirm(ActionEvent e) {
        if (engine.isExploded()) {
            return;
        }
        Integer number = parseInput(inputField.getText());
        NumberBombEngine.GuessResult result = engine.guess(number);

        switch (result) {
            case INVALID -> {
                hintLabel.setText("无效输入，请重新输入当前范围内的整数。");
                hintLabel.setForeground(new Color(255, 196, 90));
                logArea.append("无效输入：" + inputField.getText().trim() + "（必须大于 "
                        + engine.getLow() + " 且小于 " + engine.getHigh() + "）\n");
            }
            case TOO_HIGH -> {
                hintLabel.setText("数字大了，往小的调！");
                hintLabel.setForeground(new Color(255, 140, 90));
                rangeLabel.setText(engine.rangeText());
                logArea.append("猜测 " + number + " 太大了，往小调。新范围："
                        + engine.getLow() + " ~ " + engine.getHigh() + "\n");
            }
            case TOO_LOW -> {
                hintLabel.setText("数字小了，往大的调！");
                hintLabel.setForeground(new Color(120, 200, 255));
                rangeLabel.setText(engine.rangeText());
                logArea.append("猜测 " + number + " 太小了，往大调。新范围："
                        + engine.getLow() + " ~ " + engine.getHigh() + "\n");
            }
            case BOOM -> {
                hintLabel.setText("轰！你踩中了数字炸弹 " + engine.getBomb() + "。");
                hintLabel.setForeground(ACCENT);
                rangeLabel.setText("数字炸弹范围：已爆炸");
                logArea.append("猜测 " + number + " —— 爆炸！共用了 "
                        + engine.getGuessCount() + " 次有效猜测。\n点击「再来一局」重新开始。\n");
                inputField.setEnabled(false);
                confirmButton.setEnabled(false);
            }
        }
        inputField.setText("");
        inputField.requestFocusInWindow();
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    private Integer parseInput(String raw) {
        if (raw == null) {
            return null;
        }
        String text = raw.trim();
        if (text.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private void styleButton(JButton button, Color background) {
        button.setUI(new BasicButtonUI());
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setFont(BODY_FONT);
        button.setBackground(background);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }
}
