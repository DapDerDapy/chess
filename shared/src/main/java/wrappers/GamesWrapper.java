package wrappers;
import model.GameData;
import java.util.Collection;

public class GamesWrapper {
    private Collection<GameData> games;

    public GamesWrapper(Collection<GameData> games) {
        this.games = games;
    }

    // Getter
    public Collection<GameData> getGames() {
        return games;
    }

    // Setter
    public void setGames(Collection<GameData> games) {
        this.games = games;
    }
}
