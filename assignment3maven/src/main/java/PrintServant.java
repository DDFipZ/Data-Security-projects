import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PrintServant extends UnicastRemoteObject implements PrintService {

	static File userList = new File("userList.txt");
	boolean access;
	private String usertype;

	public PrintServant() throws RemoteException {
		super();
	}

	public static void main(String[] args) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {

		CreateFile();
		Clearfile();
		AddUser("Alice","a1234", "printer_manager");
		AddUser("Bob","b1234", "service_technician");
		AddUser("Charlie","c1234", "ordinary_user");
		AddUser("marcus", "somepass", "printer_manager");


	}

	@Override
	public String print(String filename, String printer) throws RemoteException {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("print")) {
			access = false;
			return "print: " + filename + " + " + printer;
		}else
			return "Denied";
	}

	@Override
	public String queue(String printer) {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("queue")) {
			access = false;
			return "queue: " + printer;
		}else
			return "Denied";
	}

	@Override
	public String topQueue(String printer, int job) {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("topQueue")) {
			access = false;
			return "topQueue: " + printer + " + " + job;
		}else
			return "Denied";
	}

	@Override
	public String start() {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("start")) {
			access = false;
			return "start";
		}else
			return "Denied";
	}

	@Override
	public String stop() {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("stop")) {
			access = false;
			return "stop";
		}else
			return "Denied";
	}

	@Override
	public String restart() {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("restart")) {
			access = false;
			return "restart";
		}else
			return "Denied";
	}

	@Override
	public String status(String printer) {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("status")) {
			access = false;
			return "status: " + printer;
		}else
			return "Denied";
	}

	@Override
	public String readConfig(String parameter) {
		// TODO Auto-generated method stub
		if(access == true && isAuthorized("readConfig")) {
			access = false;
			return "readConfig: " + parameter;
		}else
			return "Denied";
	}

	@Override
	public String setConfig(String parameter, String value) {
		// TODO Auto-generated method stub
			if(access == true && isAuthorized("setConfig")) {
				access = false;
				return "setConfig: " + parameter + " + " + value;
			}else
				return "Denied";
	}
	
	@Override
    public boolean startUp(String username, String password) throws RemoteException {
        
		try {
			access = CheckUser(username, password);
			if(access == true) {
				setUserType(username);
				return true;
			}else {
				return false;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
        
    }
	
	// Create file
	private static void CreateFile() throws IOException {

		userList.createNewFile();

	}
	// Add user
	private static void AddUser(String username, String password, String usertype) throws IOException, NoSuchAlgorithmException, ClassNotFoundException {

		BufferedWriter fileout = new BufferedWriter(new FileWriter(userList, true));
		if (userList.length() != 0) {
			fileout.newLine();
		}
		fileout.append(username);
		fileout.newLine();
		fileout.append(Hashing(password));
		fileout.close();

		// Adds users to the user role, using their username as the key
		JSONParser jsonParser = new JSONParser();
		try {
			Object o = jsonParser.parse(new FileReader("user_roles.json"));
			JSONObject jsonObject = (JSONObject) o;
			if(!jsonObject.containsKey("username")){
				jsonObject.put(username, usertype);
				FileWriter fileWriter = new FileWriter("user_roles.json");
				fileWriter.write(jsonObject.toString());
				fileWriter.flush();
				fileWriter.close();
			} else {
				System.out.println(username + " already exists");
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

	}
	// Clear file
	public static void Clearfile() throws IOException {
		BufferedWriter fileout = new BufferedWriter(new FileWriter(userList));
		fileout.flush();
		fileout.close();

	}
	// Check user
	private static boolean CheckUser(String username, String password) throws IOException, NoSuchAlgorithmException {

		BufferedReader br = new BufferedReader(new FileReader(userList));
		String line;
		boolean present = false;
		while ((line = br.readLine()) != null) {
			if (line.equals(username)) {
				line = br.readLine();
				if (Hashing(password).equals(line)) {
					present = true;
				}
			}
		}
		return present;
	}
	
	// Hashing
	public static String Hashing(String password) throws NoSuchAlgorithmException {

		String hashedPassword = "";

		MessageDigest hashfunc = MessageDigest.getInstance("SHA-512");
		byte[] hashedPass = hashfunc.digest(password.getBytes(StandardCharsets.UTF_8));
		hashedPassword = bytesToHex(hashedPass);

		return hashedPassword;
	}
	// Bytes to hex
	private static String bytesToHex(byte[] bytes) {

		StringBuilder hexString = new StringBuilder();
		for (byte b : bytes) {
			hexString.append(String.format("%02X ", b));
		}
		return hexString.toString();
	}

	private void setUserType(String username){
		JSONObject jsonObject = getJSONFile("user_roles.json");
		if(jsonObject.containsKey(username)){
			this.usertype = (String) jsonObject.get(username);
			System.out.println(this.usertype);
		}
	}

	private static JSONObject getJSONFile(String jsonfile){
		JSONParser jsonParser = new JSONParser();
		try {
			Object o = jsonParser.parse(new FileReader(jsonfile));
			JSONObject jsonObject = (JSONObject) o;
			return jsonObject;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	private boolean isAuthorized(String method){
			JSONObject aclmethods = getJSONFile("ACL_methods.json");
			if(aclmethods.containsKey(method)){
				JSONArray array = (JSONArray) aclmethods.get(method);

				for (int i = 0; i < array.size(); i++) {
					String utype = (String) array.get(i);
					if (utype.equals(this.usertype)){
						return true;
					}
				}
			}
			return false;
	}
}

