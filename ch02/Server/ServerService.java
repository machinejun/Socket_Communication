package ch02.Server;

import java.net.Socket;

/**
 * @protocol 
 * @author ITPS
 *
 */
public interface ServerService {
	
	void startNetwork(int portNumber);
	void linkSomeone();
	void runServer(String[] protcol);
	void broadcast (String msg);
	void createRoom(String msg);
	void exitRoom(String msg);
	void enterRoom(String msg);
	void printLog(String totalLog);
	void BasicDataRecept(String nickname);
	void chatting(String msg);
	void getMessage(String msg);
	void writeMsg(String msg);
	
}
