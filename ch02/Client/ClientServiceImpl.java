package ch02.Client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import ch02.View.ClientView;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientServiceImpl implements ClientService {

	private ClientView clientView;
	private Socket socket;

	private InputStream inputStream;
	private OutputStream outputStream;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;

	private String nickName;
	private String roomNumber;
	
	public ClientServiceImpl(ClientView clientView) {
		this.clientView = clientView;

	}

	@Override
	public boolean login(String ip, int portNumber, String nickName) {
		try {
			socket = new Socket(ip, portNumber);
			
			
			inputStream = socket.getInputStream();
			dataInputStream = new DataInputStream(inputStream);

			outputStream = socket.getOutputStream();
			dataOutputStream = new DataOutputStream(outputStream);
			
			
			this.nickName = nickName;
			roomNumber = "0";
			
			String msg = "Admission" + "/" + nickName;
			sentMsg(msg);
			runClient();
			return true;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "연결실패!", "알림", JOptionPane.ERROR_MESSAGE);
			return false;
		}

	}

	@Override
	public void runClient() {

		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						String log = dataInputStream.readUTF();
						
						System.out.println(nickName+ "<<<<<" + log);
						String[] protocol = getLog(log);

						switch (protocol[0]) {
						case "NewUser":
							clientView.userUpdate(protocol[1]);
							break;
						case "Rename":
							nickName = protocol[1];
							clientView.getSomeOneName().setText("I am" + protocol[1]);
						case "OldUser":
							clientView.userUpdate(protocol[1]);
							break;
						case "OldRoom":
							clientView.roomUpdate(protocol[1]);
							break;
						case "NewRoom":
							clientView.roomUpdate(protocol[1]);
							break;
						case "EnterRoom":
							StringTokenizer d = new StringTokenizer(protocol[1], "@");
							String nickName = d.nextToken();
							roomNumber = d.nextToken();
							break;
						case "Message":
							JOptionPane.showMessageDialog(null, protocol[1]);
							
						case "ExitRoom":
							if(protocol[1].equals("ok")) {
								roomNumber = "0";
								JOptionPane.showMessageDialog(null, "퇴장하였습니다");
							}
							break;
						case "Chatting":
							clientView.getChatLogArea().append(protocol[1]);
							break;
						case "roomRemove":
							// protocol : roomRemove/유저가 없는 방의 roomNumber
							clientView.roomRemove(protocol[1]);
							break;
						case "Disconnect":
							// protocol : Disconnect/접속이 끊어진 유저 닉네임
							clientView.userRemove(protocol[1]);
						}
					} catch(SocketException e){
						JOptionPane.showMessageDialog(null, "서바가 다운되었습니다.");
						System.exit(0);
					}catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
		}).start();
	}
	
	@Override
	public void enterRoom(String roomNumber) {
		String msg = "EnterRoom/" + roomNumber + "@" + nickName;
		this.roomNumber = roomNumber;
		sentMsg(msg);
		
	}

	@Override
	public void createRoom(String roomNumber) {
		String msg = "CreateRoom/" + roomNumber + "@" + nickName;
		this.roomNumber = roomNumber;
		sentMsg(msg);
	}

	@Override
	public void exitRoom() {
		String msg = "ExitRoom/" + roomNumber + "@" + nickName;
		this.roomNumber = "0";
		sentMsg(msg);

	}

	@Override
	public void	chat(String chat) {
		String msg = "Chatting/"+ roomNumber+ ">" + nickName + "@" +chat;
		sentMsg(msg);
	}

	@Override
	public void sentMsg(String msg) {
		try {
			dataOutputStream.writeUTF(msg);
			dataOutputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	public void toMessage(String reciver, String Message) {
		String msg = "Message/" + reciver + ">"+ nickName + "@" + Message;
		sentMsg(msg);
	}

	@Override
	public String[] getLog(String log) {
		StringTokenizer dividing = new StringTokenizer(log, "/");
		String logHead = dividing.nextToken();
		String logBody = dividing.nextToken();

		String[] protocol = new String[2];

		protocol[0] = logHead;
		protocol[1] = logBody;

		return protocol;
	}
	
	public static void main(String[] args) {
		ClientServiceImpl client = new ClientServiceImpl(new ClientView());
	}

	

}
