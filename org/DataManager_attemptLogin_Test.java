import static org.junit.Assert.*;

import java.beans.Transient;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.junit.Test;
public class DataManager_attemptLogin_Test {
    /*
	 * This is a test class for the DataManager.createFund method.
	 */

     @Test 
     public void testSuccessfulLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"success\",\"data\":{\"_id\":\"667b79c7d381e21210a9b45d\",\"login\":\"work\",\"password\":\"hello\",\"name\":\"Tryingtowork\",\"description\":\"hellowork\",\"funds\":[{\"target\":3000,\"_id\":\"6685eacd7a73e40070130e8f\",\"name\":\"Workwork\",\"description\":\"fund for work\",\"org\":\"667b79c7d381e21210a9b45d\",\"donations\":[{\"_id\":\"6685eadf26daac031c5f4e1b\",\"contributor\":\"6668db089d6aab58f8cf8d8d\",\"fund\":\"6685eacd7a73e40070130e8f\",\"date\":\"2024-07-04T00:20:47.662Z\",\"amount\":500,\"__v\":0},{\"_id\":\"6685eae926daac031c5f4e28\",\"contributor\":\"668241d854e9b17250b495c4\",\"fund\":\"6685eacd7a73e40070130e8f\",\"date\":\"2024-07-04T00:20:57.781Z\",\"amount\":1000,\"__v\":0},{\"_id\":\"6685eaf426daac031c5f4e36\",\"contributor\":\"6668db089d6aab58f8cf8d8d\",\"fund\":\"6685eacd7a73e40070130e8f\",\"date\":\"2024-07-04T00:21:08.162Z\",\"amount\":1000,\"__v\":0}],\"__v\":0}],\"__v\":0}}";

			}
			
		});

        Organization org = dm.attemptLogin("snoopy","bestsnoopy");
        assertNotNull(org);
        assertEquals("66578ea0d659812ff80f7c2d", org.getId());
        assertEquals("Develop for Snoppy", org.getName());
        
        List<Fund> funds = org.getFunds();
        assertEquals(1, funds.size());
        
        Fund fund = funds.get(0);
        assertEquals("Workwork", fund.getName());
        assertEquals("fund for work", fund.getDescription());
        assertEquals("3000", fund.getTarget());
        
        List<Donation> donations = fund.getDonations();
        assertEquals(3, donations.size());
        
        Donation donation = donations.get(0);
        assertEquals("Lesly", donation.getContributorName());
        assertEquals(500, donation.getAmount());
        assertEquals("2024-07-04T00:20:47.662Z", donation.getDate());

     }
     
     @Test(expected = IllegalStateException.class)
     public void testNullResponse() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return null;

			}
			
		});
        Organization org = dm.attemptLogin("work", "hello");
     }
     
     @Test(expected = IllegalArgumentException.class)
     public void testNullLoginandPassword() {
    	 DataManager dm = new DataManager(new WebClient("localhost", 3001) {
 			
 			@Override
 			public String makeRequest(String resource, Map<String, Object> queryParams) {
 				String hello = "null";
 				return hello;

 			}
 			
 		});
    	 dm.attemptLogin(null, null);
     }
     
     @Test(expected = IllegalStateException.class)
     public void testFailedLogin() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "{\"status\":\"failure\"}";

			}
			
		});
        Organization org = dm.attemptLogin("snoopy", "123");
        
     }

     @Test(expected = IllegalStateException.class)
     public void testLoginthrowException() {
        DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				throw new RuntimeException("exception");

			}
			
		});
        Organization org = dm.attemptLogin("snoopy", "123");
     }

     @Test(expected = IllegalStateException.class)
	public void testInvalidJsonResponse() {
		DataManager dm = new DataManager(new WebClient("localhost", 3001) {
			@Override
			public String makeRequest(String resource, Map<String, Object> queryParams) {
				return "Invalid JSON response";
			}
		});
		
		Organization org = dm.attemptLogin("testLogin", "testPassword");
		
		assertNull(org);
	}
}
