import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Put a short phrase describing the program here.
 *
 * @author Jared Israel
 * @author Jake Haskins
 * @author Santosh Gajje
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
	    System.out.println("Please enter 'Add', 'Edit', 'Search', 'Checkout', 'Return', 'Report, 'Delete' or 'End'");

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
		InputManager.editEntry(reader, conn);
		break;
	    case "search":
		OutputManager.readItem(reader, conn);
		break;
	    case "checkout":
		InputManager.addCheckoutItem(reader, conn);
		break;
	    case "return":
		// Return items here
		InputManager.addReturnItem(reader, conn);
		break;
		case "report":
		InputManager.Report(reader, conn);
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
