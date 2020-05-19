package banking;

import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        BankingSystem bankingSystem = new BankingSystem(getDataBasePatch(args));
        bankingSystem.printStartMenu();
    }

    static String getDataBasePatch(String[] args) {
        String patch = "";
        for (int i = 0; i < args.length; i++) {
            if ("-fileName".equals(args[i])) {
                patch = args[i + 1];
            }
        }
        return patch;
    }
}

class BankingSystem {

    private final Scanner scanner = new Scanner(System.in);
    private final BankingDB bankingDB;
    private CreditCard creditCard;

    public BankingSystem(String dataBasePatch) {
        this.bankingDB = new BankingDB(dataBasePatch);
    }

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
                "2. Add income\n" +
                "3. Do transfer\n" +
                "4. Close account\n" +
                "5. Log out\n" +
                "0. Exit");

        switch (Integer.parseInt(scanner.nextLine())) {
            case 1:
                getBalance();
                break;
            case 2:
                addIncome();
                break;
            case 3:
                doTransfer();
                break;
            case 4:
                closeAccount();
                break;
            case 5:
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

        this.creditCard = new CreditCard();

        System.out.printf("\nYour card have been created\n" +
                "Your card number:\n" +
                "%s\n" +
                "Your card PIN:\n" +
                "%s\n\n", creditCard.getCardNumber(), creditCard.getPIN());

        bankingDB.createCreditCard(creditCard.getCardNumber(), creditCard.getPIN());
        printStartMenu();
    }

    private void logIntoAccount() {
        System.out.println("\nEnter your card number:");
        String enterLogin = scanner.nextLine();
        System.out.println("Enter your PIN:");
        String enterPIN = scanner.nextLine();

        creditCard = bankingDB.getCard(enterLogin);
        if (creditCard.getPIN().equals(enterPIN)) {
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
        System.out.printf("\nBalance: %d\n", creditCard.getBalance());
        printAccountMenu();
    }

    private void addIncome() {

        System.out.println("\nHow much money you want to add?");
        int income = Integer.parseInt(scanner.nextLine());

        bankingDB.addIncome(creditCard.getCardNumber(), creditCard.getBalance(), income);
        creditCard.setBalance(creditCard.getBalance() + income);
        System.out.println("\nSuccessful!");
        printAccountMenu();
    }

    private void doTransfer() {

    }

    private void closeAccount() {
        bankingDB.deleteCreditCard(creditCard.getCardNumber());
        printStartMenu();
    }

    private void exit() {
        bankingDB.closeConnection();
        System.out.println("\nBye!");
    }
}

class CreditCard {

    private final String cardNumber;
    private final String PIN;
    private int balance;

    public CreditCard() {
        this.cardNumber = generateCardNumber();
        this.PIN = generatePIN();
        this.balance = 0;
    }

    public CreditCard(String cardNumber, String PIN, int balance) {
        this.cardNumber = cardNumber;
        this.PIN = PIN;
        this.balance = balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getPIN() {
        return PIN;
    }

    public int getBalance() {
        return balance;
    }

    private String generateCardNumber() {
        String cardNumber = "400000" + generateRandomKey(9);
        return cardNumber + getControlNumber(cardNumber);
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

    private String generatePIN() {
        return generateRandomKey(4);
    }

    private String generateRandomKey(int length) {
        StringBuilder randomKey = new StringBuilder();
        for (int i = 0; i < length; i++) {
            randomKey.append(((int) (Math.random() * 10)));
        }
        return randomKey.toString();
    }
}

class BankingDB {

    private Connection connection;
    private String query;

    BankingDB(String dataBasePatch) {
        this.connection = connection("jdbc:sqlite:./" + dataBasePatch);
        createCardTable();
    }

    private Connection connection(String url) {
        try {
            connection = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return connection;
    }

    public void closeConnection() {
        try {
            connection.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void createCardTable () {
        query = "CREATE TABLE card (\n" +
                "\tid INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                "\tnumber TEXT NOT NULL,\n" +
                "\tpin TEXT NOT NULL,\n" +
                "\tbalance INTEGER DEFAULT 0\n" +
                ");";
        try {
            Statement statement = connection.createStatement();
            statement.execute(query);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void createCreditCard(String cardNumber, String PIN) {
        query = "INSERT INTO card (number, pin, balance) VALUES (?, ?, ?);";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.setString(2, PIN);
            preparedStatement.setInt(3, 0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    void deleteCreditCard(String cardNumber) {

        query = "DELETE FROM card WHERE number = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    CreditCard getCard(String cardNumber) {

        CreditCard creditCard = null;

        query = "SELECT number, pin, balance FROM card WHERE number = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, cardNumber);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                creditCard = new CreditCard(resultSet.getString("number"), resultSet.getString("pin"), resultSet.getInt("balance"));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return creditCard;
    }

    void addIncome(String cardNumber, int balance, int income) {
        query = "UPDATE card SET balance = ? WHERE number = ?;";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, income + balance);
            preparedStatement.setString(2, cardNumber);
            preparedStatement.execute();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}