package in.org.projecteka.clientregistry.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.org.projecteka.clientregistry.model.Provider;
import in.org.projecteka.clientregistry.model.Resource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.*;

public class ResourceRepository {

    private ApplicationContext context;

    private HashMap<String, String> providersMap = new HashMap<>();

    private Map<String, ArrayList<ResourceUpdate>> resourceFeeds = new HashMap<>();
        private Map<String, HashMap<String, String>> resourceMaps = new HashMap<String, HashMap<String, String>>() {{
        put("provider",  providersMap);
    }};

    private Map<String, String> resourceUpdateLookup = new HashMap<String, String>() {{
        put("provider",  "classpath:providers_list.txt");
    }};

    private Map<String, String> resourceLocations = new HashMap<>() ;

    public ResourceRepository(ApplicationContext context) {
        this.context = context;
        resourceLocations.put("provider", String.format("classpath:providers/%s/", getActiveProfile(context)));
    }

    private String getActiveProfile(ApplicationContext context) {
        String[] profiles = context.getEnvironment().getActiveProfiles();
        final String DEFAULT = "local";
        return profiles.length > 0 && !profiles[0].equals("${profile}") ? profiles[0] : DEFAULT;
    }

    private String locateResourceInPath(String id, String path) {
        String value = null;
        org.springframework.core.io.Resource resource = context.getResource(path + id.trim());
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()), 1024);
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append('\n');
            }
            reader.close();
            value = builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public List<Resource> findResources(String type, String updatedSince, int offset, int limit, String name) throws ParseException, JsonProcessingException {
        String location = resourceUpdateLookup.get(type);
        if (StringUtils.isBlank(location)) return new ArrayList<>();

        ArrayList<ResourceUpdate> locationUpdatesForType = resourceFeeds.computeIfAbsent(type, k -> new ArrayList<>());

        if (locationUpdatesForType.isEmpty()) {
            loadProviderUpdates(location, locationUpdatesForType);
        }

        ArrayList<Resource> results = new ArrayList<>();
        int totalEvents = locationUpdatesForType.size();

        if (offset >= totalEvents) {
            return results;
        }

        int range = offset + limit;
        if (range >= totalEvents) {
            range = totalEvents;
        }

        List<ResourceUpdate> resourceUpdates = locationUpdatesForType.subList(offset, range);
        for (ResourceUpdate resourceUpdate : resourceUpdates) {
            Resource resource = findResource(type, resourceUpdate.identifier + ".json");
            if (resource != null) {
                if (name == null) {
                    results.add(resource);
                } else {
                    String value = resource.getValue();
                    ObjectMapper mapper = new ObjectMapper();
                    Provider provider = mapper.readValue(value, Provider.class);
                    if (provider.getName().toLowerCase().contains(name.toLowerCase())){
                        results.add(resource);
                    }
                }
            }
        }
        return results;
    }

    public Resource findResource(String type, String identifier) {
        HashMap<String, String> resources = resourceMaps.get(type);
        String resourceLocation = resourceLocations.get(type);
        if ((resources == null) || (resourceLocation == null)) return null;

        String value = resources.get(identifier);
        if (value != null) {
            return new Resource(value);
        }
        String content = locateResourceInPath(identifier, resourceLocation);
        if (content != null) {
            resources.put(identifier, content);
            return new Resource(content);
        }
        return null;
    }

    private static class ResourceUpdate {
        private String identifier;

        public ResourceUpdate(String identifier) {
            this.identifier = identifier;
        }
    }

    private void loadProviderUpdates(String location, ArrayList<ResourceUpdate> resourceUpdates) {
        org.springframework.core.io.Resource resource = context.getResource(location);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()), 1024);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                ResourceUpdate resourceUpdate = new ResourceUpdate(parts[1].trim());
                resourceUpdates.add(resourceUpdate);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
