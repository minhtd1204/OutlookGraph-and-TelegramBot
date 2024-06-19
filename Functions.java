package tutorial.bottutorial;

import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.FileAttachment;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author minhtd
 */
public class Functions {

    public static String getRandomLineFromFile(String filePath) throws IOException {
        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        if (lines.isEmpty()) {
            return null;
        }
        Random random = new Random();
        int randomIndex = random.nextInt(lines.size());
        return lines.get(randomIndex);
    }

    public static boolean isFirstCharacterUppercase(String input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        char firstChar = input.charAt(0);
        return Character.isUpperCase(firstChar);
    }

    public static int calculateInitialDelay() {
        // Tính toán thời gian bắt đầu từ hiện tại đến 9 giờ sáng.
        java.time.LocalTime now = java.time.LocalTime.now();
        java.time.LocalTime targetTime = java.time.LocalTime.of(9, 0);
        int initialDelay = (int) java.time.Duration.between(now, targetTime).getSeconds();
        if (initialDelay < 0) {
            // Nếu đã qua 9 giờ sáng, thì tính lại cho lần mai.
            initialDelay += 24 * 60 * 60; // 1 ngày
        }
        return initialDelay;
    }

    public static String downloadAttachmentToPath(Attachment attachment, String filePath) {
        File fileDown = new File(filePath + attachment.name);
        try {
            Files.write(fileDown.toPath(), ((FileAttachment) attachment).contentBytes);
            return (fileDown.toPath().toString());
        } catch (IOException ex) {
            System.out.println("Error at downloadAttachmentToPath function.");
            Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Error at downloadAttachmentToPath function.");
        return "";
    }

    public static void deleteFile(String pathToFile) {
        File file = new File(pathToFile);

        // Kiểm tra xem file có tồn tại không
        if (file.exists()) {
            // Xóa file và kiểm tra kết quả
            if (file.delete()) {
                System.out.println("File deleted successfully: " + pathToFile);
            } else {
                System.out.println("Failed to delete the file");
            }
        } else {
            System.out.println("File does not exist: " + pathToFile);
        }
    }
}
