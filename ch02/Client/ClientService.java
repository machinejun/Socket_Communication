package ch02.Client;

import ch02.Server.User;

public interface ClientService {
	
	boolean login(String ip, int portNumber, String nickName);
	void runClient();
	void sentMsg(String log);
	void createRoom(String roomNumber);
	void enterRoom(String roomNumber);
	void exitRoom();
	void chat(String chat);
	String[] getLog(String log);
	void toMessage(String reciver, String Message);
}
