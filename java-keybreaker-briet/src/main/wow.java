/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package main;

/**
 *
 * @author arnau
 */
public class wow {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String a = "\033[0;104m";
        String b = "\033[1;94m";
        String c = "\033[44m";
        String d = "\033[1;94m";
        String e = "\033[0;104m";
        String RESET = "\033[0m";

        String[] words = {a, b, c, d, e};

        for (String word : words) {
            System.out.println(word + "Texto" + RESET);

        }

    }

}
