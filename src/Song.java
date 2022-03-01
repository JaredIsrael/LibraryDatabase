import java.util.ArrayList;
import java.util.List;

public class Song {
    private String title;
    private int runtime;
    private ArrayList<String> featuredArtists;

    public Song(String newTitle) {
	this.title = newTitle;
	this.runtime = 0;
	this.featuredArtists = new ArrayList<String>();
    }

    public Song(String newTitle, int newRuntime, ArrayList<String> newFeaturedArtists) {
	this.title = newTitle;
	this.runtime = newRuntime;
	this.featuredArtists = newFeaturedArtists;
    }

    public Song(String newTitle, int newRuntime) {
	this.title = newTitle;
	this.runtime = newRuntime;
	this.featuredArtists = new ArrayList<String>();
    }

    public int getRuntime() {
	return this.runtime;
    }

    public String getTitle() {
	return this.title;
    }

    public List<String> getFeaturedArisits() {
	return this.featuredArtists;
    }

}
