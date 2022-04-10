import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InputManager {

    // Order: Title, doi/eidr, pub date
    private static String insertMediaItem = "INSERT INTO MEDIA_ITEM VALUES (?, ?, ?);";
    // Order: Name, DOB, library_id
    private static String insertPerson = "INSERT INTO PERSON VALUES (?,?,?);";
    // Order: Name
    private static String findPerson = "SELECT library_id FROM PERSON WHERE name = ?;";
    private static String nextUniqueId = "SELECT MAX(library_id) AS max FROM PERSON;";
    private static String checkoutInventory = "select mi.title, mi.doi_eidr, cp.doi_eidr, cp.patron_id, cp.checkout_date, cp.inventory_number from copy as cp, media_item as mi where mi.doi_eidr = cp.doi_eidr;";
    private static String findPersonName = "select ps.library_id, ps.name, lp.library_id from person as ps, library_patron as lp where ps.library_id = lp.library_id;";
    private static String checkoutConfirm = "update copy set patron_id = ?, checkout_date = ? where inventory_number = ?;";

    public static void addItem(BufferedReader reader, Connection conn) {
	String nextLine = "";
	boolean foundType = false;

	do {
	    System.out.println("What do you wish to add? Please respond with 'Musical album', 'Audio book' or 'Movie'");

	    try {
		nextLine = reader.readLine();
		nextLine = nextLine.toLowerCase();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    switch (nextLine) {
	    case "musical album":
		addMusicalAlbum(reader, conn);
		foundType = true;
		break;
	    case "audio book":
		addAudioBook(reader, conn);
		foundType = true;
		break;
	    case "movie":
		addMovie(reader, conn);
		foundType = true;
		break;
	    default:
		System.out.println("Input not recognized.");
		break;
	    }
	} while (!foundType);

    }

    public static String addMediaItem(BufferedReader reader, Connection conn) {
	String doi = "", title = "", pubdate = "";

	System.out.println("Enter title: ");
	title = readLine(reader);

	System.out.println("Enter doi/eidr: ");
	doi = readLine(reader);

	System.out.println("Enter date of publication (YYYY-MM-DD): ");
	pubdate = readLine(reader);

	PreparedStatement ps = null;
	try {
	    ps = conn.prepareStatement(insertMediaItem);
	    ps.setString(1, title);
	    ps.setString(2, doi);
	    ps.setString(3, pubdate);

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	DBUtils.updateQueryConnection(conn, ps);
	try {
	    ps.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return doi;
    }

    public static int getNextLibraryId(Connection conn) {
	int nextId = 0;
	PreparedStatement ps = null;
	try {
	    ps = conn.prepareStatement(nextUniqueId);

	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	ResultSet rs = DBUtils.queryConnection(conn, ps);

	try {
	    if (rs.next()) {
		nextId = rs.getInt("max");
	    }
	    rs.close();
	    ps.close();

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	nextId++;
	return nextId;
    }

    public static int readOrAddPerson(BufferedReader reader, Connection conn) {
	String name = readLine(reader);

	PreparedStatement ps = null;
	ResultSet rs = null;
	try {
	    ps = conn.prepareStatement(findPerson);
	    ps.setString(1, name);
	    rs = DBUtils.queryConnection(conn, ps);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	try {
	    if (rs.next()) {
		return rs.getInt("library_id");
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	// PERSON MUST BE ADDED
	System.out.println("Enter birthday if applicable (YYYY-MM-DD): ");
	String dob = readLine(reader);
	int nextId = getNextLibraryId(conn);

	PreparedStatement insert;
	try {
	    insert = conn.prepareStatement(insertPerson);
	    insert.setString(1, name);
	    insert.setString(2, dob);
	    insert.setInt(3, nextId);
	    DBUtils.updateQueryConnection(conn, insert);
	    ps.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	return nextId;
    }

    public static void addMusicalAlbum(BufferedReader reader, Connection conn) {

	// RUNTIME, RECORD LABEL, GENRE, ARTIST ID, DOI/EIDR

	String doi = addMediaItem(reader, conn);

	System.out.println("Enter artist name: ");
	int artistId = readOrAddPerson(reader, conn);

    }

    public static void addAudioBook(BufferedReader reader, Connection con) {

    }

    public static void addMovie(BufferedReader reader, Connection conn) {

    }
    
    public static void addCheckoutItem(BufferedReader reader, Connection conn) {
    	Map <String, String []> inventory = new HashMap<String, String []>();
    	Map<String, String> people = new HashMap<String, String>();
    	
    	PreparedStatement stmt = null;
    	PreparedStatement stmt2 = null;
    	ResultSet rs = null;
    	ResultSet rs2 = null;
    	
    	try {
    		stmt2 = conn.prepareStatement(findPersonName);
    		rs2 = DBUtils.queryConnection(conn, stmt2);
    		while (rs2.next()) {
    			String id = rs2.getString("library_id");
    			String name = rs2.getString("name");
    			people.put(id, name);
    		}
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    	System.out.println("Enter your library id: ");
    	String user_id = readLine(reader);
    	int[] arr = null;
		try {
			arr = prettyPrintMap(conn);
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	if (people.containsKey(user_id)) {
    		System.out.println("Hello " + people.get(user_id));
    		System.out.println();
    		try {
    			arr[0] = "Inventory Number".length() + 1;
        		stmt = conn.prepareStatement(checkoutInventory);
    			rs = DBUtils.queryConnection(conn, stmt);
    			while (rs.next()) {
    				String [] array = new String [4];
    				array[0] = prettyPrintSizer(rs.getString("title"), arr[1]);
    				array[1] = prettyPrintSizer(rs.getString("doi_eidr"), arr[2]);
    				array[2] = rs.getString("patron_id");
    				array[3] = rs.getString("checkout_date");
    				inventory.put(prettyPrintSizer(rs.getString("inventory_number"), arr[0]), array);
    			}
    		} catch (SQLException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		System.out.println("Here is a list of items avaiable for checkout:");
    		String str2 = prettyPrintSizer("Media Title", arr[1]);
    		String str3 = prettyPrintSizer("DOI/EIDR", arr[2]);
    		System.out.println("Inventory Number " + str2 + str3);
        	for (Map.Entry<String, String []> entry : inventory.entrySet()) {
        		String inv_num = entry.getKey();
        		String[] list = entry.getValue();
        		if (list[2] == null && list[3] == null) {
        			System.out.println(inv_num + list[0] + list[1]);
        		}
        	}
        	System.out.println("Please enter the inventory number of the item to checkout");
        	String check_inv = readLine(reader);
        	PreparedStatement upStmt;
        	try {
				upStmt = conn.prepareStatement(checkoutConfirm);
				
				LocalDate dateObj = LocalDate.now();
		        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		        String date = dateObj.format(formatter);
		        upStmt.setString(1, user_id);
		        upStmt.setString(2, date);
				upStmt.setString(3, check_inv);
				DBUtils.updateQueryConnection(conn, upStmt);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	System.out.println("Your checkout is processed!\nYour item is due within the next 30 days!");
        	try {
				rs.close();
				rs2.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
    	} else {
    		System.out.println("The given libray ID is incorrect or not in our system");
    	}
    }
    
    public static int[] prettyPrintMap(Connection conn) throws SQLException {
    	int [] arr = new int [3];
    	PreparedStatement stmt3 = conn.prepareStatement("select max(length(copy.inventory_number)) as max_inv from copy;");
    	ResultSet rs3 = DBUtils.queryConnection(conn, stmt3);
    	while (rs3.next()) {
    		arr[0] = rs3.getInt("max_inv");
    	}
    	PreparedStatement stmt4 = conn.prepareStatement("select max(length(media_item.title)) as max_title from media_item;");
    	ResultSet rs4 = DBUtils.queryConnection(conn, stmt4);
    	while (rs4.next()) {
    		arr[1] = rs4.getInt("max_title");
    	}PreparedStatement stmt5 = conn.prepareStatement("select max(length(media_item.doi_eidr)) as max_doi from media_item;");
    	ResultSet rs5 = DBUtils.queryConnection(conn, stmt5);
    	while (rs5.next()) {
    		arr[2] = rs5.getInt("max_doi");
    	}
    	rs3.close();
    	rs4.close();
    	rs5.close();
    	return arr;
    }
    
    public static String prettyPrintSizer(String word, int reqLength) {
    	while (word.length() < reqLength) {
    		word = word + " ";
    	}
    	return word;
    }
    
    
    public static String readLine(BufferedReader reader) {
	String input = "";
	try {
	    input = reader.readLine();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return input;
    }

}
