package tutorial.bottutorial;

import com.microsoft.graph.models.Attachment;
import com.microsoft.graph.models.FileAttachment;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MessageCollectionPage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author minhtd
 */
public class GraphOutlook {

    public static void connectGraph() {
        final Properties oAuthProperties = new Properties();
        try {
            FileInputStream inputStream = new FileInputStream("C:\\Users\\minhtd\\Documents\\BotT\\BotTutorial\\src\\main\\java\\tutorial\\bottutorial\\oAuth.properties");
            oAuthProperties.load(inputStream);
        } catch (IOException e) {
            System.out.println("Unable to read OAuth configuration. Make sure you have a properly formatted oAuth.properties file. See README for details.");
        }
        initializeGraph(oAuthProperties);
    }

    private static void initializeGraph(Properties properties) {
        try {
            Graph.initializeGraphForUserAuth(properties,
                    challenge -> System.out.println(challenge.getMessage()));
        } catch (Exception e) {
            System.out.println("Error initializing Graph for user auth");
            System.out.println(e.getMessage());
        }
    }

    private static void listInbox(String idFolder) {
        try {
            final MessageCollectionPage messages = Graph.getInbox(idFolder);

            // Output each message's details
            for (Message message : messages.getCurrentPage()) {
                if (message.subject.toLowerCase().contains("morning brief")) {
                    System.out.println("Message: " + message.subject);
                    System.out.println("  From: " + message.from.emailAddress.name);
                    System.out.println("  Status: " + (message.isRead ? "Read" : "Unread"));
                    System.out.println("  Received: " + message.receivedDateTime
                            // Values are returned in UTC, convert to local time zone
                            .atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime()
                            .format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)));
                }
            }

            final Boolean moreMessagesAvailable = messages.getNextPage() != null;
            System.out.println("\nMore messages available? " + moreMessagesAvailable);
        } catch (Exception e) {
            System.out.println("Error getting inbox");
            System.out.println(e.getMessage());
        }
    }

    public static String getMorningBrief() {
        try {
            MessageCollectionPage messages = Graph.getInbox(BotTutorial.idMorningBrief);
            for (Message message : messages.getCurrentPage()) {
                AttachmentCollectionPage attachments = Graph.getAttachment(message);
                for (Attachment attachment : attachments.getCurrentPage()) {
                    String filePath = Functions.downloadAttachmentToPath(attachment, "D:\\mb\\");
                    return filePath;
                }
                break;
            }
        } catch (Exception ex) {
            System.out.println("Error in sendMorningBrief function");
            Logger.getLogger(GraphOutlook.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("Error in sendMorningBrief function");
        return "";
    }
}
