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
    private static final GallowsPicture gallowsPicture = new GallowsPicture();
    private static List<String> dictionary;
    private static Set<Character> usedLetters = new LinkedHashSet<>();
    private static int remainingAttempts = MAX_ATTEMPTS;
    private static int correctLettersCount = 0;

    public static void main(String[] args) {
        try {
            runMainMenu();
        } catch (RuntimeException ex) {
            System.out.println("Ошибка при загрузке файла: " + ex.getMessage() + ". Работа программы завершена.");
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
        System.out.println(START_MESSAGE);
        char letter = readMenuChoice();
        if (Character.toUpperCase(letter) == START_GAME_KEY) {
            startGame();
        }
    }

    private static void startGame() {
        loadDictionary();
        String secretWord = getSecretWord();
        char[] secretWordMask = createMask(secretWord);

        showWord(secretWordMask);
        while (!isGameOver(secretWord)) {
            processGuess(secretWordMask, secretWord);
            showGameStatus(secretWordMask);
        }
        endGame(secretWord);
    }

    private static boolean isGameOver(String secretWord) {
        return remainingAttempts == 0 || correctLettersCount == secretWord.length();
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

    private static void processGuess(char[] maskedWord, String word) {
        System.out.println();
        System.out.println(INPUT_LETTER_MESSAGE);
        char letter = readGuessedLetter();
        isCorrectGuess(maskedWord, word, letter);
    }

    private static void showGameStatus(char[] secretWordMask) {
        drawHangman();
        showWord(secretWordMask);
        showRemainingAttempts();
        showUsedLetters();
    }

    private static void drawHangman() {
        System.out.println(gallowsPicture.getPictures(remainingAttempts));
    }

    private static void isCorrectGuess(char[] maskedWord, String word, char letter) {
        if (isUsedLetter(letter)) {
            System.out.println("Вы уже вводили такую букву.");
            return;
        }
        if (!revealMatchLetters(maskedWord, word, letter)) {
            System.out.println("Ой! Такой буквы нет.");
            remainingAttempts--;
        }
    }

    private static boolean revealMatchLetters(char[] maskedWord, String word, char letter) {
        boolean isLetterInMask = false;
        for (int i = 0; i < word.length(); i++) {
            if (letter == word.charAt(i)) {
                maskedWord[i] = letter;
                correctLettersCount++;
                isLetterInMask = true;
            }
        }
        return isLetterInMask;
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

    private static void endGame(String word) {
        if (remainingAttempts == 0) {
            printLoseMessage();
            System.out.println("Загаданным словом было - " + word);
        }
        if (correctLettersCount == word.length()) {
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