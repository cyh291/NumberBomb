
const AUTH_API = 'https://numberbomb-7.onrender.com'; // 换成你 Render Web Service 的地址
//
const authOverlay = document.getElementById('auth-overlay');
const authTitle = document.getElementById('auth-title');
const authForm = document.getElementById('auth-form');
const authUsername = document.getElementById('auth-username');
const authPassword = document.getElementById('auth-password');
const authSwitch = document.getElementById('auth-switch');
const authMessage = document.getElementById('auth-message');

let isLoginMode = true;

function openAuth(loginMode) {
  isLoginMode = loginMode;
  authTitle.textContent = loginMode ? '登录' : '注册';
  authSwitch.textContent = loginMode ? '没有账号？去注册' : '已有账号？去登录';
  authMessage.textContent = '';
  authUsername.value = '';
  authPassword.value = '';
  authOverlay.classList.remove('hidden');
}

authSwitch.addEventListener('click', () => openAuth(!isLoginMode));
document.getElementById('login-btn').addEventListener('click', () => openAuth(true));
document.getElementById('register-btn').addEventListener('click', () => openAuth(false));

authForm.addEventListener('submit', async (e) => {
  e.preventDefault();
  const username = authUsername.value.trim();
  const password = authPassword.value.trim();
  if (!username || !password) {
    authMessage.textContent = '用户名和密码不能为空';
    authMessage.style.color = '#e85d3a';
    return;
  }

  const endpoint = isLoginMode ? '/api/login' : '/api/register';
  try {
    const res = await fetch(AUTH_API + endpoint, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ username, password })
    });
    const data = await res.json();
    if (res.ok) {
      authMessage.textContent = data.message;
      authMessage.style.color = '#81c784';
      if (isLoginMode) {
        setTimeout(() => authOverlay.classList.add('hidden'), 800);
      }
    } else {
      authMessage.textContent = data.error;
      authMessage.style.color = '#e85d3a';
    }
  } catch (err) {
    authMessage.textContent = '网络错误，请稍后再试';
    authMessage.style.color = '#e85d3a';
  }
});

authOverlay.addEventListener('click', (e) => {
  if (e.target === authOverlay) authOverlay.classList.add('hidden');
});