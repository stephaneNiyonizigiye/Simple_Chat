// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package client;

import ocsf.client.*;
import common.*;
import java.io.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 * @version July 2000
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  String id;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  public ChatClient(String id,String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    this.clientUI = clientUI;
    openConnection();
    this.id=id;
  }
  
  //accesseur 
  public String getId() {return this.id;}


  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
  }

  /** Question 2
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
 * @throws IOException 
   */
  public void handleMessageFromClientUI(String message) throws IOException
  {
	  if (message.startsWith("#"))
		  command(message.substring(1));
	  else {
    try
    {
      sendToServer(message);
    }
    catch(IOException e)
    {
      clientUI.display
        ("Could not send message to server.  Terminating client.");
      quit();
    }
	  }
  }
  
  //  Question 2 A  traitement des commandes tapees dans la ligne de commande
 public void command(String command) throws IOException {
	 if (command.equals("quit"))
	 {
		 try {
			this.quit();
		 }
		 catch(Exception e) {
			 clientUI.display("failed to close connection");
		 }
		
	 }
	 else if (command.equals("logoff")) {
		 
		 try {
			 if(this.isConnected()) {
			 this.closeConnection();
			 clientUI.display("you are logged off");
			 }
			 else
				 clientUI.display("you are already offline");
		 }
		 catch (Exception e) {
			 clientUI.display("failed to logoff");
		 }
	 }
	 else if (command.startsWith("setport")) {
		 String tab[]=new String[4];
		 tab=command.split("<");
		 String port = tab[1].substring(0,tab[1].length()-1);
		 setPort(Integer.parseInt(port));
		 clientUI.display("new port : "+ getPort());
	 }
	 else if (command.startsWith("sethost")) {
		 String tab[]=new String[4];
		 tab=command.split("<");
		 String host = tab[1].substring(0,tab[1].length()-1);
		 setHost(host);
		 clientUI.display("new Host : "+ getHost());
	 }
	 else  if (command.contains("login")) {
		 //situation ou c une commande a envoyer au serveur 
		 if( command.contains("<"))
			 this.sendToServer("#"+command);
		 
		 //le cas ou c une commande local
		 else {
			 
			 if(this.isConnected()) {
				 clientUI.display("you are already connected");
			 }
		 else
			 try {
				 this.openConnection();
			 }
		 	catch(Exception e) {
		 		clientUI.display("can't connect");
		 	}
	 }
	 }
	 else if (command.equals("gethost")){
		 clientUI.display("host : "+getHost());
	 }
	 else if(command.equals("getport")) {
		 clientUI.display("port : "+String.valueOf(getPort()));
	 }
	 	
 }
  
  /**
   * This method terminates the client.
   */
  public void quit()
  {
    try
    {
      closeConnection();
    }
    catch(IOException e) {}
    System.exit(0);
  }
  
  
  /** Question 1.A
	 * Hook method called after the connection has been closed. The default
	 * implementation does nothing. The method may be overriden by subclasses to
	 * perform special processing such as cleaning up and terminating, or
	 * attempting to reconnect.
	 */
  @Override
	protected void connectionClosed() {
		System.out.println(">"+ "connection to the server has been closed");
	}
	/** Question 1.A
	 * Hook method called each time an exception is thrown by the client's
	 * thread that is waiting for messages from the server. The method may be
	 * overridden by subclasses.
	 * 
	 * @param exception
	 *            the exception raised.
	 */
  @Override
	protected void connectionException(Exception exception) {
		System.out.println(exception);
		System.out.println("server disconnected");
		System.exit(0);
	}
  /**
	 * Hook method called after a connection has been established. The default
	 * implementation does nothing. It may be overridden by subclasses to do
	 * anything they wish.
	 */
	protected void connectionEstablished() {
		try {
			this.sendToServer("#login<"+this.getId()+">");
			clientUI.display ("your Id is "+ getId());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


}

//End of ChatClient class
