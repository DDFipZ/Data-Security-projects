import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Scanner;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Client {

	public static void main(String[] args) throws NotBoundException, MalformedURLException, RemoteException,
			IOException, ClassNotFoundException, NoSuchAlgorithmException {
		

		Scanner theScanner = new Scanner(System.in);
		System.out.println("Enter Username: ");
		String username = theScanner.nextLine();
		System.out.println("Enter Password: ");
		String password = theScanner.nextLine();
	
		
		PrintService service = (PrintService) Naming.lookup("rmi://localhost:5099/hello");
		

		System.out.println(service.startUp(username, password));
		if (service.startUp(username, password)) {
			System.out.println("Type a number to select service: ");
			System.out.println("1. Show filename & specified printer");
			System.out.println("2. Show queue for a given printer");
			System.out.println("3. Move job to the top of the queue");
			System.out.println("4. Start the print server");
			System.out.println("5. Stop the print server");
			System.out.println("6. Restart the print server");
			System.out.println("7. Show status of printer");
			System.out.println("8. Show value of parameter");
			System.out.println("9. Set the parameter to a given value");
			String selectService = theScanner.nextLine();
			
			if (selectService.equals("1")) {
				System.out.println(service.print("file", "print"));
			} else if (selectService.equals("2")) {
				System.out.println(service.queue("printer"));
			} else if (selectService.equals("3")) {
				System.out.println(service.topQueue("printer", 1));
			} else if (selectService.equals("4")) {
				System.out.println(service.start());
			} else if (selectService.equals("5")) {
				System.out.println(service.stop());
			} else if (selectService.equals("6")) {
				System.out.println(service.restart());
			} else if (selectService.equals("7")) {
				System.out.println(service.status("printer"));
			} else if (selectService.equals("8")) {
				System.out.println(service.readConfig("parameter"));
			} else if (selectService.equals("9")) {
				System.out.println(service.setConfig("parameter", "value"));
			} else
				System.out.println("Not an option");
		}
		theScanner.close();
	}
}
