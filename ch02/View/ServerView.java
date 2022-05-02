package ch02.View;

import java.awt.Font;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import ch02.Server.ServerService;
import ch02.Server.ServerServiceImpl;

public class ServerView extends JFrame implements ActionListener{
	
	private ServerService serverService;

	private ImagePanel bgImagePanel;
	private JTextArea logbox;
	private JTextField inputPortArea;
	private JLabel titleArea;
	private ScrollPane logArea;
	private JButton startButton;
	private JButton endButton;
	private JButton printLogButton;
	
	public ServerView() {
		this.serverService = new ServerServiceImpl(this);
		initObject();
		initLayout();
		addEventListner();
	}
	
	private void initObject(){
		bgImagePanel = new ImagePanel();
		logArea = new ScrollPane();
		inputPortArea = new JTextField("plz input port number");
		startButton = new JButton("Start");
		endButton = new JButton("End");
		printLogButton = new JButton("Print Log");
		titleArea = new JLabel("채팅 프로그램");
		logbox = new JTextArea();
		setContentPane(bgImagePanel);
		logArea.add(logbox);
		
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
	}
	
	private void initLayout() {
		setVisible(true);
		setSize(520, 500);
		setLayout(null);
		setResizable(false);
		
		logArea.setVisible(true);
		logbox.setEditable(false);
		endButton.setEnabled(false);
		printLogButton.setEnabled(false);
		startButton.setBounds(100, 390 ,80, 40);
		endButton.setBounds(320, 390 ,80, 40);
		printLogButton.setBounds(200, 390 , 100, 40);
		inputPortArea.setBounds(100, 340, 300, 40);
		logArea.setBounds(100, 70, 300, 250);
		logbox.setBounds(100, 70, 300, 250);
		titleArea.setBounds(150, 20, 300, 50);
		titleArea.setFont(new Font("d2coding", 1, 30));
		bgImagePanel.add(startButton);
		bgImagePanel.add(endButton);
		bgImagePanel.add(printLogButton);
		bgImagePanel.add(logArea);
		bgImagePanel.add(inputPortArea);
		bgImagePanel.add(titleArea);
		
		
		
	
	}
	
	private void addEventListner() {
		startButton.addActionListener(this);
		printLogButton.addActionListener(this);
		endButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(startButton)) {
			try {
				int portNumber = Integer.parseInt(inputPortArea.getText());
				if(portNumber > 1023 && portNumber < 20000) {
					Calendar calender = Calendar.getInstance();
					SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd");
					String date = f.format(calender.getTime());
					serverService.startNetwork(portNumber);
					serverService.writeMsg("service Start" + "(" + date + ")");
					serverService.writeMsg("------------------");
					startButton.setEnabled(false);
					printLogButton.setEnabled(true);
					endButton.setEnabled(true);
				}else {
					System.out.println("잘못된 번호입니다.");
				}
			} catch (NumberFormatException e2) {
				JOptionPane.showMessageDialog(null, "포트번호를 입력하세요");
			}
		}else if(e.getSource().equals(printLogButton)) {
			System.out.println("print Server Log");
			String totalLog = logbox.getText();
			serverService.printLog(totalLog);
			JOptionPane.showMessageDialog(null, "출력완료");
		}else if(e.getSource().equals(endButton)) {
			System.out.println("End program!!!!!");
			System.exit(0);
		}
		
	}
	
	public void showLog(String log) {
		logbox.append(log+"\n");
	}
}
