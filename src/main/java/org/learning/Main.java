package org.learning;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    private static List<String> dictionary;
    private static final Scanner scanner = new Scanner(System.in);
    private static final Random random = new Random();
    private static Set<Character> usedLetters = new HashSet<>();
    private static int remainingAttempts = 6;
    private static int correctGuess = 0;

    public static void main(String[] args) {
        if (loadDictionary()) return;
        showMainMenu();
    }

    private static boolean loadDictionary() {
        try {
            dictionary = Files.readAllLines(Path.of("src/main/resources/dictionary.txt"));
        } catch (IOException ex) {
            System.out.println("Файл не найден.");
            return true;
        }
        if (dictionary.isEmpty()) {
            System.out.println("Файл со словами пуст.");
            return true;
        }
        return false;
    }

    private static void showMainMenu() {
        System.out.println("Нажмите 'С' для начала игры или 'В' для выхода из игры.");
        String line = scanner.next();
        while (!(line.equals("С") || line.equals("В"))) {
            System.out.println("Нажмите 'С' для начала игры или 'В' для выхода из игры.");
            line = scanner.next();
        }
        if (line.equals("С")) {
            startGame();
        } else {
            System.exit(0);
        }
    }

    private static void startGame() {
        int wordIndex = random.nextInt(dictionary.size());
        char[] word = dictionary.get(wordIndex).toCharArray();
        char[] visibleWord = new char[word.length];
        Arrays.fill(visibleWord, ('*'));
        showWord(visibleWord);
        while (!(remainingAttempts == 0 || correctGuess == word.length)) {
            guessLetter(visibleWord, word);
        }
        endGame(word);
    }

    private static void guessLetter(char[] visibleWord, char[] word) {
        System.out.println();
        System.out.println("Введите букву:");
        char letter = validateLetter();
        if (!addUsedLetter(letter)) {
            System.out.println("Вы уже вводили такую букву!");
            showUsedLetters();
            return;
        }
        boolean rightGuess = false;
        for (int i = 0; i < word.length; i++) {
            if (letter == word[i]) {
                visibleWord[i] = letter;
                correctGuess++;
                rightGuess = true;
            }
        }
        if (!rightGuess) {
            System.out.println("Ой! Такой буквы нет!");
            remainingAttempts--;
            showRemainingAttempts();
            drawHangman();
        }
        showWord(visibleWord);
        System.out.println();
        showUsedLetters();
    }

    private static char validateLetter() {
        String line = scanner.next();
        while (line.length() != 1) {
            System.out.println("Введите букву:");
            line = scanner.next();
        }
        char letter = line.charAt(0);
        if (letter >= 'А' && letter <= 'Я' || letter == 'Ё') {
            line = line.toLowerCase();
            return line.charAt(0);
        }
        if (letter >= 'а' && letter <= 'я' || letter == 'ё') {
            return letter;
        }
        System.out.println("Введите русскую букву:");
        return validateLetter();
    }

    private static void showRemainingAttempts() {
        String attempt = switch (remainingAttempts) {
            case 4, 3, 2 -> " попытки";
            case 1 -> " попытка";
            default -> " попыток";
        };
        System.out.println("У вас осталось " + remainingAttempts + attempt);
    }

    private static boolean addUsedLetter(char letter) {
        if (usedLetters.contains(letter)) {
                return false;
            }

        usedLetters.add(letter);
        return true;
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
        System.out.println();
        System.out.println("Ваше слово: ");
        for (int i = 0; i < visibleWord.length; i++) {
            System.out.print(visibleWord[i]);
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
            System.out.println("Вы проиграли!");
            System.out.println();
        } else {
            if (correctGuess == word.length) {
                System.out.println("Поздравляю, Вы выиграли!");
            }
        }
        usedLetters = new HashSet<>();
        remainingAttempts = 6;
        correctGuess = 0;
        showMainMenu();
    }
}