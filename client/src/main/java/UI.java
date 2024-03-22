import dataAccess.AuthDAO;

public class UI {
    private boolean isLoggedIn = false;
    private String authToken = null;

    private String username = null;


    public void runUI() {
        while (true) {
            if (!isLoggedIn) {
                PreloginUI preloginUI = new PreloginUI();
                preloginUI.processUserInput();
                if (preloginUI.moveToPost()) {
                    isLoggedIn = true;
                }
            } else {
                PostLoginUI postLoginUI = new PostLoginUI(authToken, username);
                postLoginUI.processUserInput();
            }
        }
    }
}
