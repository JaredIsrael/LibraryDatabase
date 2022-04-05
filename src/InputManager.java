import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InputManager {

    // Order: Title, doi/eidr, pub date
    private static String insertMediaItem = "INSERT INTO MEDIA_ITEM VALUES (?, ?, ?);";
    // Order: Name, DOB, library_id
    private static String insertPerson = "INSERT INTO PERSON VALUES (?,?,?);";
    // Order: Name
    private static String findPerson = "SELECT library_id FROM PERSON WHERE name = ?;";
    private static String nextUniqueId = "SELECT MAX(library_id) AS max FROM PERSON;";

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
		System.out.println("DEBUG: Get next library id next passed");
		nextId = rs.getInt("max");
	    }
	    ps.close();

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	nextId++;
	System.out.println("NEXT ID IS: " + nextId);
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
		System.out.println("DEBUG: Person recognized");
		return rs.getInt("library_id");
	    }
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	// PERSON MUST BE ADDED
	System.out.println("DEBUG: Person not recognized, adding new one");
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
