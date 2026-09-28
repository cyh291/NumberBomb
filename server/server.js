const express = require('express');
const bcrypt = require('bcryptjs');
const cors = require('cors');
const sqlite3 = require('sqlite3').verbose();
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(express.json());

// 用 SQLite 存用户数据（文件存在 server 目录下）
const db = new sqlite3.Database(path.join(__dirname, 'users.db'));
db.run(`CREATE TABLE IF NOT EXISTS users (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  username TEXT UNIQUE,
  password TEXT
)`);

// 注册接口
app.post('/api/register', async (req, res) => {
  const { username, password } = req.body;
  if (!username || !password) return res.status(400).json({ error: '用户名和密码不能为空' });
  const hashed = await bcrypt.hash(password, 10);
  db.run('INSERT INTO users (username, password) VALUES (?, ?)', [username, hashed], function(err) {
    if (err) return res.status(400).json({ error: '用户名已存在' });
    res.json({ message: '注册成功', userId: this.lastID });
  });
});

// 登录接口
app.post('/api/login', (req, res) => {
  const { username, password } = req.body;
  db.get('SELECT * FROM users WHERE username = ?', [username], async (err, user) => {
    if (err || !user) return res.status(400).json({ error: '用户不存在' });
    const ok = await bcrypt.compare(password, user.password);
    if (!ok) return res.status(400).json({ error: '密码错误' });
    res.json({ message: '登录成功', userId: user.id });
  });
});

app.listen(PORT, () => console.log(`Server running on port ${PORT}`));