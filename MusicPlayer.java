import java.util.*;

public class MusicPlayer {
    public static class Song {
        public final String id;
        public final String title;
        public final String artist;

        public Song(String id, String title, String artist) {
            this.id = id;
            this.title = title;
            this.artist = artist;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    private final LinkedList<Song> playlist = new LinkedList<>();
    private final Map<String, Song> library = new HashMap<>();
    private final Deque<Song> upNext = new ArrayDeque<>();
    private final Deque<Song> history = new ArrayDeque<>();
    private final Map<String, Integer> playCounts = new HashMap<>();
    private Song current = null;

    public boolean addSong(String id, String title, String artist) {
        if (library.containsKey(id)) return false;
        Song song = new Song(id, title, artist);
        playlist.addLast(song);
        library.put(id, song);
        return true;
    }

    public Song findSong(String id) {
        return library.get(id);
    }

    public List<String> showPlaylist() {
        List<String> titles = new ArrayList<>();
        for (Song song : playlist) titles.add(song.title);
        return titles;
    }

    private Song start(Song song) {
        current = song;
        playCounts.put(song.id, playCounts.getOrDefault(song.id, 0) + 1);
        return song;
    }

    public Song play(String id) {
        Song song = findSong(id);
        if (song == null) return null;
        if (current != null) history.push(current);
        return start(song);
    }

    public Song nowPlaying() {
        return current;
    }

    public int playCount(String id) {
        return playCounts.getOrDefault(id, 0);
    }

    public boolean queueLater(String id) {
        Song song = findSong(id);
        if (song == null) return false;
        upNext.addLast(song);
        return true;
    }

    public boolean queueNext(String id) {
        Song song = findSong(id);
        if (song == null) return false;
        upNext.addFirst(song);
        return true;
    }

    public List<String> showUpNext() {
        List<String> titles = new ArrayList<>();
        for (Song song : upNext) {
            if (library.containsKey(song.id)) titles.add(song.title);
        }
        return titles;
    }

    public Song next() {
        Song candidate = null;
        while (!upNext.isEmpty()) {
            Song song = upNext.pollFirst();
            if (library.containsKey(song.id)) {
                candidate = song;
                break;
            }
        }
        if (candidate == null) {
            if (current == null) return null;
            int index = playlist.indexOf(current);
            if (index < 0 || index + 1 >= playlist.size()) return null;
            candidate = playlist.get(index + 1);
        }
        if (current != null) history.push(current);
        return start(candidate);
    }

    public Song back() {
        Song candidate = null;
        while (!history.isEmpty()) {
            Song song = history.pop();
            if (library.containsKey(song.id)) {
                candidate = song;
                break;
            }
        }
        if (candidate == null) return null;
        if (current != null) upNext.addFirst(current);
        return start(candidate);
    }

    public List<String> showHistory() {
        List<String> titles = new ArrayList<>();
        for (Song song : history) {
            if (library.containsKey(song.id)) titles.add(song.title);
        }
        return titles;
    }

    public boolean removeSong(String id) {
        Song song = library.get(id);
        if (song == null || song == current) return false;
        playlist.remove(song);
        library.remove(id);
        playCounts.remove(id);
        return true;
    }
}
