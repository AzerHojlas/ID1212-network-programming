/**
 * @authors Porsev Aslan & Azer Hojlas 2021-12-09
 * Guessing game logic.
 */

import java.util.Random;

public class game {

    int randomNumber;
    int attempts;

    public int getRandomNumber() {
        return randomNumber;
    }

    public int getAttempts() {
        return attempts;
    }

    public game() {
        this.randomNumber = generateRandomNumber();
        attempts = 0;
    }

    public int generateRandomNumber() {
        return new Random().nextInt(100) + 1;
    }

    public void restart() {
        this.randomNumber = generateRandomNumber();
        attempts = 0;
    }

    public String guess(int guess) {
        this.attempts = this.attempts + 1;
        if (guess == this.randomNumber) {
            return "Correct";
        } else if (guess < this.randomNumber) {
            return "Higher";
        } else {
            return "Lower";
        }
    }
}
