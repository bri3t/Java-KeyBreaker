package main;

import java.util.Arrays;
import java.util.HashMap;

public class Keybreaker {

    private static final String[] validActions = {"decrypt", "replace", "help"};
    private static final String[] validOptions = {"--out=", "--pwdfile="};
    private String[] args;
    private String action = "";
    private String inputFile = "";
    private String outputFile = "";
    private String passwordFile = "";
    int toDo = -1;
    Logic logica = new Logic();
    HashMap<String, String> mapToDo = new HashMap<>();

    public Keybreaker(String[] args) {
        this.args = args;
        if (!processArgs()) {
            printHelp();
        } else {
            startFunction(toDo);
        }
    }

    private void startFunction(int num) {
        switch (num) {
            case 1: // decrypt
                logica.decrypt(mapToDo, false);
                break;
            case 2: // replace
                logica.replace(mapToDo);
                break;
            default:
                printHelp();
        }
    }

    private boolean processArgs() {

        if (args.length == 1 && args[0].equalsIgnoreCase("help")) {
            System.out.println("Help:");
            return false;
        }

        if (args.length < 2 || args.length > 4) {
            String text = args.length < 2 ? "Missing args" : "Too much args";
            System.out.println(text);
            return false;
        }
        action = args[0];
        inputFile = args[1];

        if (!checkActions(action)) {
            return false;
        }
        if (!checkFile(inputFile)) {
            return false;
        }

        if (args.length >= 3) {
            if (!checkOptions(args[2])) {
                return false;
            }
        }

        if (args.length == 4) {
            if (!checkNotSameOptions()) {
                if (!checkOptions(args[3])) {
                    return false;
                }
            } else {
                System.out.println("Las dos opciones no pueden ser iguales: " + args[2] + " - " + args[3]);
                return false;
            }
        }

        mapToDo.put("out", outputFile);
        mapToDo.put("pwd", passwordFile);
        mapToDo.put("file", inputFile);

        return true;
    }

    private boolean checkActions(String action) {
        // Check if the action is "help" and return false if it is
        if (action.equalsIgnoreCase("help")) {
            System.out.println("Invalid syntax usage");
            return false;
        }
        // Continue to check if the action is contained in the validActions array
        boolean result = Arrays.asList(validActions).contains(action);

        if (!result) {
            System.out.println("El action esta mal");
        } else {
            toDo = action.equals("decrypt") ? 1 : (action.equals("replace") ? 2 : 0);
        }

        return result;
    }

    private boolean checkFile(String fileName) {
        boolean result = fileName.endsWith(".txt");
        if (!result) {
            System.out.println("nombre fichero incorrecto");
        }
        return result;
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
            }
        }
        System.out.println("Invalid option: " + arg);

        return false;
    }

    private boolean checkOptionsLenght(String arg) {
        return arg.length() >= 10;
    }

    private boolean checkNotSameOptions() {
        if (args[2].length() < 6 || args[3].length() < 6) {
            return false;
        }

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
