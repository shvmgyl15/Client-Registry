package in.org.projecteka.clientregistry.repository;

import in.org.projecteka.clientregistry.model.UserCredentials;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Component
public class IdentityRepository extends in.org.projecteka.clientregistry.repository.PropertyReader {
    Map<String, String> clients = new HashMap<>();

    public IdentityRepository() {
        loadClients();
    }

    private void loadClients() {
        Properties properties;
        properties = loadProperties("clients.properties");
        for (String user : properties.stringPropertyNames()) {
            clients.put(user, properties.getProperty(user));
        }
    }

    public boolean checkClientIdAndAuthToken(UserCredentials userCredentials) {
        String clientId = userCredentials.getClientId();
        return clients.containsKey(clientId) && clients.get(clientId).equals(userCredentials.getAuthToken());
    }

}