import java.time.LocalDate;
import java.util.List;

public class MusicalAlbum extends MediaItem {
    private String artist;
    private int runtime;
    private String recordLabel;
    private List<Song> songs;

    public MusicalAlbum() {

    }

    public MusicalAlbum(String newTitle, String newDoi, LocalDate newPubDate, String newGenre, int newRuntime,
	    String newRecordLabel, List<Song> newSongs, String newArtist) {

	this.title = newTitle;
	this.doiEidr = newDoi;
	this.pubDate = newPubDate;
	this.genre = newGenre;
	this.runtime = newRuntime;
	this.recordLabel = newRecordLabel;
	this.songs = newSongs;
	this.artist = newArtist;
    }

    public String getArtist() {
	return this.artist;
    }

    public void setArtist(String newArtist) {
	this.artist = newArtist;
    }

    public List<Song> getSongs() {
	return this.songs;
    }

    public void setSongs(List<Song> newSongs) {
	this.songs = newSongs;
    }

    public int getRuntime() {
	return this.runtime;
    }

    public void setRuntime(int newRuntime) {
	this.runtime = newRuntime;
    }

    public String getRecordlabel() {
	return this.recordLabel;
    }

    public void setRecordLabel(String newRecordLabel) {
	this.recordLabel = newRecordLabel;
    }

}
