import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Put a short phrase describing the program here.
 *
 * @author Jared Israel
 *
 */
public class ProgramWithIO {
    /**
     * Private constructor so this utility class cannot be instantiated.
     */

    private static String DATABASE = "LibraryDB.db";
    // Branch test 2

    /**
     * Main method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//	ItemsData itemsData = new ItemsData();
	Connection conn = DBUtils.initializeConnection(DATABASE);
	String nextLine = "";

	do {
	    // Print prompt
	    System.out.println("Please enter 'Add', 'Edit', 'Search', 'Order', 'Checkout', 'Delete' or 'End'");

	    // Try read next line
	    try {
		nextLine = reader.readLine();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }
	    nextLine = nextLine.toLowerCase(); // Sanitizing
	    switch (nextLine) {
	    case "add":
		InputManager.addItem(reader, conn);
		break;
	    case "edit":
		// Edit item here
		break;
	    case "search":
		OutputManager.readItem(reader, conn);
		break;
	    case "order":
		// Order items here
		break;
	    case "checkout":
		InputManager.addCheckoutItem(reader, conn);
		break;
	    case "delete":
		InputManager.deleteEntry(reader, conn);
		break;
	    case "end":
		System.out.println("Closing program");
		break;
	    default:
		System.out.println("Input not recognized.");
		break;
	    }

	} while (!nextLine.equals("end"));

	// Close reader and catch exception
	try {
	    reader.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	try {
	    conn.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }
}
