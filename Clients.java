import java.util.*;
import java.net.*;
import java.io.*;

class ClientRequestAndResponseInformation implements Serializable{
	int port;
	int getOtherClient; //0 means do not get other client address whereas 1 means to get other client address
	
	public ClientRequestAndResponseInformation(int id){
		this.port= id;
	}
}

class Send{
	
	public Send(){
		
	}
	
	
	public synchronized void sendObject(int port,ClientRequestAndResponseInformation clientRequestAndResponseInformation){
		
		try{
			
			Socket client= new Socket("localhost",4000);
			OutputStream outToServer = client.getOutputStream();
	     	ObjectOutputStream out = new ObjectOutputStream(outToServer);
			clientRequestAndResponseInformation.getOtherClient=0;
		 	out.writeObject(foo);
			out.flush();
			out.close();
			
		}catch(Exception e){
			System.out.println(e);
			
		}
		
	}
	
	public synchronized int getOtherClientsAddress(ClientRequestAndResponseInformation clientRequestAndResponseInformation){
		try{
			clientRequestAndResponseInformation.getOtherClient=1;
			Socket client= new Socket("localhost",4000);
			OutputStream outToServer = client.getOutputStream();
	     	ObjectOutputStream out = new ObjectOutputStream(outToServer);
			out.writeObject(foo);
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
					public void run(){
						try{
						
							DataInputStream in = new DataInputStream(threadClient.getInputStream());
				
							System.out.printf(in.readUTF());
						
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
	public  static  void connectToClient(int clientPort){
		
		
		try{
			Socket client = new Socket("localhost", clientPort);
		    OutputStream outToServer = client.getOutputStream();
		    DataOutputStream out = new DataOutputStream(outToServer);
         
		    out.writeUTF("Hello from " + client.getLocalSocketAddress()+ "to" + client.getRemoteSocketAddress()+ "on port" + clientPort +"\n"  );
		}catch(Exception e){
			System.out.println(e);
		}
	}
	
	public static void main(String[] args){
		try{
			//Create Multiple Clients
			for(int i=0;i<3;i++){
				
				new Thread(){
					
					public void run(){
						try{
							//Get random number and assign it as the port number of the client
							Random rm= new Random();
							
							int port = rm.nextInt(50) + 1;
							if(port < 1000){
								port+=4000;
							}
							
							
							//Create an object to send and initialize the object to facilitate that transport 
							ClientRequestAndResponseInformation clientRequestAndResponseInformation = new ClientRequestAndResponseInformation(port);
							Send send= new Send();
							
							//Description on the method
							send.sendObject(port,clientRequestAndResponseInformation);
							
							//Initialize the port as final so as to pass in the thread so as to make it run on different thread.
							final int finalPort = port;
							
							new Thread(){
								public void run(){
									makeServer(finalPort);
								}
							}.start();
							
							System.out.println("After Making Server"); 
							//Sleep so that all the client get registered on the index server
							sleep(1000);
							
							//Get the port of other client.
							int clientPort = send.getOtherClientsAddress(clientRequestAndResponseInformation);
							
							if(clientPort == 0){
								System.out.println("No client Available");
							}else{
								connectToClient(clientPort);
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