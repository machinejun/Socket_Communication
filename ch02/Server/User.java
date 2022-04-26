package ch02.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User extends Thread {
	private ServerService serverService;
	private ServerData serverData;

	private String nickName;
	private Socket userSocket;
	
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	
	StringBuffer check;

	public User(Socket userSocket, ServerService serverService, ServerData serverData) {
		this.serverData = serverData;
		this.userSocket = userSocket;
		this.serverService = serverService;
		nickName = "";
		serverData.getUserlist().add(this);
		check = new StringBuffer();
		check.append("");
	}

	@Override
	public void run() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						dataInputStream = new DataInputStream(userSocket.getInputStream());
						dataOutputStream = new DataOutputStream(userSocket.getOutputStream());

						String msg = dataInputStream.readUTF();
						serverService.writeMsg(msg);
						serverService.getMessage(msg);
					} catch (IOException e) {
						// TODO: handle exception
					}

				}
			}
		}).start();

	}
	public void exitRoom(String roomNumber) {
		InnerRoom r = null;
		for (InnerRoom room : serverData.getRoomlist()) {
			if (room.getRoomName().equals(roomNumber)) {
				r = room;
				break;
			}
		}
		r.getRoomUser().remove(this);
	}
	

	public void sentMsg(String msg) {
		try {
			dataOutputStream.writeUTF(msg);
			check.append(msg);
			System.out.println("check: " + msg);
			dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
