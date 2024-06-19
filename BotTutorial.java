package tutorial.bottutorial;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 *
 * @author minhtd
 */
public class BotTutorial {

    public static String idMorningBrief = "AAMkADhlZTA5Y2IyLWRmZWEtNDNiNS05Y2QwLWQyNGJhOTVmYjM5NAAuAAAAAACE1tEopfhOQKpCHjAGwExGAQDbLKwlY7aRQ6ZjmSJVBSkpAAA-4imvAAA=";
    public static String idInbox = "AAMkADhlZTA5Y2IyLWRmZWEtNDNiNS05Y2QwLWQyNGJhOTVmYjM5NAAuAAAAAACE1tEopfhOQKpCHjAGwExGAQDbLKwlY7aRQ6ZjmSJVBSkpAAAAAAEMAAA";
    public static String idminh = "1989524025"; //@minducktran
    public static String id5 = "-1001796074306_3936"; //Morning brief - Thang trong hinh la cunt
    
    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        Bot bot = new Bot();
        botsApi.registerBot(bot);
        
        
//        String id1 = "-1001796074306_1"; //Tocs
//        String id2 = "-1001796074306_2"; //Di bac ne
//        String id3 = "-1001796074306_6"; //Du lich day do
//        String id4 = "-1001796074306_8"; //Duy Khanh
//        String idtest = "-1001796074306_1"; //group test

        bot.updateMorningBrief(); //call function auto update Morning Brief
    }
}
