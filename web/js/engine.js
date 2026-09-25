class NumberBombEngine {
  constructor() {
    this.reset(0, 100);
  }

  reset(minBound, maxBound) {
    this.low = minBound;
    this.high = maxBound;
    this.bomb = minBound + 1 + Math.floor(Math.random() * (maxBound - minBound - 1));
    this.exploded = false;
    this.guessCount = 0;
  }

  guess(number) {
    if (this.exploded) {
      return "BOOM";
    }
    if (!Number.isInteger(number) || number <= this.low || number >= this.high) {
      return "INVALID";
    }
    this.guessCount += 1;
    if (number === this.bomb) {
      this.exploded = true;
      return "BOOM";
    }
    if (number > this.bomb) {
      this.high = number;
      return "TOO_HIGH";
    }
    this.low = number;
    return "TOO_LOW";
  }

  rangeText() {
    return "数字炸弹范围：" + this.low + "  ~  " + this.high;
  }
}
