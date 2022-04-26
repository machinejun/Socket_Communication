package ch02.View;

import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import ch02.Client.ClientService;
import ch02.Client.ClientServiceImpl;
import ch02.Server.InnerRoom;
import ch02.Server.ServerData;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ClientView extends JFrame implements ActionListener{
	
	private ClientService clientService;
	private ServerData serverdata;
	private boolean loginFlag;
	
	private ImagePanel bgImagePanel;
	private JTabbedPane jTabbedPane;
	private JButton endBtn;
	
	private JPanel firstPanel;
	private JLabel iptext;
	private JTextField userIPArea;
	private JLabel porttext;
	private JTextField portNumberArea;
	private JLabel nickNametext;
	private JTextField userNicNameArea;
	private JButton startBtn;
	
	private JPanel secondPanel;
	private JList userList;
	private JList roomList;
	private JButton makingRoomBtn;
	private JButton sentMsgBtn;
	private JButton enterRoomBtn;
	private JButton exitRoomBtn;
	
	private JPanel thirdPanel;
	private ScrollPane chatLogBox;
	private JTextArea chatLogArea;
	private JTextField chatArea;
	private JButton sentchatBtn;
	
	Vector<String> users;
	Vector<String> rooms;
	
	public ClientView() {
		clientService = new ClientServiceImpl(this);
		serverdata = ServerData.getinstance();
		initObject();
		initLayout();
		addEventListner();
		loginFlag = false;
	}
	
	private void initObject() {
		bgImagePanel = new ImagePanel();
		jTabbedPane = new JTabbedPane();
		
		endBtn = new JButton("End Program");
		firstPanel = new JPanel();
		iptext = new JLabel("IP : ");
		userIPArea = new JTextField("127.0.0.1");
		porttext = new JLabel("PortNumber: ");
		portNumberArea = new JTextField("input portNumber");
		nickNametext = new JLabel("NickName : ");
		userNicNameArea = new JTextField("write user NickName");
		startBtn = new JButton("Start Connect");
		
		secondPanel = new JPanel();
		userList = new JList<String>();
		users = new Vector<String>();
		userList.setListData(users);
		roomList = new JList<InnerRoom>();
		rooms = new Vector<String>();
		roomList.setListData(rooms);
		makingRoomBtn = new JButton("방생성");
		enterRoomBtn = new JButton("입장");
		exitRoomBtn = new JButton("방나가기");
		
		thirdPanel = new JPanel();
		chatLogBox = new ScrollPane();
		chatLogArea = new JTextArea();
		chatArea = new JTextField();
		sentchatBtn = new JButton("sent");
		
		setContentPane(bgImagePanel);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(500, 500);
		
		bgImagePanel.add(jTabbedPane);
		jTabbedPane.addTab("login", firstPanel);
		firstPanel.add(iptext);
		firstPanel.add(userIPArea);
		firstPanel.add(porttext);
		firstPanel.add(portNumberArea);
		firstPanel.add(nickNametext);
		firstPanel.add(userNicNameArea);
		firstPanel.add(startBtn);
		
		
		
		secondPanel.add(userList);
		secondPanel.add(roomList);
		secondPanel.add(makingRoomBtn);
		secondPanel.add(enterRoomBtn);
		secondPanel.add(exitRoomBtn);
		
		
		thirdPanel.add(chatLogBox);
		chatLogBox.add(chatLogArea);
		thirdPanel.add(chatArea);
		thirdPanel.add(sentchatBtn);
		
		
	
	}
	
	private void initLayout() {
		setVisible(true);
		jTabbedPane.setBounds(50, 100, 400, 300);
		
		firstPanel.setLayout(null);
		iptext.setBounds(100, 30, 50, 50);
		userIPArea.setBounds(130, 40, 160, 30);
		porttext.setBounds(100, 80, 100, 50);
		portNumberArea.setBounds(180, 90, 110, 30);
		nickNametext.setBounds(100, 130, 100, 50);
		userNicNameArea.setBounds(170, 140, 120, 30);
		startBtn.setBounds(170, 200, 120, 30);
		
		secondPanel.setLayout(null);
		userList.setBounds(65, 30, 120, 170);
		roomList.setBounds(215, 30, 120, 170);
		makingRoomBtn.setBounds(105, 220, 80, 30);
		enterRoomBtn.setBounds(195, 220, 80, 30);
		exitRoomBtn.setBounds(285, 220, 90, 30);
		
		thirdPanel.setLayout(null);
		chatLogBox.setBounds(30, 30, 340, 160);
		chatArea.setBounds(30, 220, 260, 30);
		sentchatBtn.setBounds(300, 220, 80, 30);	
	}
	
	private void addEventListner() {
		startBtn.addActionListener(this);
		makingRoomBtn.addActionListener(this);
		enterRoomBtn.addActionListener(this);
		exitRoomBtn.addActionListener(this);
		sentchatBtn.addActionListener(this);
		userList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		userList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int i = userList.getSelectedIndex();
				String reciver = users.get(i);
				String message = JOptionPane.showInputDialog("메세지를 입력하세요(userNick@메세지내용)");
				System.out.println(reciver);
				clientService.toMessage(reciver, message);
			}
		});
		
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		roomList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				int i = roomList.getSelectedIndex();
				String roomNumber = rooms.get(i);
				JOptionPane.showInputDialog( roomNumber +"에 입장합니다.");
				clientService.enterRoom(roomNumber);
				makingRoomBtn.setEnabled(false);
				roomList.setEnabled(false);
				jTabbedPane.addTab("chating", thirdPanel);
			}
		});
		
		
		
		chatArea.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String chat = chatArea.getText();
					//chatLogArea.append(chat + "\n");
					clientService.chat(chat);
					System.out.println("채팅 전송");
				}
			}
		});
		
	}
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource().equals(startBtn)) {
			try {
				String IP = userIPArea.getText();
				int portNumber = Integer.parseInt(portNumberArea.getText());
				String nickName = userNicNameArea.getText();
				
				loginFlag = clientService.login(IP, portNumber, nickName);
				if(loginFlag) {
					jTabbedPane.remove(0);
					jTabbedPane.addTab("waiting room", secondPanel); 
				}else {
					JOptionPane.showMessageDialog(null, "로그인 실패( 포트번호를 확인해주세요 )");
				}
				
			} catch (NumberFormatException e2) {
				JOptionPane.showMessageDialog(null, "잘못된 형식입니다.");
			}	
		}
		else if(e.getSource().equals(makingRoomBtn)) {
			System.out.println("방을 생성합니다.");
			String roomName = JOptionPane.showInputDialog("방이름을 입력하세요");
			clientService.createRoom(roomName);
			makingRoomBtn.setEnabled(false);
			roomList.setEnabled(false);
			jTabbedPane.addTab("chating", thirdPanel);
		}
		else if(e.getSource().equals(enterRoomBtn)) {
			System.out.println("방에 입장합니다.");
			makingRoomBtn.setEnabled(false);
			roomList.setEnabled(false);
			jTabbedPane.addTab("chating", thirdPanel);
		}
		else if(e.getSource().equals(exitRoomBtn)) {
			System.out.println("방을 나갑니다.");
			clientService.exitRoom();
			makingRoomBtn.setEnabled(true);
			roomList.setEnabled(true);
			jTabbedPane.remove(1);
		}else {
			String chat = chatArea.getText();
			//chatLogArea.append(chat + "\n");
			clientService.chat(chat);
			System.out.println("채팅 전송");
		}
	}
	
	
	public void showRoom() {
		
	}
	
	public void userUpdate(String user) {
		for (String name : users) {
			if(name.equals(user)) {
				return;
			}
		}
		users.add(user);
		userList.setListData(users);
	}
	
	public void roomUpdate(String roomNumber) {
		for (String room : rooms) {
			if(room.equals(roomNumber)) {
				return;
			}
		}
		rooms.add(roomNumber);
		roomList.setListData(rooms);
	}
	
	public void roomRemove(String roomNumber) {
		for (String room : rooms) {
			if(room.equals(roomNumber)) {
				rooms.remove(room);
			}
		}
		roomList.setListData(rooms);
	}

	
	public static void main(String[] args) {
		new ClientView();
	}


	


	
}
