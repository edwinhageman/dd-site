package emh.dd_site;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;

@Configuration
public class GoogleSheetsConfig {

    private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS_READONLY);
    private static final String APPLICATION_NAME = "dd_site";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();

    @Bean
    public Sheets sheets(@Value("${google.credentials.path}") String credentialsPath) throws IOException, GeneralSecurityException {
        if (credentialsPath == null || credentialsPath.isEmpty()) {
            throw new IllegalArgumentException("Missing GCP_CREDENTIALS_PATH environment variable");
        }
        try (InputStream is = new FileInputStream(credentialsPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(is).createScoped(SCOPES);
            HttpCredentialsAdapter credentialsAdapter = new HttpCredentialsAdapter(credentials);

            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    JSON_FACTORY,
                    credentialsAdapter
            )
                    .setApplicationName(APPLICATION_NAME)
                    .build();
        }
    }
}