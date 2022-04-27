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
