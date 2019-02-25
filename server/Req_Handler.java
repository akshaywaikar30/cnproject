/*
Name:Akshay Mahesh Waikar
ID: 1001373973
Book: Computer Networking. A Top Down Approach. Fifth Edition by James F. Kurose, Keith W. Ross. Chapter 2.

https://www.youtube.com/watch?v=vCDrGJWqR8w

https://github.com/jyotisalitra/web-client-server

Thread Tutorial from http://docs.oracle.com/javase/tutorial/essential/concurrency/runthread.html

http://www.oracle.com/technetwork/java/socket-140484.html

*/
import java.io.BufferedReader;		//headers names
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.time.*;


public class Req_Handler implements Runnable {

	private Socket C1_Socket; 
	private int C_ID; 

	private final String Carriage_Line = "\r\n"; 
	private final String Seperator_tab = " "; 
	
	//constructor with parameters.
	public Req_Handler(Socket Socket, int C_ID) {
		this.C1_Socket = Socket;
		this.C_ID = C_ID;
	}

	private String GetErrorMsg ()  //if the file is not present in the server, it throws this error message
	{
		String Error_Msg = 	"<!doctype html>\n" +
									"<html lang=\"en\">\n" +
									"<head>\n" +
									"    <meta charset=\"UTF-8\">\n" +
									"    <title>Error 404</title>\n" +
									"</head>\n" +
									"<body>\n" +
									"    <b>ErrorCode:</b> 404\n" +
									"    <br>\n" +
									"    <b>Error Message:</b> The requested file does not exist on this server. \n" +
									"</body>\n" +
									"</html>";
		return Error_Msg;
	}
	public void run() {
		
		//define input and output streams
		BufferedReader soc_In_Stream = null; 
		DataOutputStream soc_Out_Stream = null; 
		
		FileInputStream R_file = null; 
		
		try {
			//obtaining a reference to clientSocket's inputStream
			soc_In_Stream = new BufferedReader(new InputStreamReader(C1_Socket.getInputStream()));
			
			//obtaining a reference to clientSocket's outputStream
			soc_Out_Stream = new DataOutputStream(C1_Socket.getOutputStream());

			//reading a request from socket inputStream
			String packet = soc_In_Stream.readLine();
			
			
			if(packet != null)
			{
				System.out.println("[SERVER - CLIENT"+C_ID+"]> Request Received: " + packet); //tells if the packet not null then request recieved
			String date=java.time.format.DateTimeFormatter.RFC_1123_DATE_TIME.format(ZonedDateTime.now(ZoneOffset.systemDefault())).toString();
			System.out.println("[SERVER - CLIENT"+C_ID+"]> Date "+date);
				
				String[] msg = packet.split(Seperator_tab);
				
				if (msg.length == 3&& msg[0].equals("GET")) {
					
					
					String FPath = msg[1];
					
					 
					if(FPath.indexOf("/") != 0)
					{	
						FPath = "/" + FPath;
					}
					
					
					System.out.println("[SERVER - CLIENT"+C_ID+"]> Requested filePath: " + FPath);
					
					if(FPath.equals("/"))
					{
						System.out.println("[SERVER - CLIENT"+C_ID+"]> Response with index.html file");
						
						//set filePath to the default index.htm file
						FPath = FPath + "index.html";
					}
					
					FPath = "." + FPath;

					File file = new File(FPath);
					try {
						if (file.isFile() && file.exists()) {
							
							
							String responseLine = "HTTP/1.0" + Seperator_tab + "200" + Seperator_tab + "OK" + Carriage_Line ;
							soc_Out_Stream.writeBytes(responseLine);

							soc_Out_Stream.writeBytes("Content-type: " + getFileType(FPath) + Carriage_Line );
							
							soc_Out_Stream.writeBytes(Carriage_Line);
							
							R_file = new FileInputStream(file);

							byte[] buffer = new byte[1024];
							int bytes = 0;
							
							while((bytes = R_file.read(buffer)) != -1 ) {
								soc_Out_Stream.write(buffer, 0, bytes);
							}
							
							System.out.println("[SERVER - CLIENT"+C_ID+"]> Sending Response with status line: " + responseLine);                
							soc_Out_Stream.flush();
							System.out.println("[SERVER - CLIENT"+C_ID+"]> HTTP Response sent");
							
						} else {
							System.out.println("[SERVER - CLIENT"+C_ID+"]> ERROR: Requested filePath " + FPath + " does not exist");
							String responseLine = "HTTP/1.0" + Seperator_tab + "404" + Seperator_tab + "Not Found" + Carriage_Line ;
							soc_Out_Stream.writeBytes(responseLine);
							soc_Out_Stream.writeBytes("Content-type: text/html" + Carriage_Line );
							soc_Out_Stream.writeBytes(Carriage_Line );
							soc_Out_Stream.writeBytes(GetErrorMsg());
							System.out.println("[SERVER - CLIENT"+C_ID+"]> Sending Response with status line: " + responseLine);
							soc_Out_Stream.flush();
							System.out.println("[SERVER - CLIENT"+C_ID+"]> HTTP Response sent");
						}
						
					} catch (FileNotFoundException e) {
						System.err.println("[SERVER - CLIENT"+C_ID+"]> EXCEPTION: Requested filePath " + FPath + " does not exist");
					} catch (IOException e) {
						System.err.println("[SERVER - CLIENT"+C_ID+"]> EXCEPTION in processing request." + e.getMessage());
					}
				} else {
					System.out.println("[SERVER - CLIENT"+C_ID+"]> Invalid HTTP GET Request. " + msg[0]);
				}
			}
			else
			{
				
				System.out.println("[SERVER - CLIENT"+C_ID+"]> Discarding unknown HTTP request.");
			}

		} catch (IOException e) 
		{
			System.err.println("[SERVER - CLIENT"+C_ID+"]> EXCEPTION in processing request." + e.getMessage());
			
		} finally {
			//resources being closed
			try {
				if (R_file != null) {
					R_file.close();
				}
				if (soc_In_Stream != null) {
					soc_In_Stream.close();
				}
				if (soc_Out_Stream != null) {
					soc_Out_Stream.close();
				}
				if (C1_Socket != null) {
					C1_Socket.close();
					System.out.println("[SERVER - CLIENT"+C_ID+"]> Closing the connection.\n");
				}
			} catch (IOException e) {
				System.out.println("[SERVER - CLIENT"+C_ID+"]> EXCEPTION in closing resource." + e);
			}
		}
	}
	
		private String getFileType(String filePath)
	{
		//check if the file is HTML
		if(filePath.endsWith(".html") || filePath.endsWith(".html"))
		{
			return "text/html";
		}
		//otherwise, a binary file
		return "application/octet-stream";
	}
	
	
}