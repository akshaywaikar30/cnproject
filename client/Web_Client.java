/*
Name:Akshay Mahesh Waikar
ID: 1001373973
Book: Computer Networking. A Top Down Approach. Fifth Edition by James F. Kurose, Keith W. Ross. Chapter 2.

https://www.youtube.com/watch?v=vCDrGJWqR8w

https://github.com/jyotisalitra/web-client-server

Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html

http://www.oracle.com/technetwork/java/socket-140484.html

*/
import java.io.BufferedReader;		//header names
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.time.*;
import java.util.GregorianCalendar;
public class Web_Client {

	public static void main(String[] args) {
		final String carFeed = "\r\n"; //returns the line feed
		final String line_separator = " "; //used as a line separator
		
		String Host1 = null;
		
		//initialses serverPort to 8080
		int sPort = 8080;
		
		String filePath = "/"; //initializes filePath with default file /
		
		//check command line arguments for server-host, port, and filePath
		//at least serverHost is required
		if(args.length == 1)
		{
			//first argument is serverHost
			Host1 = args[0];
		}
		else if (args.length == 2){
			//first argument is serverHost
			Host1 = args[0];
			
			//second can be either serverPort or filePath
			try {
				sPort = Integer.parseInt(args[1]); //check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]> Integer Port has not been provided. Default Server port will be used.");
				
				//then assume this string is filePath
				filePath = args[1];
			}
		}
		else if (args.length == 3){
			//first argument is serverHost
			Host1 = args[0];
			
			//second argument is serverPort
			try {
				sPort = Integer.parseInt(args[1]); //check if port is an integer
			}
			catch (NumberFormatException nfe)
			{
				System.err.println("[CLIENT]> Integer Port is not provided. Default Server port will be used.");
			}
			
			//third argument is fName
			filePath = args[2];
		}
		else
		{
			System.err.println("[CLIENT]> Not enough parameters provided. At least serverHost is required.");
			System.exit(-1);
		}
		
		System.out.println("[CLIENT]> Using Server Port: " + sPort);
		System.out.println("[CLIENT]> Using FilePath: " + filePath);
		
		//define a socket
		Socket socket = null;
		
		//define input and output streams
		BufferedReader socketInStream = null; //it reads data received over the socket's input Stream
		DataOutputStream socketOutStream = null; //it writes data over the socket's output Stream
		
		FileOutputStream fos = null; //writes content of the responded file in a file
		
		try {
			
			//get inet address of the serverHost
			InetAddress serverInet = InetAddress.getByName(Host1);
			
			//try to connect to the server
			socket = new Socket(serverInet, sPort);
			System.out.println("[CLIENT]> Connected to the server at " + Host1 + ":" + sPort);
			
			//get a reference to socket's inputStream
			socketInStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
			//get a reference to socket's outputStream
			socketOutStream = new DataOutputStream(socket.getOutputStream());

			//now send a HTTP GET request
			String requestLine = "GET" + line_separator + filePath + line_separator +"HTTP/1.0" + carFeed;
			System.out.println("[CLIENT]> Sending HTTP GET request: " + requestLine);
			//timer
			long start = new GregorianCalendar().getTimeInMillis();
			//send the requestLine
			socketOutStream.writeBytes(requestLine);
			
			//send an empty line
			socketOutStream.writeBytes(carFeed);
			
			//flush out output stream
			socketOutStream.flush();
			
			System.out.println("[CLIENT]> Waiting for a response from the server");
			//extract response Code
			String responseLine = socketInStream.readLine();
			System.out.println("[CLIENT]> Received HTTP Response with status line: " + responseLine);

			//extract content-type of the response
			String contentType = socketInStream.readLine();
			System.out.println("[CLIENT]> Received " + contentType);
			String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now()).toString();
			System.out.println("[CLIENT]> Date "+date);
			//read a blank line i.e. CRLF
			socketInStream.readLine();

			System.out.println("[CLIENT]> Received Response Body:");
			//start reading content body
			StringBuilder content = new StringBuilder();
			String res;
			while((res = socketInStream.readLine()) != null)
			{
				//save content to a buffer
				content.append(res + "\n");
				
				//print it as well
				System.out.println(res);
			}
			
			//get a name of the file from the response
			String fName = getfName(content.toString());
			long finish = new GregorianCalendar().getTimeInMillis();
			System.out.println("Ping RTT: " + (finish - start + "ms"));
			//open a outputstream to the fName
			//file will be created if it does not exist
			fos = new FileOutputStream(fName);
			
			fos.write(content.toString().getBytes());
			fos.flush();
			
			System.out.println("[CLIENT]> HTTP Response received. File Created: " + fName);
			
		}
			//catched all the exceptions
		catch (IllegalArgumentException iae) {
			System.err.println("[CLIENT]> EXCEPTION in connecting to the SERVER: " + iae.getMessage());
		}
		catch (IOException e) {
			System.err.println("[CLIENT]> ERROR " + e);
		}
		finally {
			try {
				//close all resources
				if (socketInStream != null) {
					socketInStream.close();
				}
				if (socketOutStream != null) {
					socketOutStream.close();
				}
				if (fos != null) {
					fos.close();
				}
				if (socket != null) {
					socket.close();
					System.out.println("[CLIENT]> Closing the Connection.");
				}
			} 
			//catches any exceptions
			catch (IOException e) {
				System.err.println("[CLIENT]> EXCEPTION in closing resource." + e);
			}
		}
	}

	private static String getfName(String content)
	{
		//default fName if <title> tag is empty
		String fName = "";
		
		fName = content.substring(content.indexOf("<title>")+("<title>").length(), content.indexOf("</title>"));
		
		if(fName.equals(""))
		{
			fName = "index";
		}
		
		fName = fName+".htm";
		
		return fName;
	}
}