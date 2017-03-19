import java.util.*;
import java.net.*;
import java.io.*;

class ClientRequestAndResponseInformation implements Serializable{
	int port;
	int getOtherClient; //0 means do not get other client address whereas 1 means to get other client address
	String[] filesPresent;
	String getFile;
	public ClientRequestAndResponseInformation(int id){
		this.port= id;
	}
	
}

class Send{
	
	public Send(){
		
	}
	
	
	public synchronized void sendObject(ClientRequestAndResponseInformation clientRequestAndResponseInformation){
		
		try{
			
			Socket client= new Socket("localhost",4000);
			OutputStream outToServer = client.getOutputStream();
	     	ObjectOutputStream out = new ObjectOutputStream(outToServer);
			clientRequestAndResponseInformation.getOtherClient=0;
			clientRequestAndResponseInformation.filesPresent = new String[] {"abc.txt", "xyz.txt" };
		 	out.writeObject(clientRequestAndResponseInformation);
			out.flush();
			out.close();
			
		}catch(Exception e){
			System.out.println(e+"Problem in sending");
			
		}
		
	}
	
	public synchronized int getOtherClientsAddress(ClientRequestAndResponseInformation clientRequestAndResponseInformation){
		try{
			clientRequestAndResponseInformation.getOtherClient=1;
			Socket client= new Socket("localhost",4000);
			OutputStream outToServer = client.getOutputStream();
	     	ObjectOutputStream out = new ObjectOutputStream(outToServer);
			clientRequestAndResponseInformation.getFile = "abc.txt";
			out.writeObject(clientRequestAndResponseInformation);
			out.flush();
			
			ObjectInputStream in = new ObjectInputStream(client.getInputStream());
			
			int otherPort= (int) in.readObject();
			
			in.close();
			return otherPort;
			
		}catch(Exception e){
			System.out.println(e+"Problem is here");
			return 0;
		}
	}
	
}


public class Clients {
	//Call this method when u want your client to act as server
	public static void makeServer(int port) {
		
		System.out.println("Listeing on port" + port);
		try{
			ServerSocket server= new ServerSocket(port);
				//Listen to client request till true "indefinetly"
			while(true){
				Socket threadClient= server.accept();
					//On accepting client request spawn a new Thread
				new Thread(){
					public synchronized void run(){
						try{
							
							DataInputStream in = new DataInputStream(threadClient.getInputStream());
							System.out.printf(in.readUTF());
							
							File myFile = new File("xyz.txt");
							byte[] mybytearray = new byte[(int) myFile.length()];
							BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
							bis.read(mybytearray, 0, mybytearray.length);
							
							OutputStream os = threadClient.getOutputStream();
							os.write(mybytearray, 0, mybytearray.length);
							os.flush();
							
				
						}catch(Exception e){
							System.out.println(e);
						}
					}
				
				}.start();
			}		
			
		
		
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	//Call this method when u want to connect to the other client
	public  static  void connectToClient(int serverPort){
		
		
		try{
			Socket client = new Socket("localhost", serverPort);
		    OutputStream outToServer = client.getOutputStream();
		    DataOutputStream out = new DataOutputStream(outToServer);
         
		    //out.writeUTF("Hello from " + client.getLocalSocketAddress()+ "to" + client.getRemoteSocketAddress()+ "on port" + serverPort +"\n"  );
			out.writeUTF(client.getLocalSocketAddress() + " requesting to " + serverPort + "for file" +"\n");
			
			byte[] mybytearray = new byte[6022386];
			InputStream is = client.getInputStream();
			FileOutputStream fos = new FileOutputStream("pqr.txt");
		    int bytesRead;
		    int current = 0;
			BufferedOutputStream bos = null;
			bos = new BufferedOutputStream(fos);
			bytesRead = is.read(mybytearray,0,mybytearray.length);
			do {
			        bos.write(mybytearray);
			        bytesRead = is.read(mybytearray);
			 } while (bytesRead != -1);
			
			bos.flush();
			
			client.close();
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void main(String[] args){
		try{
			//Create Multiple Clients
			for(int i=0;i<1;i++){
				
				new Thread(){
					
					public void run(){
						try{
							//Get random number and assign it as the port number of the client
							Random rm= new Random();
							
							int port = rm.nextInt(200) + 1;
							if(port < 1000){
								port+=4000;
							}
							
							
							//Create an object to send and initialize the object to facilitate that transport 
							ClientRequestAndResponseInformation clientRequestAndResponseInformation = new ClientRequestAndResponseInformation(port);
							Send send= new Send();
							
							//Description on the method
							send.sendObject(clientRequestAndResponseInformation);
							
							//Initialize the port as final so as to pass in the thread so as to make it run on different thread.
							final int finalPort = port;
							
							new Thread(){
								public void run(){
									makeServer(finalPort);
									
								}
							}.start();
							
							 
							//Sleep so that all the client get registered on the index server
							sleep(1000);
							System.out.println("After Making Server");
							//Get the port of other client.
							int serverPort = send.getOtherClientsAddress(clientRequestAndResponseInformation);
							System.out.println("Got from Server" + serverPort);
							if(serverPort == 0 || serverPort == 4000 ){
								System.out.println("No client Available");
							}else{
								connectToClient(serverPort);
							}	
							
						}catch(Exception e){
							System.out.println(e); //catch any error in the above process
							
						}
					}
				}.start();
			}
		}catch(Exception e){
			System.out.println(e);
		}
	}
}