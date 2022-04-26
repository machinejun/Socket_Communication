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
	
	
	
	
		
}
