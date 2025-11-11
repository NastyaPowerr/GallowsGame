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
    private static final String INPUT_LETTER_MESSAGE = "Введите букву русского алфавита: ";
    private static final String DICTIONARY_PATH = "src/main/resources/dictionary.txt";
    private static final int MAX_ATTEMPTS = 6;
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    private static final GallowsPicture gallowsPicture = new GallowsPicture();
    private static List<String> dictionary;
    private static Set<Character> usedLetters = new LinkedHashSet<>();
    private static int remainingAttempts = MAX_ATTEMPTS;
    private static int correctLettersCount = 0;

    public static void main(String[] args) {
        runMainMenu();
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
        System.out.println(START_MESSAGE);
        char letter = validateMenuLetter();
        if (Character.toUpperCase(letter) == START_GAME_KEY) {
            try {
                loadDictionary();
            } catch (RuntimeException ex) {
                System.out.println("Ошибка при загрузке файла: " + ex.getMessage() + ". Работа программы завершена.");
                return;
            }
            startGame();
        }
    }

    private static void startGame() {
        int wordIndex = random.nextInt(dictionary.size());
        char[] word = dictionary.get(wordIndex).toCharArray();
        char[] maskedWord = new char[word.length];
        Arrays.fill(maskedWord, ('*'));
        showWord(maskedWord);
        while (!(remainingAttempts == 0 || correctLettersCount == word.length)) {
            guessLetter(maskedWord, word);
        }
        endGame(word);
    }

    private static void guessLetter(char[] maskedWord, char[] word) {
        System.out.println();
        System.out.println(INPUT_LETTER_MESSAGE);
        char letter = validateGuessedLetter();
        if (isUsedLetter(letter)) {
            System.out.println("Вы уже вводили такую букву.");
            showUsedLetters();
            return;
        }
        if (!isCorrectGuess(maskedWord, word, letter)) {
            System.out.println("Ой! Такой буквы нет.");
            remainingAttempts--;
            showRemainingAttempts();
            drawHangman();
        }
        showWord(maskedWord);
        showUsedLetters();
    }

    private static void drawHangman() {
        System.out.println(gallowsPicture.getPictures(remainingAttempts));
    }

    private static boolean isCorrectGuess(char[] maskedWord, char[] word, char letter) {
        boolean correctGuess = false;
        for (int i = 0; i < word.length; i++) {
            if (letter == word[i]) {
                maskedWord[i] = letter;
                correctLettersCount++;
                correctGuess = true;
            }
        }
        return correctGuess;
    }

    private static char validateGuessedLetter() {
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
        return validateGuessedLetter();
    }

    private static char validateMenuLetter() {
        String line = scanner.next();
        while (line.length() != 1) {
            System.out.println(START_MESSAGE);
            line = scanner.next();
        }
        while (!(Character.toUpperCase(line.charAt(0)) == START_GAME_KEY || Character.toUpperCase(line.charAt(0)) == END_GAME_KEY)) {
            System.out.println(START_MESSAGE);
            line = scanner.next();
        }
        return line.charAt(0);
    }

    private static void showRemainingAttempts() {
        System.out.println("Осталось попыток: " + remainingAttempts);
    }

    private static boolean isUsedLetter(char letter) {
        if (usedLetters.contains(letter)) {
            return true;
        }
        usedLetters.add(letter);
        return false;
    }

    private static void showWord(char[] maskedWord) {
        System.out.println("Ваше слово: ");
        for (char c : maskedWord) {
            System.out.print(c + " ");
        }
    }

    private static void showUsedLetters() {
        System.out.println("\nИспользованы буквы: ");
        for (char c : usedLetters) {
            System.out.print(c + " ");
        }
    }

    private static void endGame(char[] word) {
        if (remainingAttempts == 0) {
            printLoseMessage();
            System.out.println("Загаданным словом было - " + new String(word));
        }
        if (correctLettersCount == word.length) {
            printWinMessage();
        }
        resetGameState();
        runMainMenu();
    }

    private static void printLoseMessage() {
        System.out.println("\nВы проиграли!");
    }

    private static void printWinMessage() {
        System.out.println("\nПоздравляю, Вы победили!");
    }

    private static void resetGameState() {
        usedLetters = new LinkedHashSet<>();
        remainingAttempts = MAX_ATTEMPTS;
        correctLettersCount = 0;
    }
}