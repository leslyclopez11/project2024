import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Test;

public class DataManager_UsernameAvailable_Test {
    @Test (expected = IllegalArgumentException.class)
    public void testNullUsername()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001));
        dm.UsernameAv(null);
    }

    @Test (expected = IllegalStateException.class)
    public void testNullResponse()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001){
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return null;
            }
        });

        dm.UsernameAv("work");
    }

    @Test
    public void testUsernameNotAvailable()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return "{\"status\":\"username already taken\"}";
            }

        });

        assertEquals(false, dm.UsernameAv("work"));
    }

    @Test
    public void testAvailableUserName()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return "{\"status\":\"username available\"}";
            }

        });

        assertEquals(true, dm.UsernameAv("work"));
    }

	@Test(expected = IllegalStateException.class)
    public void testErrorStatus()
    {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
            @Override
            public String makeRequest (String resource, Map<String, Object> queryParams){
                return "{\"status\":\"error\"}";
            }

        });
        dm.UsernameAv("work");
    }

	@Test(expected = IllegalStateException.class)
	public void testInvalidJsonResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "Invalid JSON response";
			}
		});

        dm.UsernameAv("work");
	}
}