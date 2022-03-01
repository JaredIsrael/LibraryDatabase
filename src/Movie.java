import java.time.LocalDate;
import java.util.ArrayList;

public class Movie extends MediaItem {
    private float imdbRating;
    private int runtime;
    private String filmPublisher;
    // Extend functionality with CastMember class, associating a role to each member
    private ArrayList<String> cast;

    public Movie() {
    }

    public Movie(String newTitle, String newDoi, LocalDate newPubDate, String newGenre, float newImdbRating,
	    int newRuntime, String newFilmPublisher, ArrayList<String> newCast) {

	this.title = newTitle;
	this.doiEidr = newDoi;
	this.pubDate = newPubDate;
	this.genre = newGenre;
	this.imdbRating = newImdbRating;
	this.runtime = newRuntime;
	this.filmPublisher = newFilmPublisher;
	this.cast = newCast;
    }

    public String getFilmPublisher() {
	return this.filmPublisher;
    }

    public void setFilmPublisher(String newFilmPublisher) {
	this.filmPublisher = newFilmPublisher;
    }

    public float getImdbRating() {
	return this.imdbRating;
    }

    public void setImdbRating(float newImbdRating) {
	this.imdbRating = newImbdRating;
    }

    public int getRuntime() {
	return this.runtime;
    }

    public void setRuntime(int newRuntime) {
	this.runtime = newRuntime;
    }

    public ArrayList<String> getCast() {
	return this.cast;
    }

    public void setCast(ArrayList<String> newCast) {
	this.cast = newCast;
    }

}
