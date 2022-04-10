import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class InputManager {

    // Order: Title, doi/eidr, pub date
    private static String insertMediaItem = "INSERT INTO MEDIA_ITEM VALUES (?, ?, ?);";
    // Order: Name, DOB, library_id
    private static String insertPerson = "INSERT INTO PERSON VALUES (?,?,?);";
    // Order: Name
    private static String findPerson = "SELECT library_id FROM PERSON WHERE name = ?;";

    // Order: runtime, publisher, genre, rating, doi
    private static String insertMovie = "INSERT INTO MOVIE VALUES(?,?,?,?,?)";
    // Order: library id, imdb url
    private static String insertActor = "INSERT INTO ACTOR VALUES(?,?)";
    // Order: doi, library_id, role
    private static String insertCast = "INSERT INTO CAST VALUES(?,?,?)";
    // Order: library_id, spotify_url, applemusic_url
    private static String insertMusicalArtist = "INSERT INTO MUSICAL_ARTIST VALUES(?,?,?)";
    // Order: runtime, record_label, genre, library_id, doi_eidr
    private static String insertMusicalAlbum = "INSERT INTO MUSICAL_ALBUM VALUES(?,?,?,?,?)";
    // Order: length, genre, author_library_id, narrator_library_id, doi_eidr
    private static String insertAudiobook = "INSERT INTO AUDIOBOOK VALUES(?,?,?,?,?)";
    // Order: name, chapter_index, length, doi_eidr
    private static String insertChapter = "INSERT INTO BOOK_CHAPTER VALUES(?,?,?,?)";

    // Order: library id
    private static String findActor = "SELECT * FROM ACTOR AS A WHERE A.library_id=?";
    // Order: library id
    private static String findMusicalArtist = "SELECT* FROM MUSICAL_ARTIST AS M WHERE M.library_id=?;";
    // Order: Name, runtime, doi
    private static String insertSong = "INSERT INTO SONG VALUES(?,?,?);";
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

    public static void addMusicalArtist(int artistId, Connection conn, BufferedReader reader) {
	ResultSet rs = null;
	PreparedStatement psFind = null, psUpdate = null;
	try {
	    psFind = conn.prepareStatement(findMusicalArtist);
	    psFind.setInt(1, artistId);
	    rs = DBUtils.queryConnection(conn, psFind);
	    if (!rs.next()) {
		System.out.println("Please enter the artist's spotify page URL: ");
		String spotifyUrl = readLine(reader);
		System.out.println("Please enter the artist's apple music page URL: ");
		String amUrl = readLine(reader);
		psUpdate = conn.prepareStatement(insertMusicalArtist);
		psUpdate.setInt(1, artistId);
		psUpdate.setString(2, spotifyUrl);
		psUpdate.setString(3, amUrl);
		DBUtils.updateQueryConnection(conn, psUpdate);
		psUpdate.close();

	    }

	    psFind.close();
	    rs.close();

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void addMusicalAlbum(BufferedReader reader, Connection conn) {

	// RUNTIME, RECORD LABEL, GENRE, ARTIST ID, DOI/EIDR
	try {
	    conn.setAutoCommit(false);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	String doi = addMediaItem(reader, conn);

	System.out.println("Enter artist name: ");
	int artistId = readOrAddPerson(reader, conn);
	addMusicalArtist(artistId, conn, reader);

	System.out.println("Enter runtime (HH:MM:SS): ");
	String runtime = readLine(reader);
	System.out.println("Enter record label: ");
	String recordLabel = readLine(reader);
	System.out.println("Enter genre: ");
	String genre = readLine(reader);

	PreparedStatement ps = null;
	try {
	    ps = conn.prepareStatement(insertMusicalAlbum);
	    ps.setString(1, runtime);
	    ps.setString(2, recordLabel);
	    ps.setString(3, genre);
	    ps.setInt(4, artistId);
	    ps.setString(5, doi);
	    DBUtils.updateQueryConnection(conn, ps);
	    ps.close();
	    conn.commit();
	    conn.setAutoCommit(true);

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("Album added. Do you wish to add songs? (Y/N)");
	String choice = readLine(reader);
	if (choice.equals("Y")) {
	    addSongs(doi, reader, conn);
	}

    }

    public static void addSongs(String doi, BufferedReader reader, Connection conn) {
	String cont = "Y", name = "", runtime = "";
	try {
	    conn.setAutoCommit(false);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	while (cont.equals("Y")) {
	    System.out.println("Enter song name: ");
	    name = readLine(reader);
	    System.out.println("Enter runtime of song: (HH:MM:SS)");
	    runtime = readLine(reader);

	    PreparedStatement ps = null;
	    try {
		ps = conn.prepareStatement(insertSong);
		ps.setString(1, name);
		ps.setString(2, runtime);
		ps.setString(3, doi);
		DBUtils.updateQueryConnection(conn, ps);
		ps.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    System.out.println("Continue adding songs? (Y/N)");
	    cont = readLine(reader);
	}

	try {
	    conn.commit();
	    conn.setAutoCommit(true);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void addAudioBook(BufferedReader reader, Connection conn) {
	try {
	    conn.setAutoCommit(false);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	String doi = addMediaItem(reader, conn);
	System.out.println("Enter author name: ");
	int authorId = readOrAddPerson(reader, conn);
	System.out.println("Enter narrator name: ");
	int narratorId = readOrAddPerson(reader, conn);

	String length, genre;
	System.out.println("Enter length of audiobook: (HH:MM:SS)");
	length = readLine(reader);
	System.out.println("Enter genre: ");
	genre = readLine(reader);
	try {
	    PreparedStatement ps = conn.prepareStatement(insertAudiobook);
	    ps.setString(1, length);
	    ps.setString(2, genre);
	    ps.setInt(3, authorId);
	    ps.setInt(4, narratorId);
	    ps.setString(5, doi);
	    DBUtils.updateQueryConnection(conn, ps);
	    ps.close();

	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	try {
	    conn.commit();
	    conn.setAutoCommit(true);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	System.out.println("Audiobook added, do you wish to add chapters? (Y/N)");
	String choice = readLine(reader);
	if (choice.equals("Y")) {
	    addChapters(doi, reader, conn);
	}
    }

    public static void addChapters(String doi, BufferedReader reader, Connection conn) {
	String cont = "Y", name = "", length = "";
	int index;
	try {
	    conn.setAutoCommit(false);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	while (cont.equals("Y")) {
	    System.out.println("Enter chapter number (index): ");
	    index = Integer.parseInt(readLine(reader));
	    System.out.println("Enter chapter name (if applicable): ");
	    name = readLine(reader);
	    System.out.println("Enter length of chapter: (HH:MM:SS)");
	    length = readLine(reader);
	    // Order: name, chapter_index, length, doi_eidr

	    PreparedStatement ps = null;
	    try {
		ps = conn.prepareStatement(insertChapter);
		ps.setString(1, name);
		ps.setInt(2, index);
		ps.setString(3, length);
		ps.setString(4, doi);
		DBUtils.updateQueryConnection(conn, ps);
		ps.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    System.out.println("Continue adding chapters? (Y/N)");
	    cont = readLine(reader);
	}

	try {
	    conn.commit();
	    conn.setAutoCommit(true);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void addMovie(BufferedReader reader, Connection conn) {
	try {
	    conn.setAutoCommit(false);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	String doi, runtime, publisher, genre, rating;
	doi = addMediaItem(reader, conn);
	System.out.println("Enter movie runtime (HH:MM:SS): ");
	runtime = readLine(reader);
	System.out.println("Enter film publisher: ");
	publisher = readLine(reader);
	System.out.println("Enter genre: ");
	genre = readLine(reader);
	System.out.println("Enter rating: ");
	rating = readLine(reader);
	try {
	    PreparedStatement ps = conn.prepareStatement(insertMovie);
	    ps.setString(1, runtime);
	    ps.setString(2, publisher);
	    ps.setString(3, genre);
	    ps.setString(4, rating);
	    ps.setString(5, doi);

	    DBUtils.updateQueryConnection(conn, ps);
	    ps.close();

	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}

	try {
	    conn.commit();
	    conn.setAutoCommit(true);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	System.out.println("Movie added, do you wish to add cast/role information?(Y/N)");
	String choice = readLine(reader);
	if (choice.equals("Y")) {
	    addCast(doi, reader, conn);
	}
    }

    public static void addCast(String doi, BufferedReader reader, Connection conn) {
	// doi, lib id, role
	String cont = "Y", role = "";
	int libraryId;
	try {
	    conn.setAutoCommit(false);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	while (cont.equals("Y")) {
	    System.out.println("Enter name: ");
	    libraryId = readOrAddPerson(reader, conn);
	    addActor(libraryId, reader, conn);
	    System.out.println("Enter role(s): ");
	    role = readLine(reader);
	    PreparedStatement ps = null;
	    try {
		ps = conn.prepareStatement(insertCast);
		ps.setString(1, doi);
		ps.setInt(2, libraryId);
		ps.setString(3, role);
		DBUtils.updateQueryConnection(conn, ps);
		ps.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    System.out.println("Continue adding cast? (Y/N)");
	    cont = readLine(reader);
	}

	try {
	    conn.commit();
	    conn.setAutoCommit(true);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void addActor(int actorId, BufferedReader reader, Connection conn) {
	ResultSet rs = null;
	PreparedStatement psFind = null, psUpdate = null;
	try {
	    psFind = conn.prepareStatement(findActor);
	    psFind.setInt(1, actorId);
	    rs = DBUtils.queryConnection(conn, psFind);
	    if (!rs.next()) {
		System.out.println("Please enter the actors imdb url: ");
		String imdbUrl = readLine(reader);
		psUpdate = conn.prepareStatement(insertActor);
		psUpdate.setInt(1, actorId);
		psUpdate.setString(2, imdbUrl);
		DBUtils.updateQueryConnection(conn, psUpdate);
		psUpdate.close();
	    }

	    psFind.close();
	    rs.close();

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void addCheckoutItem(BufferedReader reader, Connection conn) {
	Map<String, String[]> inventory = new HashMap<String, String[]>();
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
		    String[] array = new String[4];
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
	    for (Map.Entry<String, String[]> entry : inventory.entrySet()) {
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
	int[] arr = new int[3];
	PreparedStatement stmt3 = conn
		.prepareStatement("select max(length(copy.inventory_number)) as max_inv from copy;");
	ResultSet rs3 = DBUtils.queryConnection(conn, stmt3);
	while (rs3.next()) {
	    arr[0] = rs3.getInt("max_inv");
	}
	PreparedStatement stmt4 = conn
		.prepareStatement("select max(length(media_item.title)) as max_title from media_item;");
	ResultSet rs4 = DBUtils.queryConnection(conn, stmt4);
	while (rs4.next()) {
	    arr[1] = rs4.getInt("max_title");
	}
	PreparedStatement stmt5 = conn
		.prepareStatement("select max(length(media_item.doi_eidr)) as max_doi from media_item;");
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
