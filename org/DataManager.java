import java.text.ParseException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class DataManager {

	private final WebClient client;
	private final Map<String, String> contributorNameC;
	public DataManager(WebClient client) {
		if (client == null) {
			throw new IllegalArgumentException("WebClient can't be null");
		}
		this.client = client;
		this.contributorNameC = new HashMap<>();
	}

	/**
	 * Attempt to log the user into an Organization account using the login and password.
	 * This method uses the /findOrgByLoginAndPassword endpoint in the API
	 * @return an Organization object if successful; null if unsuccessful
	 */
	public Organization attemptLogin(String login, String password) {
		if (login == null || password == null) {
			throw new IllegalArgumentException("Login or password can't be null");
		}
		try {
			Map<String, Object> map = new HashMap<>();
			map.put("login", login);
			map.put("password", password);
			String response = client.makeRequest("/findOrgByLoginAndPassword", map);

			if (response == null) {
				throw new IllegalStateException("Response returned null");
			}
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");


			if (status.equals("success")) {
				JSONObject data = (JSONObject)json.get("data");
				String fundId = (String)data.get("_id");
				String name = (String)data.get("name");
				String description = (String)data.get("descrption");
				Organization org = new Organization(fundId, name, description);

				JSONArray funds = (JSONArray)data.get("funds");
				Iterator it = funds.iterator();
				while(it.hasNext()){
					JSONObject fund = (JSONObject) it.next(); 
					fundId = (String)fund.get("_id");
					name = (String)fund.get("name");
					description = (String)fund.get("description");
					long target = (Long)fund.get("target");

					Fund newFund = new Fund(fundId, name, description, target);

					JSONArray donations = (JSONArray)fund.get("donations");
					List<Donation> donationList = new LinkedList<>();
					Iterator it2 = donations.iterator();
					while(it2.hasNext()){
						JSONObject donation = (JSONObject) it2.next();
						String contributorId = (String)donation.get("contributor");
						String contributorName = this.getContributorName(contributorId);
						long amount = (Long)donation.get("amount");
						String date = (String)donation.get("date");
						donationList.add(new Donation(fundId, contributorName, amount, date));
					}

					newFund.setDonations(donationList);

					org.addFund(newFund);

				}

				return org;
			} else {
				throw new IllegalStateException("Login failed: " + json.get("error"));
			}
		} catch (ParseException e) {
			throw new IllegalStateException("Failed to parse JSON response", e);
		} catch (Exception e) {
			throw new IllegalStateException("Error in communicating with server", e);
		}
	}

	/**
	 * Look up the name of the contributor with the specified ID.
	 * This method uses the /findContributorNameById endpoint in the API.
	 * @return the name of the contributor on success; null if no contributor is found
	 */
	public String getContributorName(String id) {
		if (id == null) {
			throw new IllegalStateException("Contributor ID is null");
		}

		if (contributorNameC.containsKey(id)) {
			return contributorNameC.get(id);
		}
		try {

			Map<String, Object> map = new HashMap<>();
			map.put("_id", id);
			String response = client.makeRequest("/findContributrNameById", map);
			if (response == null) {
				throw new IllegalStateException("Response returned null");
			}
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				String name = (String)json.get("data");
				contributorNameC.put(id, name);
				return name;
			} else {
				return null;
			}

		} catch (ParseException e) {
			throw new IllegalStateException("Failed to parse JSON response", e);
		} catch (Exception e) {
			throw new IllegalStateException("Error in communicating with server", e);
		}
			
	}


	

	/**
	 * This method creates a new fund in the database using the /createFund endpoint in the API
	 * @return a new Fund object if successful; null if unsuccessful
	 */
	public Fund createFund(String orgId, String name, String description, long target) {
		if (orgId == null || name == null || description == null) {
			throw new IllegalArgumentException("Organization ID, name, and description are null");
		}
		try {

			Map<String, Object> map = new HashMap<>();
			map.put("orgId", orgId);
			map.put("name", name);
			map.put("description", description);
			map.put("target", target);
			String response = client.makeRequest("/createFund", map);
			if (response == null) {
				throw new IllegalStateException("Response returned null");
			}
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			String status = (String)json.get("status");

			if (status.equals("success")) {
				JSONObject fund = (JSONObject)json.get("data");
				String fundId = (String)fund.get("_id");
				return new Fund(fundId, name, description, target);
			} else {
				return null;
			} 

		} catch (ParseException e) {
			throw new IllegalStateException("Failed to parse JSON response", e);
		} catch (Exception e) {
			throw new IllegalStateException("Error in communicating with server", e);
		}	
	}


}
