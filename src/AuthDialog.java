import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class AuthDialog extends JDialog {
    private final UserAccountStore store = UserAccountStore.getInstance();
    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JLabel statusLabel = new JLabel("请输入用户名和密码", SwingConstants.CENTER);

    public AuthDialog(JFrame owner) {
        super(owner, "登录 / 注册", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(360, 260);
        setLocationRelativeTo(owner);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBackground(new Color(18, 16, 28));
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        setContentPane(root);

        JLabel title = new JLabel("数字炸弹账号", SwingConstants.CENTER);
        title.setFont(new Font("Microsoft YaHei", Font.BOLD, 24));
        title.setForeground(new Color(232, 93, 58));
        root.add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(3, 2, 10, 10));
        form.setOpaque(false);

        JLabel userLabel = new JLabel("用户名：");
        userLabel.setForeground(new Color(230, 224, 245));
        userLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        form.add(userLabel);
        usernameField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(0, 32));
        form.add(usernameField);

        JLabel passLabel = new JLabel("密码：");
        passLabel.setForeground(new Color(230, 224, 245));
        passLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        form.add(passLabel);
        passwordField.setFont(new Font("Microsoft YaHei", Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(0, 32));
        form.add(passwordField);

        statusLabel.setFont(new Font("Microsoft YaHei", Font.PLAIN, 12));
        statusLabel.setForeground(new Color(255, 214, 102));
        form.add(statusLabel);
        form.add(new JLabel());
        root.add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 10, 0));
        buttons.setOpaque(false);

        JButton loginButton = new JButton("登录");
        styleButton(loginButton, new Color(255, 193, 7));
        loginButton.addActionListener(e -> handleLogin());

        JButton registerButton = new JButton("注册");
        styleButton(registerButton, new Color(111, 207, 151));
        registerButton.addActionListener(e -> handleRegister());

        buttons.add(loginButton);
        buttons.add(registerButton);
        root.add(buttons, BorderLayout.SOUTH);

        getRootPane().setDefaultButton(loginButton);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (!UserAccount.isValidUsername(username)) {
            statusLabel.setText("用户名长度需为3~20位，仅可含字母、数字、_、-");
            return;
        }
        if (!UserAccount.isValidPassword(password)) {
            statusLabel.setText("密码长度至少为6位");
            return;
        }

        if (!store.validateLogin(username, password)) {
            statusLabel.setText("用户名不存在或密码错误");
            return;
        }

        AuthSession.setCurrentUser(username);
        JOptionPane.showMessageDialog(this, "登录成功，欢迎回来：" + UserAccount.normalizeUsername(username),
                "登录成功", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void handleRegister() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (!UserAccount.isValidUsername(username)) {
            statusLabel.setText("用户名长度需为3~20位，仅可含字母、数字、_、-");
            return;
        }
        if (!UserAccount.isValidPassword(password)) {
            statusLabel.setText("密码长度至少为6位");
            return;
        }

        String normalizedUsername = UserAccount.normalizeUsername(username);
        if (store.usernameExists(normalizedUsername)) {
            statusLabel.setText("用户名已存在，请更换一个用户名");
            return;
        }

        if (!store.registerUser(normalizedUsername, password)) {
            statusLabel.setText("注册失败，请检查用户名和密码");
            return;
        }

        AuthSession.setCurrentUser(normalizedUsername);
        JOptionPane.showMessageDialog(this, "注册成功，已自动登录：" + normalizedUsername,
                "注册成功", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    private void styleButton(JButton button, Color background) {
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBackground(background);
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Microsoft YaHei", Font.BOLD, 14));
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }
}
