const engine = new NumberBombEngine();

const overlay = document.getElementById("overlay");
const rangeLine = document.getElementById("range-line");
const hintLine = document.getElementById("hint-line");
const logEl = document.getElementById("log");
const inputEl = document.getElementById("guess-input");
const confirmBtn = document.getElementById("confirm-btn");

function setHint(text, kind) {
  hintLine.textContent = text;
  hintLine.className = "hint hint-" + kind;
}

function appendLog(text) {
  logEl.textContent += text + "\n";
  logEl.scrollTop = logEl.scrollHeight;
}

function startNewRound() {
  engine.reset(0, 100);
  rangeLine.textContent = engine.rangeText();
  setHint("在范围内输入一个整数，点确认拆弹。", "normal");
  logEl.textContent =
    "新对局开始。炸弹藏在开区间 (" + engine.low + ", " + engine.high + ") 里。\n" +
    "超出当前范围的输入无效，需要重新输入。\n";
  inputEl.value = "";
  inputEl.disabled = false;
  confirmBtn.disabled = false;
  inputEl.focus();
}

function openGame() {
  overlay.classList.remove("hidden");
  startNewRound();
}

function closeGame() {
  overlay.classList.add("hidden");
}

function parseInput(raw) {
  const text = (raw || "").trim();
  if (!text) {
    return null;
  }
  if (!/^-?\d+$/.test(text)) {
    return null;
  }
  return Number(text);
}

function onConfirm(event) {
  event.preventDefault();
  if (engine.exploded) {
    return;
  }

  const raw = inputEl.value;
  const number = parseInput(raw);
  const result = engine.guess(number);

  if (result === "INVALID") {
    setHint("无效输入，请重新输入当前范围内的整数。", "invalid");
    appendLog("无效输入：" + raw.trim() + "（必须大于 " + engine.low + " 且小于 " + engine.high + "）");
  } else if (result === "TOO_HIGH") {
    setHint("数字大了，往小的调！", "high");
    rangeLine.textContent = engine.rangeText();
    appendLog("猜测 " + number + " 太大了，往小调。新范围：" + engine.low + " ~ " + engine.high);
  } else if (result === "TOO_LOW") {
    setHint("数字小了，往大的调！", "low");
    rangeLine.textContent = engine.rangeText();
    appendLog("猜测 " + number + " 太小了，往大调。新范围：" + engine.low + " ~ " + engine.high);
  } else if (result === "BOOM") {
    setHint("轰！你踩中了数字炸弹 " + engine.bomb + "。", "boom");
    rangeLine.textContent = "数字炸弹范围：已爆炸";
    appendLog("猜测 " + number + " —— 爆炸！共用了 " + engine.guessCount + " 次有效猜测。");
    appendLog("点击「再来一局」重新开始。");
    inputEl.disabled = true;
    confirmBtn.disabled = true;
  }

  inputEl.value = "";
  if (!engine.exploded) {
    inputEl.focus();
  }
}

document.getElementById("enter-game").addEventListener("click", openGame);
document.getElementById("guess-form").addEventListener("submit", onConfirm);
document.getElementById("restart-btn").addEventListener("click", startNewRound);
document.getElementById("close-btn").addEventListener("click", closeGame);
document.addEventListener("keydown", (event) => {
  if (event.key === "Escape" && !overlay.classList.contains("hidden")) {
    closeGame();
  }
});
