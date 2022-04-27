package ch02.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
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
	private ServerData dataList;

	private ServerSocket serverSocket;
	private ServerData serverData;
	private Socket socket;

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
						JOptionPane.showMessageDialog(null, "알수 없는 오류 발생", "Error", JOptionPane.ERROR_MESSAGE);
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
			dataList.checkUserNick("").setNickName(protocol[1]);
			String admissionlog = "NewUser/" + protocol[1];

			broadcast(admissionlog);
			BasicDataRecept(protocol[1]);
			break;

		case "Message":
			StringTokenizer dividing = new StringTokenizer(protocol[1], ">|@");
			String caller = dividing.nextToken();
			String receiver = dividing.nextToken();
			String message = dividing.nextToken();

			dataList.checkUserNick(receiver).sentMsg("Message/from " + caller + " " + message);
			break;
		case "CreateRoom":
			createRoom(protocol[1]);
			break;

		case "Chatting":
			chatting(protocol[1]);
			break;

		case "EnterRoom":
			enterRoom(protocol[1]);
			break;

		case "ExitRoom":
			exitRoom(protocol[1]);
		default:

		}

	}

	@Override
	public void getMessage(String msg) {
		StringTokenizer dividing = new StringTokenizer(msg, "/");
		String logHead = dividing.nextToken();
		String logBody = dividing.nextToken();
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
		}
	}

	@Override
	public void BasicDataRecept(String nickName) {
		User user = dataList.checkUserNick(nickName);

		for (User u : dataList.getUserlist()) {
			user.sentMsg("OldUser/" + u.getNickName());
		}

		try {
			for (InnerRoom room : dataList.getRoomlist()) {
				user.sentMsg("OldRoom/" + room.getRoomName());
			}
		} catch (NullPointerException e) {
		}
	}

	@Override
	public void createRoom(String msg) {
		StringTokenizer dividing = new StringTokenizer(msg, "@");
		String roomNumber = dividing.nextToken();
		String name = dividing.nextToken();

		User cUser = dataList.checkUserNick(name);
		InnerRoom innerRoom = new InnerRoom(roomNumber, cUser);
		dataList.getRoomlist().add(innerRoom);

		broadcast("NewRoom/" + roomNumber);
	}

	@Override
	public void enterRoom(String log) {
		StringTokenizer dividing = new StringTokenizer(log, "@");
		String roomNumber = dividing.nextToken();
		String nickname = dividing.nextToken();
		System.out.println(nickname);

		User eUser = dataList.checkUserNick(nickname);
		InnerRoom room = dataList.checkRoonNum(roomNumber);

		room.getRoomUser().add(eUser);

		for (User user : room.getRoomUser()) {
			user.sentMsg("EnterRoom/" + nickname + "@" + roomNumber);
		}

	}

	@Override
	public void exitRoom(String log) {
		StringTokenizer dividing = new StringTokenizer(log, "@");
		String roomNumber = dividing.nextToken();
		String nickName = dividing.nextToken();

		User exitUser = dataList.checkUserNick(nickName);
		exitUser.exitRoom(roomNumber);
		exitUser.sentMsg("ExitRoom/ok");

		InnerRoom emptyRoom = dataList.checkRoonNum(roomNumber);
		if (emptyRoom == null || emptyRoom.getRoomUser().size() == 0) {
			broadcast("Remove/" + roomNumber);
		} else {
			for (User user : emptyRoom.getRoomUser()) {
				user.sentMsg("ExitRoom/" + nickName);
			}
		}
	}

	@Override
	public void printLog(String totalLog) {
		Calendar calender = Calendar.getInstance();
		SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd");
		String date = f.format(calender.getTime());
		String path = "Log" + date + ".txt";
		System.out.println(totalLog);
		System.out.println(path);
		try {
			FileWriter writer = new FileWriter(new File(path));
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

		InnerRoom chatRoom = dataList.checkRoonNum(roomNumber);

		for (User user : chatRoom.getRoomUser()) {
			System.out.println("chatting>>>> " + user.getNickName());
			user.sentMsg("Chatting/" + nickname + ":  " + contents + "\n");
		}

	}
}
