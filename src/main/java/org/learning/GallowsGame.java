package org.learning;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class GallowsGame {
    private static final char START_GAME_KEY = 'С';
    private static final char END_GAME_KEY = 'В';
    private static final String START_MESSAGE = String.format(
            "Введите '%s' для начала игры или '%s' для выхода из игры.",
            START_GAME_KEY,
            END_GAME_KEY
    );
    private static final char HIDDEN_LETTER_SYMBOL = '*';
    private static final String INPUT_LETTER_MESSAGE = "Введите букву русского алфавита: ";
    private static final String DICTIONARY_PATH = "src/main/resources/dictionary.txt";
    private static final int MAX_ATTEMPTS = 6;
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    private static List<String> dictionary;
    private static Set<Character> usedLetters = new LinkedHashSet<>();
    private static int remainingAttempts = MAX_ATTEMPTS;

    public static void main(String[] args) {
        try {
            runMainMenu();
        } catch (RuntimeException ex) {
            System.out.println("Error loading dictionary: " + ex.getMessage() + ". The program will now be stopped.");
        }
    }

    private static void loadDictionary() {
        Path dictionaryPath = Path.of(DICTIONARY_PATH);
        try {
            dictionary = Files.readAllLines(dictionaryPath);
        } catch (IOException ex) {
            throw new RuntimeException("Dictionary file not found: " + dictionaryPath.toAbsolutePath());
        }
        if (dictionary.isEmpty()) {
            throw new IllegalStateException("Dictionary file is empty: " + dictionaryPath.toAbsolutePath());
        }
    }

    private static void runMainMenu() {
        while (true) {
            System.out.println(START_MESSAGE);
            char letter = readMenuChoice();
            if (letter == START_GAME_KEY) {
                startGame();
            }
            if (letter == END_GAME_KEY) {
                return;
            }
        }
    }

    private static void startGame() {
        loadDictionary();
        String secretWord = getSecretWord();
        char[] secretWordMask = createMask(secretWord);

        while (!isGameOver(secretWordMask)) {
            showGameStatus(secretWordMask);
            processGuess(secretWordMask, secretWord);
        }
        endGame(secretWord, secretWordMask);
    }

    private static char[] createMask(String secretWord) {
        char[] secretWordMask = new char[secretWord.length()];
        Arrays.fill(secretWordMask, HIDDEN_LETTER_SYMBOL);
        return secretWordMask;
    }

    private static String getSecretWord() {
        int wordIndex = random.nextInt(dictionary.size());
        return dictionary.get(wordIndex);
    }

    private static void processGuess(char[] secretWordMask, String secretWord) {
        System.out.println();
        System.out.println(INPUT_LETTER_MESSAGE);
        char letter = readGuessedLetter();
        if (isUsedLetter(letter)) {
            System.out.println("Вы уже вводили такую букву.");
            return;
        }
        if (isCorrectGuess(secretWord, letter)) {
            usedLetters.add(letter);
            openLetterInMask(secretWordMask, secretWord, letter);
            return;
        }
        System.out.println("Ой! Такой буквы нет.");
        remainingAttempts--;
    }

    private static boolean isCorrectGuess(String secretWord, char letter) {
        for (int i = 0; i < secretWord.length(); i++) {
            if (letter == secretWord.charAt(i)) {
                return true;
            }
        }
        return false;
    }

    private static void showGameStatus(char[] secretWordMask) {
        drawHangman();
        showWord(secretWordMask);
        showRemainingAttempts();
        showUsedLetters();
    }

    private static void drawHangman() {
        GallowsRenderer.render(remainingAttempts);
    }

    private static void openLetterInMask(char[] secretWordMask, String secretWord, char letter) {
        for (int i = 0; i < secretWord.length(); i++) {
            if (letter == secretWord.charAt(i)) {
                secretWordMask[i] = letter;
            }
        }
    }

    private static char readGuessedLetter() {
        while (true) {
            String line = scanner.next();
            while (line.length() != 1) {
                System.out.println();
                System.out.println(INPUT_LETTER_MESSAGE);
                line = scanner.next();
            }
            line = line.toLowerCase();
            char letter = line.charAt(0);
            if (letter >= 'а' && letter <= 'я' || letter == 'ё') {
                return letter;
            }
            System.out.println();
            System.out.println(INPUT_LETTER_MESSAGE);
        }
    }

    private static char readMenuChoice() {
        while (true) {
            String line = scanner.next();
            if (line.length() != 1) {
                System.out.println(START_MESSAGE);
                continue;
            }
            char letter = Character.toUpperCase(line.charAt(0));
            if (letter == START_GAME_KEY || letter == END_GAME_KEY) {
                return letter;
            }
            System.out.println(START_MESSAGE);
        }
    }

    private static void showRemainingAttempts() {
        System.out.println();
        System.out.println("Осталось попыток: " + remainingAttempts);
    }

    private static boolean isUsedLetter(char letter) {
        return usedLetters.contains(letter);
    }

    private static void showWord(char[] secretWordMask) {
        System.out.println("Ваше слово: ");
        for (char c : secretWordMask) {
            System.out.print(c + " ");
        }
    }

    private static void showUsedLetters() {
        System.out.println("Использованы буквы: ");
        for (char c : usedLetters) {
            System.out.print(c + " ");
        }
    }

    private static boolean isGameOver(char[] secretWordMask) {
        return isLose() || isWin(secretWordMask);
    }

    private static boolean isWin(char[] secretWordMask) {
        for (char c : secretWordMask) {
            if (c == HIDDEN_LETTER_SYMBOL) {
                return false;
            }
        }
        return true;
    }

    private static boolean isLose() {
        return remainingAttempts == 0;
    }

    private static void endGame(String secretWord, char[] secretWordMask) {
        if (isLose()) {
            printLoseMessage(secretWord);
        }
        if (isWin(secretWordMask)) {
            printWinMessage();
        }
        resetGameState();
    }

    private static void printLoseMessage(String word) {
        System.out.println();
        System.out.println("Вы проиграли!");
        System.out.println("Заданным словом было - " + word);
    }

    private static void printWinMessage() {
        System.out.println();
        System.out.println("Поздравляю, Вы победили!");
    }

    private static void resetGameState() {
        usedLetters = new LinkedHashSet<>();
        remainingAttempts = MAX_ATTEMPTS;
    }
}