package org.learning;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static final Character START_GAME_CHAR = 'С';
    private static final Character END_GAME_CHAR = 'В';
    private static final String START_MESSAGE = String.format(
            "Нажмите '%s' для начала игры или '%s' для выхода из игры.",
            START_GAME_CHAR,
            END_GAME_CHAR
    );
    private static final String INPUT_LETTER_MESSAGE = "\nВведите букву: ";
    private static final String DICTIONARY_PATH = "src/main/resources/dictionary.txt";
    private static final int MAX_ATTEMPTS = 6;
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    private static List<String> dictionary;
    private static Set<Character> usedLetters = new HashSet<>();
    private static int remainingAttempts = MAX_ATTEMPTS;
    private static int correctLettersCount = 0;

    public static void main(String[] args) {
        try {
            loadDictionary();
        } catch (IOException | IllegalStateException ex) {
            System.out.println("Ошибка при загрузке файла: " + ex.getMessage() + ". Работа программы завершена.");
            return;
        }
        showMainMenu();
    }

    private static void loadDictionary() throws IOException {
        try {
            dictionary = Files.readAllLines(Path.of(DICTIONARY_PATH));
        } catch (IOException ex) {
            throw new IOException("файл словаря не найден в " + ex.getMessage());
        }
        if (dictionary.isEmpty()) {
            throw new IllegalStateException("файл словаря пуст");
        }
    }

    private static void showMainMenu() {
        System.out.println(START_MESSAGE);
        char letter = validateMenuLetter();
        if (letter == START_GAME_CHAR) {
            startGame();
        }
    }

    private static void startGame() {
        int wordIndex = random.nextInt(dictionary.size());
        char[] word = dictionary.get(wordIndex).toCharArray();
        char[] hiddenWord = new char[word.length];
        Arrays.fill(hiddenWord, ('*'));
        showWord(hiddenWord);
        while (!(remainingAttempts == 0 || correctLettersCount == word.length)) {
            guessLetter(hiddenWord, word);
        }
        endGame(word);
    }

    private static void guessLetter(char[] visibleWord, char[] word) {
        System.out.println(INPUT_LETTER_MESSAGE);
        char letter = validateGuessedLetter();
        if (isUsedLetter(letter)) {
            System.out.println("Вы уже вводили такую букву!");
            showUsedLetters();
            return;
        }
        if (!isCorrectGuess(visibleWord, word, letter)) {
            System.out.println("Ой! Такой буквы нет!");
            remainingAttempts--;
            showRemainingAttempts();
            drawHangman();
        }
        showWord(visibleWord);
        showUsedLetters();
    }

    private static boolean isCorrectGuess(char[] visibleWord, char[] word, char letter) {
        boolean correctGuess = false;
        for (int i = 0; i < word.length; i++) {
            if (letter == word[i]) {
                visibleWord[i] = letter;
                correctLettersCount++;
                correctGuess = true;
            }
        }
        return correctGuess;
    }

    private static char validateGuessedLetter() {
        String line = scanner.next();
        while (line.length() != 1) {
            System.out.println(INPUT_LETTER_MESSAGE);
            line = scanner.next();
        }
        line = line.toLowerCase();
        char letter = line.charAt(0);
        if (letter >= 'а' && letter <= 'я' || letter == 'ё') {
            return letter;
        }
        System.out.println(INPUT_LETTER_MESSAGE);
        return validateGuessedLetter();
    }

    private static char validateMenuLetter() {
        String line = scanner.next();
        while (line.length() != 1) {
            System.out.println(START_MESSAGE);
            line = scanner.next();
        }
        while (!(line.charAt(0) == START_GAME_CHAR || line.charAt(0) == END_GAME_CHAR)) {
            System.out.println(START_MESSAGE);
            line = scanner.next();
        }
        return line.charAt(0);
    }

    private static void showRemainingAttempts() {
        String attempt = switch (remainingAttempts) {
            case 4, 3, 2 -> " попытки";
            case 1 -> " попытка";
            default -> " попыток";
        };
        System.out.println("У вас осталось " + remainingAttempts + attempt);
    }

    private static boolean isUsedLetter(char letter) {
        if (usedLetters.contains(letter)) {
            return true;
        }
        usedLetters.add(letter);
        return false;
    }

    private static void drawHangman() {
        switch (remainingAttempts) {
            case 5:
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                break;
            case 4:
                System.out.println(" ---");
                System.out.println("|/  |");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                break;
            case 3:
                System.out.println(" ---");
                System.out.println("|/  |");
                System.out.println("|   *");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                break;
            case 2:
                System.out.println(" ---");
                System.out.println("|/  |");
                System.out.println("|   *");
                System.out.println("|  /||");
                System.out.println("|");
                System.out.println("|");
                System.out.println("|");
                break;
            case 1:
                System.out.println(" ---");
                System.out.println("|/  |");
                System.out.println("|   *");
                System.out.println("|  /||");
                System.out.println("|   |");
                System.out.println("|");
                System.out.println("|");
                break;
            case 0:
                System.out.println(" ---");
                System.out.println("|/  |");
                System.out.println("|   *");
                System.out.println("|  /||");
                System.out.println("|   |");
                System.out.println("|  /\\");
                System.out.println("|");
                break;
        }
    }

    private static void showWord(char[] visibleWord) {
        System.out.println("Ваше слово: ");
        for (char c : visibleWord) {
            System.out.print(c + " ");
        }
    }

    private static void showUsedLetters() {
        System.out.println("\nИспользованы буквы: ");
        for (char c : usedLetters) {
            System.out.print(c);
        }
    }

    private static void endGame(char[] word) {
        if (remainingAttempts == 0) {
            printLoseMessage();
        }
        if (correctLettersCount == word.length) {
            printWinMessage();
        }
        resetGameState();
        showMainMenu();
    }

    private static void printLoseMessage() {
        System.out.println("Поздравляю, Вы выиграли!");
    }

    private static void printWinMessage() {
        System.out.println("Вы проиграли!");
    }

    private static void resetGameState() {
        usedLetters = new HashSet<>();
        remainingAttempts = MAX_ATTEMPTS;
        correctLettersCount = 0;
    }
}