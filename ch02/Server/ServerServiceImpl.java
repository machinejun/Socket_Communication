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
	private ServerData dataList;

	private ServerSocket serverSocket;
	private Socket socket;

	private StringBuffer logBuffer;

	public ServerServiceImpl(ServerView serverView) {
		this.mContext = this;
		this.serverView = serverView;
		logBuffer = new StringBuffer();
		dataList = ServerData.getinstance();

	}
	
	/**
	 * 로그텍스트 박스에 메세지 로그들을 입력 하는 기능
	 */
	@Override
	public void writeMsg(String msg) {
		serverView.showLog(msg);
	}

	@Override
	public void startNetwork(int portNumber) {
		try {
			serverSocket = new ServerSocket(portNumber);
			logBuffer.append("통신을 시작합니다\n");
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
	public void linkSomeone() { // 굳이 나눌 필요가 없었다 but 에러들을 구분하려고 이렇게 했는데 에러 케이스를 따로 잡으면 된다.
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					try {
						socket = serverSocket.accept();
						User user = new User(socket, mContext, dataList);
						user.start();
					} catch (IOException e) {
						JOptionPane.showMessageDialog(null, "유저 접속 오류", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}

			}
		}).start();

	}
	
	
	/**
	 *  protocol 형식 : 처리해야할 기능/ + Body
	 *  
	 *  바디: 2개의 정보를 전달 @ 로 구분
	 *        3개의 정보를 전달 > , @ 순으로 구분
	 */
	@Override
	public void actionProtocol(String[] partedMessage) {
		String[] protocol = partedMessage;
		dataList = ServerData.getinstance();

		switch (protocol[0]) {
		case "Admission":
			/*
			 *  새로운 유저가 접속을 하면 기존의 유저의 이름과 겹치는 지 검사한 후,
			 *  없다면 새로운 유저가 접속을 기존 유저에게 알린후 새로운 유저들에게 기존 데이터 전송
			 *  
			 *  protocol : Admission/새로운 유저 닉네임
			 */
			
			protocol[1] = nameDublicatingCheck(protocol[1]);
			dataList.checkUserNick("").setNickName(protocol[1]);
			String admissionlog = "NewUser/" + protocol[1];

			broadcast(admissionlog);
			pushOldUserAndOldRoom(protocol[1]);
			break;

		case "Message":
			/*
			 *  쪽지를 보내는 프로토콜을 처리
			 *  
			 *  protocol : Message/ + 수신자 + > + 발신자 + @ + 쪽지 내용
			 */
			StringTokenizer dividing = new StringTokenizer(protocol[1], ">|@");
			String receiver = dividing.nextToken();
			String caller = dividing.nextToken();
			String message = dividing.nextToken();

			dataList.checkUserNick(receiver).sentMsg("Message/from " + caller + " >>> " + message);
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
	
	/**
	 *  새로 들어온 유저의 닉네임이 기존의 유저와 중복을 확인한 후
	 *  중복이 된다면 입력한 닉네임 뒤에 + "*", 중복되지 않는다면 입력한 닉네임을 반환
	 *  
	 * @param 새로 들어온 유저의 nickName
	 * @return nickName OR nickName*
	 */
	private String nameDublicatingCheck(String nickName) {
		if(dataList.checkUserNick(nickName) == null) {
			return nickName;
		}else {
			rename(nickName + "*");
			return nickName + "*";
		}
		
	}
	
	/**
	 *  중복된 이름을 입력한 유저에게 자신이 이름이 수정되었음을 알린다.
	 *  
	 * @param 바뀐 닉네임
	 */
	private void rename(String nickname) {
		String Msg = "Rename/" + nickname;
		dataList.checkUserNick("").sentMsg(Msg);
	}
	
	/**
	 *  수신된 메세지를 머리부분과 바디부분으로 나누는 기능
	 *  
	 * @param 클라이언트로 부터 입력된 전체의 메세지
	 */
	@Override
	public void getMessage(String msg) {
		StringTokenizer dividing = new StringTokenizer(msg, "/");
		String msgHead = dividing.nextToken();
		String msgBody = dividing.nextToken();
		String[] protocol = new String[2];

		protocol[0] = msgHead;
		protocol[1] = msgBody;

		actionProtocol(protocol);
	}
	
	/**
	 * 모든 유저에게 알린다.
	 * 
	 * @param 전체 프로토콜 msg
	 */
	@Override
	public void broadcast(String msg) {
		System.out.println("broadcast: " + msg);
		for (User user : dataList.getUserlist()) {
			user.sentMsg(msg);
		}
	}
	
	/**
	 * 새로들어온 유저에게 기존의 유저와 기존의 방의 넘버 정보를 전한다.
	 * 
	 * @param 새로들어온 유저의 닉네임
	 */
	@Override
	public void pushOldUserAndOldRoom(String nickName) {
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
	
	/**
	 * protocol : "CreateRoom/roomNumber@방 생성자 닉네임
	 * 방에 들어오는 유저를 InnerRoom의 유저 리스트에 집어 넣고,
	 * InnerRoom의 유저리스트에 있는 유저들에게 진입을 알린다.
	 * 
	 * @param msghead를 제외한 부분
	 */
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
	
	/**
	 * protocol : EnterRoom/roomNumber + @ + 방에 들어오는 유저닉네임
	 * 방에 들어오는 유저를 InnerRoom의 유저 리스트에 집어 넣고,
	 * InnerRoom의 유저리스트에 있는 유저들에게 진입을 알린다.
	 * 
	 * @param msghead를 제외한 부분
	 */
	@Override
	public void enterRoom(String msg) {
		StringTokenizer dividing = new StringTokenizer(msg, "@");
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
	
	/**
	 * protocol : ExitRoom/roomNumber + @ + 방에 나가는 유저닉네임
	 * 방에서 나가는 유저를 InnerRoom의 유저 리스트에서 삭제하고,
	 * InnerRoom의 유저리스트에 있는 유저들에게 해당유저의 퇴장을 알린다.
	 * 
	 * @param msghead를 제외한 부분
	 */
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
			broadcast("roomRemove/" + roomNumber);
		} else {
			for (User user : emptyRoom.getRoomUser()) {
				user.sentMsg("ExitRoom/" + nickName);
			}
		}
	}
	
	/**
	 * 서버의 총 로그들을 출력하는 기능
	 * 
	 * @param 현재까지 받았던 로그들
	 */
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
	
	/**
	 * protocol : Chatting/roomNumber + > + 채팅을한 유저 닉네임 + @ + 채팅 내용 
	 * 서버의 총 로그들을 출력하는 기능
	 * 
	 * @param msghead를 제외한 부분
	 */
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
	
	public static void main(String[] args) {
		ServerServiceImpl Server = new ServerServiceImpl(new ServerView());
	}
}
