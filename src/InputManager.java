import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class InputManager {

    // Order: inventory_number
    private static String deleteCopy = "DELETE FROM COPY WHERE inventory_number=?;";
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
    // Order: Order Number, Price per Unit, Tracking Number, Carrier, DOI.EIDR
    private static String insertOrder = "INSERT INTO MEDIA_ORDER VALUES(?,?,?,?,?);";

    private static String updateTitle = "UPDATE MEDIA_ITEM SET title=? WHERE doi_eidr=?;";
    private static String updatePubDate = "UPDATE MEDIA_ITEM SET pub_date=? WHERE doi_eidr=?;";
    private static String updateAudiobookLength = "UPDATE AUDIOBOOK SET length=? WHERE doi_eidr=?;";
    private static String updateAudiobookGenre = "UPDATE AUDIOBOOK SET genre=? WHERE doi_eidr=?;";

    private static String updateMovieRuntime = "UPDATE MOVIE SET runtime=? WHERE doi_eidr=?;";
    private static String updateMoviePublisher = "UPDATE MOVIE SET publisher=? WHERE doi_eidr=?;";
    private static String updateMovieGenre = "UPDATE MOVIE SET genre=? WHERE doi_eidr=?;";
    private static String updateMovieRating = "UPDATE MOVIE SET rating=? WHERE doi_eidr=?;";

    private static String updateAlbumRuntime = "UPDATE MUSICAL_ALBUM SET runtime=? WHERE doi_eidr=?;";
    private static String updateAlbumLabel = "UPDATE MUSICAL_ALBUM SET record_label=? WHERE doi_eidr=?;";
    private static String updateAlbumGenre = "UPDATE MUSICAL_ALBUM SET genre=? WHERE doi_eidr=?;";

    private static String retrievePatron = "SELECT access_credentials FROM LIBRARY_PATRON WHERE library_id=?;";

    private static String nextUniqueId = "SELECT MAX(library_id) AS max FROM PERSON;";
    private static String checkoutInventory = "select mi.title, mi.doi_eidr, cp.doi_eidr, cp.patron_id, cp.checkout_date, cp.inventory_number from copy as cp, media_item as mi where mi.doi_eidr = cp.doi_eidr;";
    private static String findPersonName = "select ps.library_id, ps.name, lp.library_id from person as ps, library_patron as lp where ps.library_id = lp.library_id;";
    private static String checkoutConfirm = "update copy set patron_id = ?, checkout_date = ? where inventory_number = ?;";

    private static String findPersonCheckouts = "SELECT * FROM (SELECT * FROM COPY LEFT JOIN MEDIA_ITEM ON MEDIA_ITEM.doi_eidr = COPY.doi_eidr) WHERE patron_id = ?;";
    private static String returnConfirm = "update copy set patron_id = ?, checkout_date = ? where inventory_number = ?;";
    private static String returnReport = "insert into return values (?,?,?,?);";

    // Order: Address, email, Access_credentials, Library_id, active
    private static String insertPatron = "INSERT INTO LIBRARY_PATRON VALUES (?,?,?,?,?);";
    // Delete patron
    private static String deletePatron = "DELETE FROM LIBRARY_PATRON WHERE library_id = ?;";
    private static String mostMovies = "SELECT name, MAX(count) FROM (SELECT P.library_id,P.name, COUNT(*) as count FROM ((MOVIE AS M JOIN COPY AS C ON M.doi_eidr=C.doi_eidr)JOIN PERSON AS P ON P.library_id=C.patron_id) GROUP BY P.library_id);";
    // Report: Checkouts of each type from user
    private static String allCheckouts = "SELECT MEDIA_ITEM.title, COPY.format 	FROM COPY, MEDIA_ITEM, PERSON 	WHERE COPY.patron_id"
	    + "= Person.library_id AND Person.name = ? AND MEDIA_ITEM.doi_eidr = COPY.doi_eidr;";
    // Report: Tracks by artist
    private static String tracksByArtist = "SELECT SONG.name FROM SONG, MUSICAL_ALBUM, PERSON WHERE SONG.doi_eidr = MUSICAL_ALBUM.doi_eidr AND MUSICAL_ALBUM.library_id = Person.library_id AND Person.name = ?";

    // Views
    private static String artistsGenreIndex = "SELECT name, genre, MA.library_id FROM (MUSICAL_ALBUM AS MA JOIN MUSICAL_ARTIST AS M ON MA.library_id = M.library_id) AS Q, PERSON AS P WHERE Q.library_id = P.library_id";

    private static String actorGenreIndex = "SELECT genre, title, name"
	    + "FROM (MOVIE AS M JOIN MEDIA_ITEM AS MI ON M.doi_eidr = MI.doi_eidr) AS Q,"
	    + "(CAST AS C JOIN PERSON AS P ON C.library_id = P.library_id) AS Q1" + "WHERE Q.doi_eidr = C.doi_eidr"
	    + "ORDER BY genre ASC;";

    private static String songGenreIndex = "SELECT name, genre FROM (MUSICAL_ALBUM AS MA JOIN SONG AS S ON MA.doi_eidr = S.doi_eidr) ORDER BY name ASC;";

    private static String audioBookIndex = "SELECT title, genre, name FROM (AUDIOBOOK AS AB JOIN MEDIA_ITEM AS MI ON AB.doi_eidr = MI.doi_eidr), Person AS P WHERE author_library_id = P.library_id";

    private static String findOrder = "SELECT doi_eidr FROM MEDIA_ORDER WHERE order_number=?;";
    private static String deleteOrder = "DELETE FROM MEDIA_ORDER WHERE order_number=?";
    private static String insertCopy = "INSERT INTO COPY VALUES (?,?,?,?,?,?)";

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

    public static void addOrder(BufferedReader reader, Connection conn) {
	Map<String, String[]> inventory = new HashMap<String, String[]>();
	Map<String, String> people = new HashMap<String, String>();

	PreparedStatement stmt = null;
	ResultSet rs = null;

	System.out.println("Enter your library id (for debug use, lib. id '9' has admin access): ");
	String user_id = readLine(reader);
	try {
	    stmt = conn.prepareStatement(retrievePatron);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	try {
	    stmt.setString(1, user_id);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	rs = DBUtils.queryConnection(conn, stmt);

	try {
	    if (rs.next()) {
		if (rs.getBoolean("access_credentials")) {
		    System.out.println("Access credentials recognized.");
		    System.out.println();

		    // Order: Order Number, Price per Unit, Tracking Number, Carrier, DOI.EIDR
		    System.out.println(
			    "Please enter the Order Number, Price per Unit (floating point value in $), Tracking Number, Carrier, and DOI/EIDR of the item to order");
		    PreparedStatement upStmt = null;
		    try {
			upStmt = conn.prepareStatement(insertOrder);

			String order_num = readLine(reader);
			upStmt.setString(1, order_num);

			String ppu = readLine(reader);
			upStmt.setFloat(2, Float.parseFloat(ppu));

			String track_num = readLine(reader);
			upStmt.setString(3, track_num);

			String carrier = readLine(reader);
			upStmt.setString(4, carrier);

			String doi_eidr = readLine(reader);
			upStmt.setString(5, doi_eidr);
			DBUtils.updateQueryConnection(conn, upStmt);
		    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    System.out.println("Your order is processed!");
		    try {
			upStmt.close();
			stmt.close();
			rs.close();
		    } catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }

		} else {
		    System.out.println("The given libray ID does not have access credentials");
		}
	    } else {
		System.out.println("Library ID not recognized.");
	    }
	} catch (NumberFormatException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public static void orderArrived(BufferedReader reader, Connection conn) {
	try {
	    conn.setAutoCommit(false);
	} catch (SQLException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	System.out.println("Enter order number of arrived item: ");
	String orderNum = readLine(reader);
	ResultSet rs = null;
	try {
	    PreparedStatement ps = conn.prepareStatement(findOrder);
	    ps.setString(1, orderNum);
	    rs = DBUtils.queryConnection(conn, ps);
	    if (rs.next()) {
		String doi = rs.getString("doi_eidr");
		PreparedStatement ps2 = conn.prepareStatement(deleteOrder), ps3 = conn.prepareStatement(insertCopy);
		ps2.setString(1, orderNum);
		DBUtils.updateQueryConnection(conn, ps2);
		System.out.println("Enter new unqiue inventory number: ");
		String invNo = readLine(reader);
		ps3.setString(1, invNo);
		ps3.setString(2, doi);
		System.out.println("Enter format of media item: ");
		String format = readLine(reader);
		ps3.setString(3, format);
		System.out.println("Enter location in library for new item: ");
		String loc = readLine(reader);
		ps3.setNull(4, Types.VARCHAR);
		ps3.setNull(5, Types.VARCHAR);
		DBUtils.updateQueryConnection(conn, ps3);
		System.out.println("Order marked as received and a copy of the item has been added to database.");
		ps2.close();
		ps3.close();

	    } else {
		System.out.println("Order num not recognized");
	    }
	    ps.close();
	    rs.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	try {
	    conn.commit();
	    conn.setAutoCommit(true);
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public static void addCheckoutItem(BufferedReader reader, Connection conn) {
	Map<String, String[]> inventory = new HashMap<String, String[]>();
	Map<String, String> people = new HashMap<String, String>();

	PreparedStatement stmt = null;
	ResultSet rs = null;
	PreparedStatement stmt2 = null;
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
	    PreparedStatement upStmt = null;
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
		upStmt.close();
		stmt.close();
		stmt2.close();
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

    public static void addReturnItem(BufferedReader reader, Connection conn) {
	Map<String, String[]> inventory = new HashMap<String, String[]>();
	Map<String, String> people = new HashMap<String, String>();
	PreparedStatement stmt = null;
	ResultSet rs = null;
	PreparedStatement stmt2 = null;
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
		stmt = conn.prepareStatement(findPersonCheckouts);
		stmt.setString(1, user_id);
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
		e.printStackTrace();
	    }

	    System.out.println("Here is a list of items avaiable for return:");
	    String str2 = prettyPrintSizer("Media Title", arr[1]);
	    String str3 = prettyPrintSizer("DOI/EIDR", arr[2]);
	    System.out.println("Inventory Number " + str2 + str3);
	    for (Map.Entry<String, String[]> entry : inventory.entrySet()) {
		String inv_num = entry.getKey();
		String[] list = entry.getValue();
		System.out.println(inv_num + list[0] + list[1]);
	    }

	    System.out.println("Please enter the inventory number of the item to return");
	    String check_inv = readLine(reader);
	    PreparedStatement upStmt = null;
	    PreparedStatement retStmt = null;
	    try {
		upStmt = conn.prepareStatement(returnConfirm);
		retStmt = conn.prepareStatement(returnReport);
		conn.setAutoCommit(false);
		LocalDate dateObj = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		String date = dateObj.format(formatter);

		upStmt.setString(1, null);
		upStmt.setString(2, null);
		upStmt.setString(3, check_inv);

		retStmt.setString(1, check_inv);
		retStmt.setString(2, user_id);
		String temp = prettyPrintSizer(check_inv, "Inventory_Number".length() + 1);
		for (Map.Entry<String, String[]> entry : inventory.entrySet()) {
		    String inv_num = entry.getKey();
		    String[] list = entry.getValue();
		    if (inv_num.equals(temp)) {
			retStmt.setString(3, list[3]);
		    }
		}

		retStmt.setString(4, date);
		DBUtils.updateQueryConnection(conn, retStmt);
		DBUtils.updateQueryConnection(conn, upStmt);
		conn.commit();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Your return is processed!");
	    try {
		stmt.close();
		stmt2.close();
		upStmt.close();
		retStmt.close();
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
	try {
	    stmt3.close();
	    stmt4.close();
	    stmt5.close();
	    rs3.close();
	    rs4.close();
	    rs5.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
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

    public static void deleteEntry(BufferedReader reader, Connection conn) {
	System.out.println("Enter the inventory number of the lost item to be deleted: ");
	String invNo = readLine(reader);

	try {
	    PreparedStatement ps = conn.prepareStatement(deleteCopy);
	    ps.setString(1, invNo);
	    DBUtils.updateQueryConnection(conn, ps);
	    ps.close();
	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("Any copy with inventory number " + invNo + " was deleted");

    }

    public static void editEntry(BufferedReader reader, Connection conn) {
	System.out.println("Do you wish to edit an 'audiobook', 'movie', or 'musical album'?");
	String choice = readLine(reader).toLowerCase();
	switch (choice) {
	case "audiobook":
	    editAudioBook(reader, conn);
	    break;
	case "movie":
	    editMovie(reader, conn);
	    break;
	case "musical album":
	    editAlbum(reader, conn);
	    break;
	default:
	    System.out.println("Input not recognized, returning to main menu");
	    break;
	}
    }

    public static void editAudioBook(BufferedReader reader, Connection conn) {
	System.out.println("Enter the doi/eidr of the audiobook you wish to edit ");
	String doi = readLine(reader);
	System.out.println("What do you wish to change? Enter 'title', 'date of publication', 'length', or 'genre' ");
	String choice = readLine(reader);
	switch (choice) {
	case "title":
	    System.out.println("Enter new title: ");
	    String title = readLine(reader);
	    PreparedStatement psTitle = null;
	    try {
		psTitle = conn.prepareStatement(updateTitle);
		psTitle.setString(1, title);
		psTitle.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psTitle);
		psTitle.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "date of publication":
	    System.out.println("Enter new publishing date (YYYY-MM-DD): ");
	    String date = readLine(reader);
	    PreparedStatement psDate = null;
	    try {
		psDate = conn.prepareStatement(updatePubDate);
		psDate.setString(1, date);
		psDate.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psDate);
		psDate.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");

	    break;
	case "length":
	    System.out.println("Enter new length (HH:MM:SS): ");
	    String length = readLine(reader);
	    PreparedStatement psLength = null;
	    try {
		psLength = conn.prepareStatement(updateAudiobookLength);
		psLength.setString(1, length);
		psLength.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psLength);
		psLength.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "genre":
	    System.out.println("Enter new genre: ");
	    String genre = readLine(reader);
	    PreparedStatement psGenre = null;
	    try {
		psGenre = conn.prepareStatement(updateAudiobookGenre);
		psGenre.setString(1, genre);
		psGenre.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psGenre);
		psGenre.close();

	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");

	    break;
	default:
	    System.out.println("Input not recognized");
	}

    }

    public static void editMovie(BufferedReader reader, Connection conn) {
	System.out.println("Enter the doi/eidr of the Movie you wish to edit ");
	String doi = readLine(reader);
	System.out.println(
		"What do you wish to change? Enter 'title', 'date of publication', 'runtime', 'publisher', 'rating' or 'genre' ");
	String choice = readLine(reader).toLowerCase();

	switch (choice) {
	case "title":
	    System.out.println("Enter new title: ");
	    String title = readLine(reader);
	    PreparedStatement psTitle = null;
	    try {
		psTitle = conn.prepareStatement(updateTitle);
		psTitle.setString(1, title);
		psTitle.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psTitle);
		psTitle.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "date of publication":
	    System.out.println("Enter new publishing date (YYYY-MM-DD): ");
	    String date = readLine(reader);
	    PreparedStatement psDate = null;
	    try {
		psDate = conn.prepareStatement(updatePubDate);
		psDate.setString(1, date);
		psDate.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psDate);
		psDate.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");

	    break;
	case "runtime":
	    System.out.println("Enter new runtime (HH:MM:SS): ");
	    String runtime = readLine(reader);
	    PreparedStatement psRuntime = null;
	    try {
		psRuntime = conn.prepareStatement(updateMovieRuntime);
		psRuntime.setString(1, runtime);
		psRuntime.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psRuntime);
		psRuntime.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");

	    break;
	case "publisher":
	    System.out.println("Enter new publisher: ");
	    String publisher = readLine(reader);
	    PreparedStatement psPublisher = null;
	    try {
		psPublisher = conn.prepareStatement(updateMoviePublisher);
		psPublisher.setString(1, publisher);
		psPublisher.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psPublisher);
		psPublisher.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "rating":
	    System.out.println("Enter new rating: ");
	    String rating = readLine(reader);
	    PreparedStatement psRating = null;
	    try {
		psRating = conn.prepareStatement(updateMovieRating);
		psRating.setString(1, rating);
		psRating.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psRating);
		psRating.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "genre":
	    System.out.println("Enter new Genre: ");
	    String genreMovie = readLine(reader);
	    PreparedStatement psGenreMovie = null;
	    try {
		psGenreMovie = conn.prepareStatement(updateMovieGenre);
		psGenreMovie.setString(1, genreMovie);
		psGenreMovie.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psGenreMovie);
		psGenreMovie.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	default:
	    System.out.println("Input not recognized.");

	}

    }

    public static void editAlbum(BufferedReader reader, Connection conn) {
	System.out.println("Enter the doi/eidr of the album you wish to edit ");
	String doi = readLine(reader);
	System.out.println(
		"What do you wish to change? Enter 'title', 'date of publication', 'runtime', 'record label', or 'genre' ");
	String choice = readLine(reader).toLowerCase();

	switch (choice) {
	case "title":
	    System.out.println("Enter new title: ");
	    String title = readLine(reader);
	    PreparedStatement psTitle = null;
	    try {
		psTitle = conn.prepareStatement(updateTitle);
		psTitle.setString(1, title);
		psTitle.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psTitle);
		psTitle.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "date of publication":
	    System.out.println("Enter new publishing date (YYYY-MM-DD): ");
	    String date = readLine(reader);
	    PreparedStatement psDate = null;
	    try {
		psDate = conn.prepareStatement(updatePubDate);
		psDate.setString(1, date);
		psDate.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psDate);
		psDate.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");

	    break;
	case "runtime":
	    System.out.println("Enter new runtime (HH:MM:SS): ");
	    String runtime = readLine(reader);
	    PreparedStatement psRuntime = null;
	    try {
		psRuntime = conn.prepareStatement(updateAlbumRuntime);
		psRuntime.setString(1, runtime);
		psRuntime.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psRuntime);
		psRuntime.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "record label":
	    System.out.println("Enter new record label: ");
	    String label = readLine(reader);
	    PreparedStatement psLabel = null;
	    try {
		psLabel = conn.prepareStatement(updateAlbumLabel);
		psLabel.setString(1, label);
		psLabel.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psLabel);
		psLabel.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	case "genre":
	    System.out.println("Enter new runtime genre: ");
	    String genre = readLine(reader);
	    PreparedStatement psGenre = null;
	    try {
		psGenre = conn.prepareStatement(updateMovieRuntime);
		psGenre.setString(1, genre);
		psGenre.setString(2, doi);
		DBUtils.updateQueryConnection(conn, psGenre);
		psGenre.close();
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    System.out.println("Entry updated.");
	    break;
	default:
	    System.out.println("Input not recognized.");
	}
    }

    public static void addPatron(BufferedReader reader, Connection conn) {
	// Order: Address, email, Access_credentials, Library_id

	PreparedStatement ps = null;
	System.out.println("Enter a person's name: ");
	int libraryID = readOrAddPerson(reader, conn);
	System.out.println("Enter your address: ");
	String address = readLine(reader);
	System.out.println("Enter your email: ");
	String email = readLine(reader);
	try {
	    ps = conn.prepareStatement(insertPatron);
	    ps.setString(1, address);
	    ps.setString(2, email);
	    ps.setInt(3, 0);
	    ps.setInt(4, libraryID);
	    ps.setInt(5, 1);
	    DBUtils.updateQueryConnection(conn, ps);
	    ps.close();

	} catch (SQLException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("Patron added, assigned library id: " + libraryID);

    }

    public static void deactivateCard(BufferedReader reader, Connection conn) {
	System.out.println("Enter a patron's id #:");
	int libraryID = Integer.parseInt(readLine(reader));
	try {
	    PreparedStatement ps = null;
	    ps = conn.prepareStatement(deletePatron);
	    ps.setInt(1, libraryID);
	    DBUtils.updateQueryConnection(conn, ps);
	    ps.close();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	System.out.println("Patron deactivated.");
    }

    public static void Report(BufferedReader reader, Connection conn) {
	System.out.println("For a report on the patron with the most movies currently checked out, enter 1");
	System.out.println("For a report on all items a patron has checked out, enter 2");
	System.out.println("For a report of all songs by a particular artist, enter 3");
	System.out.print("Which report do you wish to view: ");
	String report = readLine(reader);
	PreparedStatement ps = null;
	ResultSet rs = null;
	if (report.equals("1")) {
	    try {
		ps = conn.prepareStatement(mostMovies);
		rs = DBUtils.queryConnection(conn, ps);
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else if (report.equals("2")) {
	    try {
		ps = conn.prepareStatement(allCheckouts);
		System.out.print("Enter the name of a patron: ");
		String patronName = readLine(reader);
		ps.setString(1, patronName);
		rs = DBUtils.queryConnection(conn, ps);
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else if (report.equals("3")) {
	    try {
		ps = conn.prepareStatement(tracksByArtist);
		System.out.print("Enter the name of the artist you're looking for: ");
		String artistName = readLine(reader);
		ps.setString(1, artistName);
		rs = DBUtils.queryConnection(conn, ps);
	    } catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	} else {

	}
	DBUtils.printResultSet(rs);
    }

}
