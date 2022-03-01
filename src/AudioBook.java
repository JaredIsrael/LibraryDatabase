import java.time.LocalDate;

public class AudioBook extends MediaItem {
    private int totalLength;
    private String narrator;
    private String author;
    // Detail of chapters is not necessary here for this checkpoint
    // List<Chapter> chapters;

    public AudioBook() {
    }

    public AudioBook(String newTitle, String newDoi, LocalDate newPubDate, String newGenre, int newTotalLength,
	    String newNarrator, String newAuthor) {

	this.title = newTitle;
	this.doiEidr = newDoi;
	this.pubDate = newPubDate;
	this.genre = newGenre;
	this.totalLength = newTotalLength;
	this.narrator = newNarrator;
	this.author = newAuthor;
    }

    public int getTotalLength() {
	return this.totalLength;
    }

    // Any field conditions should be enforced here
    public void setTotalLength(int newTotalLength) {
	this.totalLength = newTotalLength;
    }

    public String getNarrator() {
	return this.narrator;
    }

    // Any field conditions should be enforced here
    public void setNarrator(String newNarrator) {
	this.narrator = newNarrator;
    }

    public String getAuthor() {
	return this.author;
    }

    public void setAuthor(String newAuthor) {
	this.author = newAuthor;
    }

}
