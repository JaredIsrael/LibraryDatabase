import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Put a short phrase describing the program here.
 *
 * @author Jared Israel
 *
 */
public final class ProgramWithIO {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private ProgramWithIO() {
    }

    /**
     * Main method.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	ItemsData itemsData = new ItemsData();

	String nextLine = "";

	do {
	    // Print prompt
	    System.out.println("Please enter 'Add', 'Edit', 'Search', 'Order' or 'End'");

	    // Try read next line
	    try {
		nextLine = reader.readLine();
	    } catch (IOException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	    }
	    nextLine = nextLine.toLowerCase(); // sanitzing
	    switch (nextLine) {
	    case "add":
		itemsData.AddItem(reader);
		break;
	    case "edit":
		itemsData.EditItem(reader);
		break;
	    case "search":
		itemsData.SearchItems(reader);
		break;
	    case "order":
		itemsData.OrderItems(reader);
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
    }
}
