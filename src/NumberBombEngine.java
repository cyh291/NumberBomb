import java.util.Random;

/**
 * 数字炸弹核心逻辑：维护当前范围与炸弹数字。
 */
public class NumberBombEngine {
    public enum GuessResult {
        INVALID,
        TOO_HIGH,
        TOO_LOW,
        BOOM
    }

    private final Random random = new Random();
    private int low;
    private int high;
    private int bomb;
    private boolean exploded;
    private int guessCount;

    public NumberBombEngine() {
        reset(0, 100);
    }

    public void reset(int minBound, int maxBound) {
        if (maxBound - minBound < 2) {
            throw new IllegalArgumentException("范围至少要能放下一个炸弹数字");
        }
        this.low = minBound;
        this.high = maxBound;
        this.bomb = minBound + 1 + random.nextInt(maxBound - minBound - 1);
        this.exploded = false;
        this.guessCount = 0;
    }

    public GuessResult guess(Integer number) {
        if (exploded) {
            return GuessResult.BOOM;
        }
        if (number == null || number <= low || number >= high) {
            return GuessResult.INVALID;
        }
        guessCount++;
        if (number == bomb) {
            exploded = true;
            return GuessResult.BOOM;
        }
        if (number > bomb) {
            high = number;
            return GuessResult.TOO_HIGH;
        }
        low = number;
        return GuessResult.TOO_LOW;
    }

    public int getLow() {
        return low;
    }

    public int getHigh() {
        return high;
    }

    public int getBomb() {
        return bomb;
    }

    public int getGuessCount() {
        return guessCount;
    }

    public boolean isExploded() {
        return exploded;
    }

    public String rangeText() {
        return "数字炸弹范围：" + low + "  ~  " + high;
    }
}
