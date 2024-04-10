package server.websocket;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import com.google.gson.Gson;
import webSocketMessages.userCommands.UserGameCommand;

@ServerEndpoint(value = "/connect")
public class WSHandler {

    private Gson gson = new Gson(); // For JSON serialization/deserialization

    @OnOpen
    public void onOpen(Session session) {
        // Handle new connection
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()) {
            case JOIN_PLAYER:
                joinGame(/* parameters */);
                break;
            case MAKE_MOVE:
                makeMove(/* parameters */);
                break;
            case JOIN_OBSERVER:
                joinObserver(/* parameters */);
                break;
            case RESIGN:
                resign(/* parameters */);
                break;
            case LEAVE:
                leave(/* parameters */);
                break;
        }
    }

    @OnClose
    public void onClose(Session session, CloseReason closeReason) {
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
    }

    private void joinGame(){

    }

    private void makeMove(){

    }

    private void joinObserver(){

    }

    private void resign(){

    }

    private void leave(){

    }

}
