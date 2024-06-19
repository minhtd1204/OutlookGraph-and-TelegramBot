package tutorial.bottutorial;

import com.azure.identity.DeviceCodeCredential;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.function.Consumer;

import com.azure.identity.DeviceCodeCredentialBuilder;
import com.azure.identity.DeviceCodeInfo;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.models.MailFolder;
import com.microsoft.graph.models.Message;
import com.microsoft.graph.requests.AttachmentCollectionPage;
import com.microsoft.graph.requests.GraphServiceClient;
import com.microsoft.graph.requests.MailFolderCollectionPage;
import com.microsoft.graph.requests.MessageCollectionPage;
import okhttp3.Request;

/**
 *
 * @author minhtd
 */
class Graph {

    private static Properties _properties;
    private static DeviceCodeCredential _deviceCodeCredential;
    private static GraphServiceClient<Request> _userClient;

    public static void initializeGraphForUserAuth(Properties properties, Consumer<DeviceCodeInfo> challenge) throws Exception {
        // Ensure properties isn't null
        if (properties == null) {
            throw new Exception("Properties cannot be null");
        }

        _properties = properties;

        final String clientId = properties.getProperty("app.clientId");
        final String tenantId = properties.getProperty("app.tenantId");
        final List<String> graphUserScopes = Arrays
                .asList(properties.getProperty("app.graphUserScopes").split(","));

        _deviceCodeCredential = new DeviceCodeCredentialBuilder()
                .clientId(clientId)
                .tenantId(tenantId)
                .challengeConsumer(challenge)
                .build();

        final TokenCredentialAuthProvider authProvider
                = new TokenCredentialAuthProvider(graphUserScopes, _deviceCodeCredential);

        _userClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
    }

    public static MessageCollectionPage getInbox(String idFolder) throws Exception {
        // Ensure client isn't null
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }

        return _userClient.me()
                .mailFolders(idFolder)
                .messages()
                .buildRequest()
                .select("from,isRead,receivedDateTime,subject")
                .top(50)
                .orderBy("receivedDateTime DESC")
                .get();
    }

    public static AttachmentCollectionPage getAttachment(Message message) throws Exception {
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }
        return _userClient.me().messages(message.id).attachments().buildRequest().get();
    }

    public static MailFolderCollectionPage getMailFolder() throws Exception {
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }
        return _userClient.me().mailFolders().buildRequest().get();
    }
    
    public static MailFolderCollectionPage getChildFolder(String mailFolderID) throws Exception {
        if (_userClient == null) {
            throw new Exception("Graph has not been initialized for user auth");
        }
        return _userClient.me().mailFolders(mailFolderID).childFolders().buildRequest().get();
    }

}
