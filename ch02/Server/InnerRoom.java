package ch02.Server;

import java.util.Vector;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InnerRoom {
	private String roomName;
	private Vector<User> roomUser;
	
	public InnerRoom(String roomName, User user) {

		this.roomName = roomName;
		this.roomUser = new Vector<User>();
		roomUser.add(user);
	}
	
	/**
	 * 해당방에 들어있는 유저를 검색하는 기능
	 * 
	 * @param nickName
	 * @return
	 */
	public User checkRoomUser(String nickName) {
		User checkUser = null;
		for (User user : roomUser) {
			if(user.getNickName().equals(nickName)) {
				checkUser = user;
				break;
			}
		}
		return checkUser;
	}
	
	
		
}
