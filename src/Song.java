import java.util.List;

public class Song {
    private String title;
    private int runtime;
    private List<String> featuredArtists;

    public Song(String newTitle, int newRuntime,
            List<String> newFeaturedArtists) {
        this.title = newTitle;
        this.runtime = newRuntime;
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
