// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

import java.io.*;

import common.ChatIF;
import ocsf.server.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 * @version July 2000
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient(Object msg, ConnectionToClient client){
	  if((boolean)(client.getInfo("isFirstMessage"))){	
			String[] splittedMessage = ((String)msg).split(" ", 2);
			client.setInfo("isFirstMessage", false);
			if(splittedMessage[0].equals("#login")){
				client.setInfo("loginID", splittedMessage[1]);
			}
			else{
				try{
					client.sendToClient("First Login Please");
					client.close();
				}
				catch(IOException e){
				}
			}
		}
		else{
			if(((String)msg).startsWith("#login")){
				try{
					client.sendToClient("Already Logged in");
				}
				catch(IOException e){
				}
			}
		    System.out.println("Message received: " + msg + " from " + client.getInfo("loginID"));
		    this.sendToAllClients((String)client.getInfo("loginID")+ ">"+ msg);
		}
  }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  boolean serverClosed;
  
  protected void serverStarted(){
    System.out.println("Server listening for connections on port " + getPort());
    
    serverClosed = false;
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped(){
    System.out.println("Server has stopped listening for connections.");
    
    serverClosed = true;
  }
  
  protected void clientConnected(ConnectionToClient client){
	  System.out.println("Client : "+ client + " has connected.");
	  
	  client.setInfo("isFirstMessage", true);
  }

  /**
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(ConnectionToClient client){
	  System.out.println("Client : " +client+ " has disconnected");
  }
  
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
	  clientDisconnected(client);
  }
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  ChatIF serverUI;
  
  public EchoServer(int port,ChatIF serverUI){
    super(port);
    this.serverUI = serverUI;
  }
  public void handleMessageFromServerUI(String msg){
	  if(msg.charAt(0)=='#'){
			try{
				handleServerCommands(msg);
			}
			
			catch(IOException e){
				System.out.println(e);
			}
		}
	  else{
		  serverUI.display("SERVER MSG>" + msg);
		  sendToAllClients("SERVER MSG>" + msg);
	  }
  }

  
  private void handleServerCommands(String msg) throws IOException{
	  //create string array to handle setHost and setPort
	  String[] splitMsg = msg.split(" ", 2);
	  
	  switch (splitMsg[0]){ 
	  	  case "#quit":
	  		System.exit(0);
	  	  	break;	
	  	  	
		  case "#stop":
			stopListening();
		  	break;
		  	
		  case "#close":
		    close();
		  	break;
		  	
		  case "#setport":
		  	if(serverClosed) {
		  		setPort(Integer.parseInt(splitMsg[1].replace("<", "").replace(">", "")));
		  	}
			else{
				throw new IOException("Close server before creating port.");
			}
		  	break;
		  	
		  case "#start":
			if(!isListening()) {
				listen();
		  	}
			else{
				throw new IOException("Server is listening");
			}
			break;
			
		  case "#getport":
			  serverUI.display("Port: "+ getPort());
			  break;
			  
		  default:
			  throw new IOException("Command not Valid."); 
		  	
	  }
  }


public static void main(String[] args){
    int port = 0; //Port to listen on

    try{
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t){
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try{
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex){
      System.out.println("ERROR - Could not listen for clients!");
    }
  }
}
//End of EchoServer class
