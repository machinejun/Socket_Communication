package ch02.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
	

	public User(Socket userSocket, ServerService serverService, ServerData serverData) {
		this.serverData = serverData;
		this.userSocket = userSocket;
		this.serverService = serverService;
		nickName = "";
		serverData.getUserlist().add(this);
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
						try {
							disconnect();
							dataInputStream.close();
							dataOutputStream.close();
							break;
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
					}

				}
			}
		}).start();

	}
	/**
	 * 해당유저가 방을 나갔을 경우 InnerRoom의 User데이터에서 자신을 삭제한다.
	 * @param roomNumber
	 */
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
	
	
	/**
	 * 접속이 끊어 졌을 경우 서버에 끊어짐을 기록한 후 유저들에게 해당 유저의
	 * 접속이 끊어졌다는 것을 알린다.
	 */
	private void disconnect() {
		serverData.getUserlist().remove(this);
		for (InnerRoom room : serverData.getRoomlist()) {
			if(room.getRoomUser().contains(this)) {
				room.getRoomUser().remove(this);
			}
		}
		serverService.writeMsg("Disconnect/" + nickName);
		serverService.broadcast("Disconnect/" + nickName);
	}
	
	/**
	 * 클라이언트로 부터 전송받은 메세지를 서버에 전송하는 기능
	 * @param msg
	 */
	public void sentMsg(String msg) {
		try {
			dataOutputStream.writeUTF(msg);
			dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
