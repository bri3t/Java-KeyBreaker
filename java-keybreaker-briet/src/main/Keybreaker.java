package main;

import java.util.Arrays;

public class Keybreaker {

    private static final String[] validActions = {"decrypt", "replace", "help"};
    private static final String[] validOptions = {"--out=", "--pwdfile="};
    private String[] args;
    private String action = "";
    private String inputFile = "";
    private String outputFile = null;
    private String passwordFile = null;

    public Keybreaker(String[] args) {
        this.args = args;
        if (!processArgs()) {
            printHelp();
        }
    }

    private boolean processArgs() {
        if (args.length < 2 || args.length > 4) {
            String text = args.length < 2 ? "Missing args" : "Too much args";
            System.out.println(text);
            return false;
        }
        action = args[0];
        inputFile = args[1];
        
        
        if (!checkActions(action)) {
            System.out.println("el action esta mal");
            return false;
        }
        if (!checkFile(inputFile)) {
            System.out.println("nombre fichero incorrecto");
            return false;
        }
        if (args.length >= 3) {
            checkOptions(args[2]);
        }

        if (args.length == 4) {
            if (!checkNotSameOptions()) return checkOptions(args[3]);
            System.out.println("Both options can't be the same");
            return false;
        }

        return true;
    }

    private boolean checkActions(String action) {
        return Arrays.asList(validActions).contains(action);
    }

    private boolean checkFile(String fileName) {
        return fileName.endsWith(".txt");
    }

    private boolean checkOptions(String arg) {
        if (checkOptionsLenght(arg)) {

            if (arg.startsWith("--out=") || arg.startsWith("--pwdfile=") && arg.endsWith(".txt")) {
                if (arg.startsWith("--out=")) {
                    outputFile = arg.substring(6);
                } else {
                    passwordFile = arg.substring(10);
                }
                return true;
            } else {
                System.out.println("Invalid option: " + arg);
                return false;
            }
        }

        return false;
    }

    private boolean checkOptionsLenght(String arg) {
        return arg.length() >= 10;
    }

    private boolean checkNotSameOptions() {
        return (args[2].substring(0, 6).equals(args[3].substring(0, 6)));
    }

    private void printHelp() {
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println("Usage: java Keybreaker <action> <inputFile> [--out=outputFile] [--pwdfile=passwordFile]");
        System.out.println("Actions:");
        for (String action : validActions) {
            System.out.println(" - " + action);
        }
        System.out.println("Options:");
        System.out.println(" --out=      Specify the output file");
        System.out.println(" --pwdfile=  Specify the password file");
        System.out.println("----------------------------------------------------------------------------------------------------------");

    }

    public static void main(String[] args) {
        new Keybreaker(args);
    }
}
