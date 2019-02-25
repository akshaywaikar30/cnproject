/*
Name:Akshay Mahesh Waikar
ID: 1001373973
Book: Computer Networking. A Top Down Approach. Fifth Edition by James F. Kurose, Keith W. Ross. Chapter 2.

https://www.youtube.com/watch?v=vCDrGJWqR8w

https://github.com/jyotisalitra/web-client-server

Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html

http://www.oracle.com/technetwork/java/socket-140484.html

*/
import java.io.IOException; 	//header names
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.GregorianCalendar;

public class Web_Server implements Runnable {

	private ServerSocket S_Socket;
	private String S_Host; 
	private int Port; 
	private final String DestHost = "localhost";
	private final int DPort = 8080;
	public Web_Server ()  //non parametric constructor
	{
		this.S_Host = DestHost;
		this.Port = DPort; 
	}
	//Parameterized constructor- receives parameters like host and port number.
	public Web_Server (String mainHost, int port)
	{
		this.S_Host = mainHost; 
		this.Port = port; 
	}
	//Parameterized constructor: receives parameters like port number.
	public Web_Server (int port)
	{
		this.S_Host = DestHost; 
		this.Port = port; 
	}
	public void run() {
		
		try {

			InetAddress ServerInet = InetAddress.getByName(S_Host);
			S_Socket = new ServerSocket(Port, 0, ServerInet);

			System.out.println("[SERVER REPLY]> SERVER started at host: " + S_Socket.getInetAddress() + " port: " + S_Socket.getLocalPort() + "\n");
			int clientID=0;
			//multithreaded server
			while(true){
				
				//wait for a client to get connected
				Socket C_Socket = S_Socket.accept();
				
				//a new client has connected to this server
				System.out.println("[SERVER - CLIENT"+clientID+"]> Connection established with the client at " + C_Socket.getInetAddress() + ":" + C_Socket.getPort());
				
				//pass clientSocket and clientID to RequestHandler object
				Req_Handler R = new Req_Handler(C_Socket, clientID);
				
				//handover processing for the newly connected client to RequestHandler in a separate thread
				new Thread(R).start();
				
				//increment clientID for the next client;
				clientID++;
			}
			
		} catch (UnknownHostException e) {
			System.out.println("[SERVER REPLY]> UnknownHostException for the hostname: " + S_Host);
		} catch (IllegalArgumentException ie) {
			System.out.println("[SERVER REPLY]> EXCEPTION in starting the SERVER: " + ie.getMessage());
		}
		catch (IOException ex) {
			System.out.println("[SERVER REPLY]> EXCEPTION in starting the SERVER: " + ex.getMessage());
		}
		finally {
				try {
					if(S_Socket != null){
						S_Socket.close();
					}
				} catch (IOException e1) {
					System.err.println("[SERVER REPLY]> EXCEPTION in closing the server socket." + e1);
				}
		}
	}
}