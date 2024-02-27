package wrappers;
import model.GameData;
import java.util.Collection;

public class GamesWrapper {
    private final Collection<GameData> games;

    public GamesWrapper(Collection<GameData> games) {
        this.games = games;
    }

    // Getter
    public Collection<GameData> getGames() {
        return games;
    }
}
