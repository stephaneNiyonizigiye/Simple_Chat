// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 



import java.io.IOException;

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
 
	ChatIF serverUI;
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port,ChatIF serverUI) 
  {
    super(port);
    this.serverUI=serverUI;
    
  }

  
  //Instance methods ************************************************
  
  /**
   * This method handles any messages received from the client.
   *
   * @param msg The message received from the client.
   * @param client The connection from which the message originated.
   */
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	if (String.valueOf(msg).startsWith("#login")) {
		
		if (client.getInfo("id")==null) {
			 String tab[]=new String[4];
			 String message= String.valueOf(msg);
			 tab=message.split("<");
			 String login = tab[1].substring(0,tab[1].length()-1);
			 client.setInfo("id", login);
		 	}
		else {
			try {
				client.sendToClient("error : id was  already set , connection termination!!");
				client.close();
			} catch (IOException e) {
				
			serverUI.display("failed to close connection with client");
			}	
		}
		}
	
	else {
    serverUI.display("Message received: " + msg + " from " +client.getInfo("id"));
    this.sendToAllClients(client.getInfo("id")+": "+msg);
	}
  }
  

  
  //traitement d entree de la console serveur 
  public void handleMessageFromUI
  (String msg) throws IOException
{ if (msg.startsWith("#")) {
				command(msg.substring(1));
				
}

else
  this.sendToAllClients("SERVERMSG>"+msg);
}
  
  //gestion des commandes 
 public void command (String msg) throws IOException {
	 if (msg=="#quit") {
		 System.exit(0);
	 }
	 else if (msg.equals("stop")) {
		 if (this.isListening()) {
			 			this.stopListening();
		 }
		 else
			 serverUI.display("the server is already offline");
	 }
	 else if (msg.equals("close")) {
		 
		 this.close();
		 serverUI.display("server closed");
	 }
	 else if (msg.equals("start")) {
		 if (this.isListening()==false)
			 							this.listen();
		 else
			 serverUI.display("server is already listening forconnections");
	 }
	 else if (msg.equals("getport")) {
		 serverUI.display(String.valueOf(getPort()));
	 }
	 else if (msg.startsWith("setport")) {
		 String tab[]=new String[4];
		 tab=msg.split("<");
		 String port = tab[1].substring(0,tab[1].length()-1);
		 setPort(Integer.parseInt(port));
		 serverUI.display("new port : "+ getPort());
	 }
	 else
		 serverUI.display("command not found");
 }
    
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    serverUI.display
      ("Server has stopped listening for connections.");
  }
  
  /**Question 1.C
   * Hook method called each time a new client connection is
   * accepted. The default implementation does nothing.
   * @param client the connection connected to the client.
   */
  protected void clientConnected(ConnectionToClient client) {
	  serverUI.display
      ("connnection received from "+client.toString());
  }

  /**Question 1.C
   * Hook method called each time a client disconnects.
   * The default implementation does nothing. The method
   * may be overridden by subclasses but should remains synchronized.
   *
   * @param client the connection with the client.
   */
  synchronized protected void clientDisconnected(
    ConnectionToClient client) {
	  System.out.println(client.toString()+" is disconnected");
  }

  /**Question 1.C
   * Hook method called each time an exception is thrown in a
   * ConnectionToClient thread.
   * The method may be overridden by subclasses but should remains
   * synchronized.
   *
   * @param client the client that raised the exception.
   * @param Throwable the exception thrown.
   */
  synchronized protected void clientException(
    ConnectionToClient client, Throwable exception) {
	  
	  serverUI.display("connection interupted");
	  try {
		  client.close();
	  }
	  catch (Exception e){
		  serverUI.display("connnection closure failed");
	  }
  }
}

  
 
//End of EchoServer class
