package banking;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BankingSystem bankingSystem = new BankingSystem();
        bankingSystem.printStartMenu();
    }
}

class BankingSystem {
    private final Scanner scanner = new Scanner(System.in);
    private final Map<Long, Integer> keyMap = new HashMap<>();

    void printStartMenu() {
        System.out.println("1. Create account\n" +
                "2. Log into account\n" +
                "0. Exit");

        switch (Integer.parseInt(scanner.nextLine())) {
            case 1:
                createAnAccount();
                break;
            case 2:
                logIntoAccount();
                break;
            case 0:
                exit();
                break;
            default:
                System.out.println("Unknown command");
                printStartMenu();
        }
    }

    void printAccountMenu() {
        System.out.println("\n1. Balance\n" +
                "2. Log out\n" +
                "0. Exit");

        switch (Integer.parseInt(scanner.nextLine())) {
            case 1:
                getBalance();
                break;
            case 2:
                logOutAccount();
                break;
            case 0:
                exit();
                break;
            default:
                System.out.println("Unknown command");
                printAccountMenu();
        }
    }

    private void createAnAccount() {

        CreditCard card = new CreditCard();

        System.out.printf("\nYour card have been created\n" +
                "Your card number:\n" +
                "%s\n" +
                "Your card PIN:\n" +
                "%d\n\n", card.getCardNumber(), card.getPIN());

        keyMap.put(card.getCardNumber(), card.getPIN());
        printStartMenu();
    }

    private void logIntoAccount() {
        System.out.println("\nEnter your card number:");
        long enterLogin = Long.parseLong(scanner.nextLine());
        System.out.println("Enter your PIN:");
        int enterPIN = Integer.parseInt(scanner.nextLine());

        if (keyMap.getOrDefault(enterLogin, -1) == enterPIN) {
            System.out.println("\nYou have successfully logged in!");
            printAccountMenu();
        } else {
            System.out.println("\nWrong card number or PIN!\n");
            printStartMenu();
        }
    }

    private void logOutAccount() {
        System.out.println("\nYou have successfully logged out!\n");
        printStartMenu();
    }

    private void getBalance() {
        System.out.println("\nBalance: 0");
        printAccountMenu();
    }

    private void exit() {
        System.out.println("\nBye!");
    }
}

class CreditCard {
    private final long cardNumber;
    private final int PIN;

    public CreditCard() {
        this.cardNumber = generateCardNumber();
        this.PIN = generatePIN();
    }

    public long getCardNumber() {
        return cardNumber;
    }

    public int getPIN() {
        return PIN;
    }

    private Long generateCardNumber() {
        String cardNumber = "400000" + generateRandomKey(9);
        return Long.parseLong(cardNumber + getControlNumber(cardNumber));
    }

    private int getControlNumber(String cardNumber) {

        int sum = 0;
        String[] arrayNumbers = cardNumber.split("");

        for (int i = 0; i < arrayNumbers.length; i++) {
            int digit = Integer.parseInt(arrayNumbers[i]);
            digit = i % 2 == 0 ? digit * 2 : digit;

            if (digit > 9)
                sum += digit - 9;
            else
                sum += digit;
        }

        int controlNumber = 10 - sum % 10;
        return controlNumber == 10 ? 0 : controlNumber;
    }

    private int generatePIN() {
        return Integer.parseInt(generateRandomKey(4));
    }

    private String generateRandomKey(int length) {
        StringBuilder randomKey = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomKey.append(((int) (Math.random() * 10)));
        }
        return randomKey.toString();
    }
}