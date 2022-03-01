import java.time.LocalDate;
import java.util.List;

class MediaItem implements Comparable<MediaItem>{
    protected String title;
    protected String doiEidr;
    protected LocalDate pubDate;
    protected String genre;

    @Override
    public int compareTo(MediaItem a) {
        return this.title.compareTo(a.title);
    }

    // Not used for now, but this is used to keep track of associated physical
    // copies
    protected List<Copy> copies;

    public String getTitle() {
	return this.title;
    }

    // Any field conditions should be enforced here
    public void setTitle(String newTitle) {
	this.title = newTitle;
    }

    public String getDoiEdir() {
	return this.doiEidr;
    }

    // Any field conditions should be enforced here
    public void setDoiEidr(String newDoiEidr) {
	this.doiEidr = newDoiEidr;
    }

    public LocalDate getPubDate() {
	return this.pubDate;
    }

    // Any field conditions should be enforced here
    public void setPubDate(LocalDate newPubDate) {
	this.pubDate = newPubDate;
    }

    public String getGenre() {
	return this.genre;
    }

    // Any field conditions should be enforced here
    public void setGenre(String newGenre) {
	this.genre = newGenre;
    }
}

