package org.learning;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Main {
    static List<String> dictionary;
    static Scanner scanner = new Scanner(System.in);
    static Random rndm = new Random();
    static char[] usedLetters = new char[33];
    static int indexUsedLetters = 0;
    static int incorrectGuesse = 6;
    static int correctGuesse = 0;

    public static void main(String[] args) {
        if (readDictionary()) return;
        startGame();
    }

    private static boolean readDictionary() {
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

    private static void startGame() {
        System.out.println("Нажмите 'С' для начала игры или 'В' для выхода из игры.");
        String line = scanner.next();
        while (!(line.equals("С") || line.equals("В"))) {
            System.out.println("Нажмите 'С' для начала игры или 'В' для выхода из игры.");
            line = scanner.next();
        }
        if (line.equals("С")) {
            midGame();
        } else {
            System.exit(0);
        }
    }

    private static void midGame() {
        int wordIndex = rndm.nextInt(dictionary.size());
        char[] word = dictionary.get(wordIndex).toCharArray();
        int mask = word.length;
        char[] visibleWord = new char[mask];
        System.out.println("only i see it: " + Arrays.toString(word));

        Arrays.fill(visibleWord, ('*'));
        showWord(visibleWord);
        while (!(incorrectGuesse == 0 || correctGuesse == word.length)) {
            repeat(visibleWord, mask, word);
        }
        endGame(word);
    }

    private static void repeat(char[] visibleWord, int mask, char[] word) {
        System.out.println();
        System.out.println("Введите букву:");
        char letter = validateLetter();
        if (!addUsedLetter(letter)) {
            System.out.println("Вы уже вводили такую букву!");
            showUsedLetters();
            return;
        }
        boolean rightGuess = false;
        for (int i = 0; i < mask; i++) {
            if (letter == word[i]) {
                visibleWord[i] = letter;
                correctGuesse++;
                rightGuess = true;
            }
        }
        if (!rightGuess) {
            System.out.println("Ой! Такой буквы нет!");
            incorrectGuesse--;
            showIncorrectGuesses();
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

    private static void showIncorrectGuesses() {
        String attempt = switch (incorrectGuesse) {
            case 4, 3, 2 -> " попытки";
            case 1 -> " попытка";
            default -> " попыток";
        };
        System.out.println("У вас осталось " + incorrectGuesse + attempt);
    }

    private static boolean addUsedLetter(char letter) {
        for (int i = 0; i < usedLetters.length; i++) {
            if (usedLetters[i] == letter) {
                return false;
            }
        }
        usedLetters[indexUsedLetters++] = letter;
        return true;
    }

    private static void drawHangman() {
        switch (incorrectGuesse) {
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
        System.out.println();
        System.out.println("Использованы буквы: ");
        for (int i = 0; i < indexUsedLetters; i++) {
            System.out.print(usedLetters[i] + " ");
        }
    }

    private static void endGame(char[] word) {
        if (incorrectGuesse == 0) {
            System.out.println("Вы проиграли!");
            System.out.println();
        } else {
            if (correctGuesse == word.length) {
                System.out.println("Поздравляю, Вы выиграли!");
            }
        }
        usedLetters = new char[33];
        indexUsedLetters = 0;
        incorrectGuesse = 6;
        correctGuesse = 0;
        startGame();
    }
}