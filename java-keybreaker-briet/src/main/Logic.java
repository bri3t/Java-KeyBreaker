package main;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author arnau
 */
public class Logic {

    Scanner sc = new Scanner(System.in);
    private static final String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static int[] indices = new int[5];

    private static final String BLUE = "\033[1;94m";    // BLUE
    private static final String GREEN = "\033[0;102m";    // BLUE
    private final String RESET = "\033[0m";

    private final byte[] IV_PARAM = {0x00, 0x01, 0x02, 0x03,
        0x04, 0x05, 0x06, 0x07,
        0x08, 0x09, 0x0A, 0x0B,
        0x0C, 0x0D, 0x0E, 0x0F};

    String passwordFile = "./passwords.txt";
    String outFile = null;

    public SecretKey keygenKeyGenerator(int keySize, String password) {
        SecretKey skey = null;
        //Control de la llargaria de la clau
        if ((keySize == 128)) {
            try {
                byte[] data = password.getBytes("UTF-8");
                MessageDigest md = MessageDigest.getInstance("SHA3-256");
                byte[] hash = md.digest(data);
                byte[] key = Arrays.copyOf(hash, keySize / 8);
                skey = new SecretKeySpec(key, "AES");
            } catch (NoSuchAlgorithmException ex) {
                System.err.println("Generador no disponible");
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Logic.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return skey; //Retornem la clau
    }

    public byte[] encryptOrDecrypt(SecretKey skey, byte[] data, boolean isEncript) {
        byte[] encryptedData = null;
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            IvParameterSpec iv = new IvParameterSpec(IV_PARAM);
            cipher.init(isEncript ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, skey, iv);

            // Procesar datos en bloques
            int blockSize = cipher.getBlockSize();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int offset = 0;
            while (offset < data.length) {
                byte[] chunk = Arrays.copyOfRange(data, offset, Math.min(offset + blockSize, data.length));
                byte[] processedChunk = cipher.update(chunk);
                outputStream.write(processedChunk);
                offset += blockSize;
            }

            // Finalizar la operación y obtener los datos procesados
            byte[] finalChunk = cipher.doFinal();
            outputStream.write(finalChunk);
            encryptedData = outputStream.toByteArray();
            outputStream.close();

            encryptedData = cipher.doFinal(data);

        } catch (Exception ex) {
        }
        return encryptedData;

    }

    public void encrypt() {
        byte[] encryptedData = encryptOrDecrypt(keygenKeyGenerator(128, "aazzz"), "Holaa".getBytes(), true);

        try ( FileOutputStream outputStream = new FileOutputStream(new File("./encriptado2.txt"))) {
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            bufferedWriter.write(new String(encryptedData));
            bufferedWriter.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void checkAndCreateFile(File file) {
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("Error al crear el archivo: " + e.getMessage());
                System.exit(0);
            }
        }
    }

    public void decrypt(HashMap<String, String> mapToDo) {
        if (mapToDo.containsKey("pwd")) {
            passwordFile = mapToDo.get("pwd");
        }

        if (mapToDo.containsKey("out")) {
            System.out.println("asdf");
            outFile = mapToDo.get("out");
        }

        File fileToDecrypt = new File(mapToDo.get("file"));

        // Verificar si el archivo existe, si no, crearlo
        File file = new File(passwordFile);
        checkAndCreateFile(file);
       

        byte[] dataEncripted = fileToByteArray(fileToDecrypt);

        byte[] data = null;
        try ( Scanner scanner = new Scanner(file)) {
            while (scanner.hasNext()) {
                String word = scanner.next();
                System.out.println(word);
                data = encryptOrDecrypt(keygenKeyGenerator(128, word), dataEncripted, false);
                if (data != null) {
                    String decText = new String(data);
                    if (outFile == null) {
                        System.out.println("********************************");
                        System.out.print("Ha encontrado en fichero: ");
                        System.out.println(decText);
                        System.out.println("********************************");
                    } else {
                        System.out.println(outFile);
                        File fileOut = new File(outFile);
                        checkAndCreateFile(fileOut);
                        appendText(decText, outFile);
                        System.out.println(GREEN + "Texto guardado en el fichero: " + outFile + RESET);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo: " + e.getMessage());
        }

        if (data == null) {
            generateStrings(dataEncripted);
        } else {

        }

    }

    public void replace() {

    }

    public boolean generateStrings(byte[] dataEncripted) {
        Arrays.fill(indices, 0);

        boolean continuar = true;
        while (continuar) {

            StringBuilder sb = new StringBuilder();

            // Construye la cadena actual basada en los índices
            for (int index : indices) {
                sb.append(chars.charAt(index));
            }
            String word = sb.toString();
            System.out.println(word);
            byte[] dataFinal = encryptOrDecrypt(keygenKeyGenerator(128, word), dataEncripted, false);
            if (dataFinal != null) {
                String decText = new String(dataFinal);
                if (isValidDecryptedText(decText)) {
                    System.out.println("********************************");
                    System.out.println("Mensaje encontrado.");
                    System.out.println(BLUE + decText + RESET);
                    System.out.println("********************************");

                    if (validateMessage(decText)) {
                        appendText(word, passwordFile);
                        System.out.println("Contraseña guardada en el fichero de contraseñas.");
                        continuar = false;
                        return false;
                    }
                }
            }

            // Incrementa el índice de la última posición
            for (int i = indices.length - 1; i >= 0; i--) {
                if (indices[i] < chars.length() - 1) {
                    indices[i]++;
                    break;
                } else {
                    indices[i] = 0; // Reinicia y lleva uno al siguiente índice si se alcanza el límite
                }
            }
        }
        return false;
    }

    private void appendText(String password, String file) {

        try ( FileWriter fw = new FileWriter(file, true)) {  // true para habilitar el modo de append
            fw.write(password + "\n");  // Añade el password seguido de un salto de línea
        } catch (IOException e) {
            System.err.println("Ocurrió un error al escribir en el archivo: " + e.getMessage());
        }
    }

    private boolean validateMessage(String text) {
        String promptMessage = String.format("El texto: \"%s\" ¿te parece lógico?\n"
                + "1. Sí\n"
                + "2. No\n"
                + "> ", text);
        boolean continuar = true;
        int option = 0;

        while (continuar) {
            System.out.print(promptMessage);
            if (sc.hasNextInt()) {
                option = sc.nextInt();
                switch (option) {
                    case 1 ->
                        continuar = false;
                    case 2 ->
                        continuar = false;
                    default -> {
                        System.out.println("Opción no válida. Por favor, elige 1 o 2.\n");
                        sc.nextLine();  // Limpiar el buffer del Scanner para prevenir bucles infinitos.
                    }
                }
            } else {
                System.out.println("Entrada no válida. Por favor, introduce un número (1-2).\n");
                sc.nextLine();  // Limpiar el buffer del Scanner para prevenir bucles infinitos.
            }
        }

        return option == 1;
    }

    public boolean isValidDecryptedText(String text) {
        String regex = "^[a-zA-Z0-9 .,!?@]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);

        return matcher.matches();
    }

    private byte[] fileToByteArray(File arxiu) {

        byte[] arxiuBytes = new byte[(int) arxiu.length()];

        try ( FileInputStream inputStream = new FileInputStream(arxiu)) {
            inputStream.read(arxiuBytes);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        return arxiuBytes;
    }

    public static void main(String[] args) {
//        generateStrings(1000);  // Ejemplo: Generar y mostrar 100 cadenas consecutivas
        Logic logic = new Logic();
    }

}
