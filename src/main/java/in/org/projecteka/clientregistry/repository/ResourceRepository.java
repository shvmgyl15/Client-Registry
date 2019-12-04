package in.org.projecteka.clientregistry.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.org.projecteka.clientregistry.model.Provider;
import org.apache.commons.lang3.StringUtils;
import in.org.projecteka.clientregistry.model.Resource;
import in.org.projecteka.clientregistry.utils.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Repository
public class ResourceRepository {

    @Autowired
    private ApplicationContext context;

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private HashMap<String, String> providersMap = new HashMap<>();

    private Map<String, ArrayList<ResourceUpdate>> resourceFeeds = new HashMap<>();
        private Map<String, HashMap<String, String>> resourceMaps = new HashMap<String, HashMap<String, String>>() {{
        put("facility",  providersMap);
    }};


    private Map<String, String> resourceUpdateLookup = new HashMap<String, String>() {{
        put("facility",  "classpath:providers_list.txt");
    }};

    private Map<String, String> resourceLocations = new HashMap<String, String>() {{
        put("facility",  "classpath:providers/");
    }};

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

        Date sinceDate = null;
        if (!StringUtils.isBlank(updatedSince)) {
            sinceDate = DateUtil.parseDate(updatedSince);
        }

        ArrayList<ResourceUpdate> locationUpdatesForType = resourceFeeds.get(type);
        if (locationUpdatesForType == null) {
            locationUpdatesForType = new ArrayList<ResourceUpdate>();
            resourceFeeds.put(type, locationUpdatesForType);
        }


        if (locationUpdatesForType.isEmpty()) {
            loadFacilityUpdates(location, locationUpdatesForType);
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
                    if (provider.getName().contains(name)){
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
        private Date updateDate;
        private String identifier;

        public ResourceUpdate(Date updateDate, String identifier) {
            this.updateDate = updateDate;
            this.identifier = identifier;
        }
    }

    private void loadFacilityUpdates(String location, ArrayList<ResourceUpdate> resourceUpdates) {
        org.springframework.core.io.Resource resource = context.getResource(location);
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream()), 1024);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.trim().split(",");
                ResourceUpdate resourceUpdate = new ResourceUpdate(dateFormat.parse(parts[0].trim()), parts[1].trim());
                resourceUpdates.add(resourceUpdate);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
