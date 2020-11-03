import java.io.*;

import common.*;

public class ServerConsole implements ChatIF {

	EchoServer server;
	final public static int DEFAULT_PORT = 5555;
	
	public ServerConsole(int port){
		server = new EchoServer(port);
	}

 
	public void accept(){
		try{
			BufferedReader fromConsole = 
			new BufferedReader(new InputStreamReader(System.in));
			String msg;

			while (true){
				msg = fromConsole.readLine();
				server.handleMessageFromServerUI(msg);
			}
		} 
		catch (Exception ex) 
		{
			System.out.println
			("Error when reading the console.");
		}
	}
	public void display(String msg){
		System.out.println("> " + msg);
    }
}