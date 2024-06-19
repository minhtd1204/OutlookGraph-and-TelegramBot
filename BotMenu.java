/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tutorial.bottutorial;

import java.io.IOException;
import java.util.Random;
import static tutorial.bottutorial.Functions.getRandomLineFromFile;

/**
 *
 * @author minhtd
 */
public class BotMenu {

    public static void help(String who) {
        Bot bot = new Bot();
        String menuHelp
                = "/help - Help menu"
                + "\n/random - Random from 1-99"
                + "\n/dice - Roll a dice"
                + "\n/frandom - Chửi một thằng ngu";
        bot.sendMessage(who, menuHelp);
    }

    public static int getRandomNumber(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("Giá trị min phải nhỏ hơn max");
        }
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static void fRandom(String who) {
        Bot bot = new Bot();
        String f = "";
        String filePathName = "C:\\Users\\minhtd\\Documents\\BotT\\BotTutorial\\src\\main\\java\\tutorial\\bottutorial\\name.txt";
        String filePath = "C:\\Users\\minhtd\\Documents\\BotT\\BotTutorial\\src\\main\\java\\tutorial\\bottutorial\\f.txt";
        try {
            f = getRandomLineFromFile(filePathName);
            if (getRandomLineFromFile(filePathName) != null) {
                String randomLine = getRandomLineFromFile(filePath);
                if (randomLine != null) {
                    f = randomLine.replace("...", f);
                } else {
                    f = "Dạy vài câu chửi bọn ngu đi.";
                }
            } else {
                f = "Cho vài cái tên để chửi đi.";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bot.sendMessage(who, f);
    }
}
