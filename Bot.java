package tutorial.bottutorial;

import com.microsoft.graph.models.Attachment;
import java.io.File;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.CopyMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author minhtd
 */
public class Bot extends TelegramLongPollingBot {

    public static String filePath;

    @Override
    public String getBotUsername() {
        return "minducktranbot";
    }

    @Override
    public String getBotToken() {
        return "6601494406:AAHZhwrGST-kmpyF3ToGaarv_1wDFWsbcl4";
    }

    @Override
    public void onUpdateReceived(Update update) {
        var msg = update.getMessage();
//        var user = msg.getFrom();
//        String id = user.getId().toString();
        String idg = update.getMessage().getChatId().toString();

        if (msg.isCommand()) {
            switch (msg.getText()) {
                case "/help" ->
                    BotMenu.help(idg);
                case "/help@minducktranbot" ->
                    BotMenu.help(idg);
                case "/random" ->
                    sendMessage(idg, String.valueOf(BotMenu.getRandomNumber(1, 99)));
                case "/random@minducktranbot" ->
                    sendMessage(idg, String.valueOf(BotMenu.getRandomNumber(1, 99)));
                case "/dice" ->
                    sendMessage(idg, String.valueOf(BotMenu.getRandomNumber(1, 6)));
                case "/dice@minducktranbot" ->
                    sendMessage(idg, String.valueOf(BotMenu.getRandomNumber(1, 6)));
                case "/frandom" ->
                    BotMenu.fRandom(idg);
                case "/frandom@minducktranbot" ->
                    BotMenu.fRandom(idg);

                default ->
                    sendMessage(idg, "Unknow Command.");
            }
        } else if (msg.getText().startsWith("@ai")) {
            String messageText = update.getMessage().getText();

            String response = getResponseFromOpenAI(messageText);

            SendMessage message = new SendMessage();
            message.setChatId(String.valueOf(idg));
            message.setText(response);

            try {
                execute(message);
            } catch (TelegramApiException e) {
                System.out.println("Fail here.");
                e.printStackTrace();
            }
        }
//        if (msg.toString().contains("duy") || msg.toString().contains("Duy")) {
//            replyMessage(idg, msg.getMessageId(), "Ai nhắc đến duy ngu thế?");
//        }
//
//        if (msg.toString().contains("Khánh") || msg.toString().contains("khánh")) {
//            replyMessage(idg, msg.getMessageId(), "Thế rồi Khánh ngu đâu?");
//        }
//
//        if (msg.toString().contains("ngân") || msg.toString().contains("Ngân")) {
//            replyMessage(idg, msg.getMessageId(), "Suốt ngày Ngân");
//        }
    }

    public void sendMessage(String chatId, String message) { //gui tin nhan 
        SendMessage sm = SendMessage.builder()
                .chatId(chatId)
                .text(message).build();
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendSticker(String chatId, String stickerFileId) { //gui sticker
        InputFile inputFile = new InputFile(stickerFileId);
        SendSticker sendSticker = new SendSticker();
        sendSticker.setChatId(chatId);
        sendSticker.setSticker(inputFile);
        try {
            execute(sendSticker);
        } catch (TelegramApiException e) {
            sendMessage(chatId, "Error at sendSticker function.");
        }
    }

    /**
     * Send file with path @documentPath to @chatId with caption @caption
     *
     * @param chatId
     * @param documentPath
     * @param caption
     */
    public void sendFile(String chatId, String documentPath, String caption) {
        SendDocument sendDocument = new SendDocument();
        sendDocument.setChatId(chatId);
        sendDocument.setDocument(new InputFile(new File(documentPath)));
        sendDocument.setCaption(caption);

        try {
            execute(sendDocument);
        } catch (TelegramApiException e) {
            sendMessage(chatId, "Error at sendFile function.");
        }
    }

    public void replyMessage(String chatId, int repwho, String message) { //phan hoi tin nhan
        SendMessage sm = SendMessage.builder()
                .chatId(chatId)
                .text(message).build();
        sm.setReplyToMessageId(repwho);
        try {
            execute(sm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void copyMessage(String chatId, Integer messageID) {
        CopyMessage cm = CopyMessage.builder()
                .fromChatId(chatId)
                .chatId(chatId)
                .messageId(messageID)
                .build();
        try {
            execute(cm);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateMorningBrief() { //chạy update morning brief mỗi 9AM từ thứ 2 đến thứ 6.
        GraphOutlook graphOutlook = new GraphOutlook();
        graphOutlook.connectGraph();
        filePath = graphOutlook.getMorningBrief();

        Timer timer = new Timer();

        // Lấy thời gian hiện tại
        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        // Lấy giờ và phút hiện tại
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        // Tính thời gian cần đợi cho đến 9AM
        int delayHours = 9 - currentHour;
        int delayMinutes = 0 - currentMinute;

        // Nếu hiện tại là sau 9AM, tính thời gian cho ngày tiếp theo
        if (delayHours < 0 || (delayHours == 0 && delayMinutes < 0)) {
            delayHours += 24;
        }
        sendFile(BotTutorial.id5, filePath, "Bản tin Morning brief ngày " + LocalDate.now());
        // Lập lịch cho công việc lặp lại vào 9AM từ thứ 2 đến thứ 6
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String filePathNew = graphOutlook.getMorningBrief();
                while (filePathNew == filePath) {
                    try {
                        Thread.sleep(120 * 1000);
                        filePathNew = graphOutlook.getMorningBrief();
                    } catch (InterruptedException ex) {
                        System.out.println("Error at Thread.sleep(300 * 1000).");
                        Logger.getLogger(Bot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                filePath = graphOutlook.getMorningBrief();
                sendFile(BotTutorial.id5, filePath, "Bản tin Morning brief ngày " + LocalDate.now());
                Functions.deleteFile(filePath);
                System.out.println("Update Morning brief at " + LocalTime.now());
            }
        }, delayHours * 3600000 + delayMinutes * 60000, 24 * 3600000); // 24 giờ một lần lặp
    }

    private String getResponseFromOpenAI(String prompt) {
        String openAiUrl = "https://api.openai.com/v1/engines/davinci/completions";
        String apiKey = "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";

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(openAiUrl);
            request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey);
            request.setHeader(HttpHeaders.CONTENT_TYPE, "application/json");

            JsonObject body = new JsonObject();
            body.addProperty("prompt", prompt);
            body.addProperty("max_tokens", 50);

            request.setEntity(new StringEntity(body.toString()));

            String responseString = EntityUtils.toString(httpClient.execute(request).getEntity());

            // Log để kiểm tra phản hồi
            System.out.println("Phản hồi từ OpenAI: " + responseString);

            JsonObject jsonResponse = JsonParser.parseString(responseString).getAsJsonObject();

            if (jsonResponse.has("error")) {
                JsonObject error = jsonResponse.getAsJsonObject("error");
                String errorMessage = error.get("message").getAsString();
                return "Lỗi từ OpenAI: " + errorMessage;
            }

            // Kiểm tra nếu trường "choices" có tồn tại
            if (jsonResponse.getAsJsonArray("choices") != null) {
                return jsonResponse.getAsJsonArray("choices").get(0).getAsJsonObject().get("text").getAsString();
            } else {
                return "Không có phản hồi hợp lệ từ OpenAI.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Có lỗi xảy ra khi kết nối với OpenAI.";
        }
    }

}
