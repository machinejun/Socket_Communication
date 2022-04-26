package ch02.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.swing.JOptionPane;

import ch02.View.ServerView;
import lombok.Data;

@Data
public class ServerServiceImpl implements ServerService {

	private ServerService mContext;
	private ServerView serverView; 

	private ServerSocket serverSocket;
	private ServerData serverData;
	private Socket socket;
	private ServerData dataList;

	private StringBuffer totalLog;

	public ServerServiceImpl(ServerView serverView) {
		this.mContext = this;
		this.serverView = serverView;
		totalLog = new StringBuffer();
		dataList = ServerData.getinstance();

	}
	
	@Override
	public void writeMsg(String msg) {
		serverView.showLog(msg);
	}
	

	@Override
	public void startNetwork(int portNumber) {
		try {
			serverSocket = new ServerSocket(portNumber);
			totalLog.append("통신을 시작합니다\n");
			linkSomeone();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "이미 사용중인 포트입니다.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "잘못 입력하셨습니다.", "Error", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}

	}

	@Override
	public void linkSomeone() {
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						socket = serverSocket.accept();
						User user = new User(socket, mContext, dataList);
						user.start();
					} catch (IOException e) {
						e.printStackTrace();
						break;
					}
				}

			}
		}).start();

	}

	@Override
	public void runServer(String[] partedMessage) {
		String[] protocol = partedMessage;
		dataList = ServerData.getinstance();

		switch (protocol[0]) {
		case "Admission":
			
			for (User u : dataList.getUserlist()) {
				if(u.getNickName().equals("")) {
					System.out.println(u.getNickName());
					u.setNickName(protocol[1]);
				}
			}
	
			String admissionlog = "NewUser/" + protocol[1];
			
			broadcast(admissionlog);
			BasicDataRecept(protocol[1]);
			break;

		case "Message":
			// Message/caller>receiver@contents
			StringTokenizer dividing = new StringTokenizer(protocol[1], ">|@");
			String caller = dividing.nextToken();
			String receiver = dividing.nextToken();
			String message = dividing.nextToken();

			for (User rUser : dataList.getUserlist()) {
				if (rUser.getNickName().equals(receiver)) {
					rUser.sentMsg("Message/from " + caller + " " + message);
				}
			}
			break;
		case "CreateRoom":
			// CreateRoom/nickName@RoomNumber
			createRoom(protocol[1]);
			break;

		case "Chatting":
			// Chatting/roomNumber>nickname@contents
			chatting(protocol[1]);
			break;

		case "EnterRoom":
			// EnterRoom/roomnumber@nickname
			enterRoom(protocol[1]);
			break;

		case "ExitRoom":
			// ExitRoom/RoomNumber@Nickname
			exitRoom(protocol[1]);
		default:

		}

	}

	@Override
	public void getMessage(String msg) {
		System.out.println("getMessage: " + msg);
		StringTokenizer dividing = new StringTokenizer(msg, "/");
		String logHead = dividing.nextToken();
		String logBody = dividing.nextToken();

		System.out.println("logHead: " + logHead);
		System.out.println("logBody: " + logBody);

		String[] protocol = new String[2];

		protocol[0] = logHead;
		protocol[1] = logBody;
		
		runServer(protocol);

	}

	@Override
	public void broadcast(String msg) {
		System.out.println("broadcast: " + msg);
		for (User user : dataList.getUserlist()) {
			user.sentMsg(msg);
			System.out.println("broadcast: " + user.getNickName() + "전송");
		}
	}

	@Override
	public void BasicDataRecept(String nickName) {
		User user = null;
		for (User u : dataList.getUserlist()) {
			if(u.getNickName().equals(nickName)) {
				user = u;
			}
		}
		
		System.out.println("basic: " + user.getNickName());
		
		for (User u : dataList.getUserlist()) {
			user.sentMsg("OldUser/" + u.getNickName());
		}
		
		try {
			for(InnerRoom room : dataList.getRoomlist()) {
				user.sentMsg("OldRoom/" + room.getRoomName());
			}
		} catch (NullPointerException e) {
		}
	}
	

	@Override
	public void createRoom(String msg) {
		StringTokenizer dividing = new StringTokenizer(msg, "@");
		String nickname = dividing.nextToken();
		String roomNumber = dividing.nextToken();
		User cUser = null;

		for (User user : serverData.getUserlist()) {
			user.getNickName().equals(nickname);
			cUser = user;
		}

		InnerRoom innerRoom = new InnerRoom(roomNumber, cUser);
		serverData.getRoomlist().add(innerRoom);

		broadcast("NewRoom/" + roomNumber);
	}

	@Override
	public void enterRoom(String log) {
		StringTokenizer dividing = new StringTokenizer(log, "@");
		String roomNumber = dividing.nextToken();
		String nickname = dividing.nextToken();
		User eUser = null;

		for (User user : serverData.getUserlist()) {
			user.getNickName().equals(nickname);
			eUser = user;
		}

		for (InnerRoom room : serverData.getRoomlist()) {
			if (room.getRoomName().equals(roomNumber)) {
				room.getRoomUser().add(eUser);
				for (User user : room.getRoomUser()) {
					user.sentMsg("EnterRoom/" + nickname + "@" + roomNumber);
				}

			}
		}
	}

	@Override
	public void exitRoom(String log) {
		StringTokenizer dividing = new StringTokenizer(log, "@");
		String roomNumber = dividing.nextToken();
		String nickName = dividing.nextToken();

		for (InnerRoom room : serverData.getRoomlist()) {
			if (room.getRoomName().equals(roomNumber)) {
				for (User user : room.getRoomUser()) {
					if (user.getNickName().equals(nickName)) {
						room.getRoomUser().remove(user);
						user.sentMsg("ExitRoom/ok");
					}
					user.sentMsg("ExitRoom/" + nickName);
				}

			}
		}
	}

	@Override
	public void printLog(String totalLog) {
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat format = new SimpleDateFormat("MM/dd");
		String date = "Log(" + format.format(calender) + ").txt";

		try {
			FileWriter writer = new FileWriter(new File(date));
			writer.write(totalLog);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void chatting(String log) {
		// TODO Auto-generated method stub
		StringTokenizer dividing = new StringTokenizer(log, ">|@");
		String roomNumber = dividing.nextToken();
		String nickname = dividing.nextToken();
		String contents = dividing.nextToken();

		for (InnerRoom room : serverData.getRoomlist()) {
			if (room.getRoomName().equals(roomNumber)) {
				for (User user : room.getRoomUser()) {
					if (!(user.getNickName().equals(nickname))) {
						user.sentMsg("Chatting/" + nickname + ": " + contents);
					}

				}
			}
		}
	}
}
