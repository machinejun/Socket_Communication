package ch02.Server;

import java.util.Vector;

public class ServerData {
	private static ServerData instance;
	private static Vector<User> userlist;
	private static Vector<InnerRoom> roomlist;
	
	public static ServerData getinstance() {
		if(instance == null) {
			instance = new ServerData();
		}
		return instance;
	}
	
	private ServerData() {
		userlist = new Vector<User>();
		roomlist = new Vector<InnerRoom>();
	}

	public Vector<User> getUserlist() {
		return userlist;
	}

	public void setUserlist(Vector<User> userlist) {
		this.userlist = userlist;
	}

	public Vector<InnerRoom> getRoomlist() {
		return roomlist;
	}

	public void setRoomlist(Vector<InnerRoom> roomlist) {
		this.roomlist = roomlist;
	}
	
	/**
	 * 해당 닉네임을 가진 유저를 찾는 기능
	 * 
	 * @param nickName
	 * @return 해당 nickName의 유저
	 */
	public User checkUserNick(String nickName) {
		User checkUser = null;
		for (User user : userlist) {
			if(user.getNickName().equals(nickName)) {
				checkUser = user;
				break;
			}
		}
		return checkUser;
	}
	
	/**
	 * 해당 룸넘버를 가진 InnerRoom을 찾는 기능
	 * 
	 * @param roomNumber
	 * @return 해당 roomNumber을 가진 InnerRoom
	 */
	public InnerRoom checkRoonNum(String roomNumber) {
		InnerRoom checkRoom = null;
		for (InnerRoom innerRoom : roomlist) {
			if(innerRoom.getRoomName().equals(roomNumber)) {
				checkRoom = innerRoom;
				break;
			}
		}
		return checkRoom;
	}

}
