import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

public class ItemsData {
    public ArrayList<MediaItem> items;

    public ItemsData() {
	this.items = new ArrayList<MediaItem>();
    }

    public String ReadLine(BufferedReader reader) {
	String input = "";
	try {
	    input = reader.readLine();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	return input;
    }

    public void AddItem(BufferedReader reader) {
	String nextLine = "";
	boolean foundType = false;

	do {
	    System.out.println("What do you wish to add? Please respond with 'Musical album', 'Audio book' or 'Movie'");

	    try {
		nextLine = reader.readLine();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }

	    switch (nextLine) {
	    case "Musical album":
		this.AddMusicalAlbum(reader);
		foundType = true;
		break;
	    case "Audio book":
		this.AddAudioBook(reader);
		foundType = true;
		break;
	    case "Movie":
		this.AddMovie(reader);
		foundType = true;
		break;
	    default:
		System.out.println("Input not recognized.");
		break;
	    }
	} while (!foundType);

    }

    // Forgive the horrible try catches, java freaks out if they aren't there
    public void AddGeneralAtts(MediaItem mediaItem, BufferedReader reader) {
	System.out.println("Enter title: ");
	String title = this.ReadLine(reader);
	mediaItem.setTitle(title);

	System.out.println("Enter genre: ");
	String genre = this.ReadLine(reader);
	mediaItem.setGenre(genre);

	System.out.println("Enter DOI/EIDR: ");
	String doi = this.ReadLine(reader);
	mediaItem.setDoiEidr(doi);

	String day = "", month = "", year = "";
	System.out.println("Enter publcation day as integer: ");
	day = this.ReadLine(reader);
	System.out.println("Enter publcation month as integer: ");
	month = this.ReadLine(reader);
	System.out.println("Enter publication year as integer: ");
	year = this.ReadLine(reader);
	mediaItem.setPubDate(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day)));
    }

    public void AddAudioBook(BufferedReader reader) {
	AudioBook newItem = new AudioBook();
	this.AddGeneralAtts(newItem, reader);

	System.out.println("Please enter author's full name: ");
	String author = this.ReadLine(reader);
	newItem.setAuthor(author);

	System.out.println("Please enter total audio book length in seconds as integer: ");
	String totalLength = this.ReadLine(reader);
	newItem.setTotalLength(Integer.parseInt(totalLength));

	System.out.println("Please enter narrator's full name: ");
	String narrator = this.ReadLine(reader);
	newItem.setNarrator(narrator);

	this.items.add(newItem);
	System.out.println("Audio book added");
    }

    public void AddMovie(BufferedReader reader) {

	Movie newItem = new Movie();
	this.AddGeneralAtts(newItem, reader);

	System.out.println("Please enter IMDB rating as float: ");
	String rating = this.ReadLine(reader);
	newItem.setImdbRating(Float.parseFloat(rating));

	System.out.println("Please enter total runtime in seconds as integer: ");
	String runtime = this.ReadLine(reader);
	newItem.setRuntime(Integer.parseInt(runtime));

	System.out.println("Enter film publisher: ");
	String publisher = this.ReadLine(reader);
	newItem.setFilmPublisher(publisher);

	ArrayList<String> cast = new ArrayList<String>();
	String newCast = "";

	do {
	    System.out.println("Please enter cast member's full name or 'Done' to finalize cast. ");
	    newCast = this.ReadLine(reader);
	    if (!newCast.equals("Done")) {
		cast.add(newCast);
	    }
	} while (!newCast.equals("Done"));
	newItem.setCast(cast);

	this.items.add(newItem);
	System.out.println("Movie added");
    }

    public void AddMusicalAlbum(BufferedReader reader) {
	MusicalAlbum newItem = new MusicalAlbum();
	this.AddGeneralAtts(newItem, reader);

	System.out.println("Please enter runtime of album in seconds as integer: ");
	String runtime = "";
	runtime = this.ReadLine(reader);
	newItem.setRuntime(Integer.parseInt(runtime));

	System.out.println("Please enter album artist: ");
	String artist = "";
	artist = this.ReadLine(reader);
	newItem.setArtist(artist);

	System.out.println("Please enter album record label: ");
	String label = "";
	label = this.ReadLine(reader);
	newItem.setRecordLabel(label);

	int counter = 1;
	String title = "";
	ArrayList<Song> songs = new ArrayList<Song>();
	do {
	    System.out.println("Enter song " + counter + " name or 'Done': ");
	    title = this.ReadLine(reader);
	    if (!title.equals("Done")) {
		Song newSong = new Song(title);
		songs.add(newSong);
		counter++;
	    }
	} while (!title.equals("Done"));
	newItem.setSongs(songs);

	this.items.add(newItem);
	System.out.println("Musical album added");

    }

    public void EditItem(BufferedReader reader) {
	System.out.println("Name of item to Edit: ");
	String data = "";
	try {
	    data = reader.readLine();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("Data read: " + data);
    }

    public void SearchItems(BufferedReader reader) {
	System.out.println("Search by title: ");
	String title = "";
	try {
	    title = reader.readLine();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	System.out.println("Results: ");
	for (int i = 0; i < this.items.size(); i++) {
	    if (this.items.get(i).getTitle().equals(title)) {
		MediaItem curr = this.items.get(i);
		String type = this.items.get(i).getClass().getSimpleName();
		System.out.println("Title: " + curr.title + ", Type: " + type + ", Genre: " + curr.genre
			+ ", Pub. Date: " + curr.pubDate.toString() + " DOI/EIDR: " + curr.getDoiEdir());

	    }
	}

    }

    public void OrderItems(BufferedReader reader) {
	String orderType = "";
	// This is horrible
	do {
	    System.out.println("Order by 'Alphabetical', 'Reverse alphabetical', 'Published' or 'Reverse published'?");
	    orderType = this.ReadLine(reader);
	} while (!(orderType.equals("Alphabetical") || orderType.equals("Reverse alphabetical")
		|| orderType.equals("Published") || orderType.equals("Reverse published")));

	ArrayList<MediaItem> orderedItems = new ArrayList<MediaItem>(this.items);
	switch (orderType) {
	case "Aphabetical":
	    orderedItems.sort(new AlphabeticalComp());
	    break;
	case "Reverse Alphabetical":
	    orderedItems.sort(new ReverseAlphabeticalComp());
	    break;
	case "Published":
	    orderedItems.sort(new PubDateComp());
	    break;
	case "Reverse Published":
	    orderedItems.sort(new ReversePubDateComp());
	    break;

	}

	for (int i = 0; i < orderedItems.size(); i++) {
	    MediaItem curr = this.items.get(i);
	    String type = this.items.get(i).getClass().getSimpleName();
	    System.out.println("Title: " + curr.title + ", Type: " + type + ", Genre: " + curr.genre + ", Pub. Date: "
		    + curr.pubDate.toString() + " DOI/EIDR: " + curr.getDoiEdir());

	}
    }

}

class AlphabeticalComp implements Comparator<MediaItem> {
    @Override
    public int compare(MediaItem a, MediaItem b) {
	return a.title.compareToIgnoreCase(b.title);
    }
}

class ReverseAlphabeticalComp implements Comparator<MediaItem> {
    @Override
    public int compare(MediaItem a, MediaItem b) {
	return b.title.compareToIgnoreCase(a.title);
    }
}

class PubDateComp implements Comparator<MediaItem> {
    @Override
    public int compare(MediaItem a, MediaItem b) {
	return b.pubDate.compareTo(a.pubDate);
    }
}

class ReversePubDateComp implements Comparator<MediaItem> {
    @Override
    public int compare(MediaItem a, MediaItem b) {
	return a.pubDate.compareTo(b.pubDate);
    }
}
