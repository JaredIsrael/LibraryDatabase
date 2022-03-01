import java.time.LocalDate;
import java.util.List;

public class MusicalAlbum extends MediaItem {
    private int runtime;
    private String recordLabel;
    private List<Song> songs;

    public MusicalAlbum(String newTitle, String newDoi, LocalDate newPubDate,
            String newGenre, int newRuntime, String newRecordLabel,
            List<Song> newSongs) {

        this.title = newTitle;
        this.doiEidr = newDoi;
        this.pubDate = newPubDate;
        this.genre = newGenre;
        this.runtime = newRuntime;
        this.recordLabel = newRecordLabel;
        this.songs = newSongs;
    }

    public List<Song> getSongs() {
        return this.songs;
    }

    public int getRuntime() {
        return this.runtime;
    }

    public String getRecordlabel() {
        return this.recordLabel;
    }

}
