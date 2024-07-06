import static org.junit.Assert.*;

import java.util.Collections;
import java.util.Map;

import org.junit.Test;

public class DataManager_createOrganization_Test {

    @Test(expected = IllegalArgumentException.class)
    public void testLoginNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization(null, "a", "b", "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testPasswordNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("a", null, "b", "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNameNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("b", "a", null, "c");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testDescriptionNull() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.createOrganization("d", "a", "b", null);
    }

    @Test(expected = IllegalStateException.class)
    public void testNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return null;
            }
        });

        dm.createOrganization("work", "hello", "Tryingtowork", "hellowork");
    }

    @Test(expected = IllegalStateException.class)
    public void testErrorStatus() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\":\"error\"}";
            }

        });
        dm.createOrganization("work", "hello", "Tryingtowork", "hellowork");
    }

    @Test(expected = IllegalStateException.class)
    public void testInvalidJsonResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "Invalid JSON response";
            }
        });

        dm.createOrganization("work", "hello", "Tryingtowork", "hellowork");
    }

    @Test
    public void testSuccessfulCreation() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001){
            @Override
            public String makeRequest(String resource, Map<String, Object> queryParams) {
                return "{\"status\": \"success\", \"data\": {\"_id\": \"667b79c7d381e21210a9b45d\", \"login\": \"work\", \"password\": \"hello\", \"name\": \"Tryingtowork\", \"description\": \"hellowork\", \"funds\": [], \"__v\": 0}}";
            }
        });

        Organization newOrg = dm.createOrganization("work", "hello", "Tryingtowork", "hellowork");
        assertEquals("667b79c7d381e21210a9b45d", newOrg.getId());
        assertEquals("Tryingtowork", newOrg.getName());
        assertEquals("hellowork", newOrg.getDescription());

    }

}

