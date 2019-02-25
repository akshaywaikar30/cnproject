/*
Name:Akshay Mahesh Waikar
ID: 1001373973
Book: Computer Networking. A Top Down Approach. Fifth Edition by James F. Kurose, Keith W. Ross. Chapter 2.

https://www.youtube.com/watch?v=vCDrGJWqR8w

https://github.com/jyotisalitra/web-client-server

Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html

http://www.oracle.com/technetwork/java/socket-140484.html

*/
public class S_Initializer { //this initialiazes the server if the port number is right. 

	
	public static void main(String[] args) {

		int port = 8080;
		
		if(args.length == 1)
		{
			try {
				port = Integer.parseInt(args[0]);
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[SERVER REPLY]> Integer Port is not provided. Server will start at default port.");
			}
		}

		System.out.println("[SERVER REPLY]> Using Server Port : " + port);
		
		Web_Server Server = new Web_Server(port);
		
		new Thread(Server).start(); //starts a new thread
	}
}
