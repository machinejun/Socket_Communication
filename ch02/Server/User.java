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
	private int roomNumber;
	private Socket userSocket;

	private InputStream inputStream;
	private OutputStream outputStream;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;

	public User(Socket userSocket, ServerService serverService, ServerData serverData) {
		this.serverData = serverData;
		this.userSocket = userSocket;
		this.serverService = serverService;
		nickName = "";
		roomNumber = 0;
		serverData.getUserlist().add(this);
	}

	@Override
	public void run() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						inputStream = userSocket.getInputStream();
						dataInputStream = new DataInputStream(inputStream);

						outputStream = userSocket.getOutputStream();
						dataOutputStream = new DataOutputStream(outputStream);

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

	public void sentMsg(String msg) {
		System.out.println(nickName +">>> sentMsg: " + msg);
		try {
			dataOutputStream.writeUTF(msg);
			dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void enterRoom() {
		// serverData.getRoomlist().get(roomNumber)
	}

}
