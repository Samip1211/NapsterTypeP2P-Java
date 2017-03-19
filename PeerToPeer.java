import java.util.*;
import java.net.*;
import java.io.*;
//The Main Server Class

class ClientRequestAndResponseInformation implements Serializable{
	int port;
	int getOtherClient;
	String[] filesPresent;
	String getFile;
	public ClientRequestAndResponseInformation(int id){
		this.port= id;
	}
	
}

class MainServer {
	List<ClientRequestAndResponseInformation> clientsInformation = new ArrayList<ClientRequestAndResponseInformation>();
	
	public synchronized int returnAddressForFile(ClientRequestAndResponseInformation client){
		for(int i=0;i<clientsInformation.size();i++){
			
			if(clientsInformation.get(i).port != client.port){
				
				if(Arrays.asList(clientsInformation.get(i).filesPresent).contains(client.getFile)){
					return  clientsInformation.get(i).port;
				}
			}else{
				return 4000;
			}
			
		}
		return 4000;
	}
	
	public synchronized void  sendToClient(OutputStream outPutStream,int clientAddress){
		try{
		 	ObjectOutputStream outStream = new ObjectOutputStream(outPutStream);
		 	outStream.writeObject(clientAddress);
		 	outStream.flush();
		 	System.out.println("Giving client"+ clientAddress);
		 	
		}catch(Exception e){
			System.out.println(e+ "Problem");
		}
	 	
	}
	
	public void makeServerSocket(int port){
		try{
			ServerSocket server = new ServerSocket(port); // Start the Server at port
			while(true){
				//Listen to client request till true "indefinetly"	
			Socket client = server.accept();
			//On accepting client request spawn a new Thread
			new Thread(){
				public synchronized void run(){
				 	try{
						 
						 ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            			 
						 ClientRequestAndResponseInformation clientRequestAndResponseInformation =(ClientRequestAndResponseInformation) in.readObject();	
						 
						 if(clientRequestAndResponseInformation.getOtherClient==1){
							 int addressToSend= returnAddressForFile(clientRequestAndResponseInformation);
							 sendToClient(client.getOutputStream(),addressToSend);
							 //server.close();
						 }else{
							 	addToArray(clientRequestAndResponseInformation);
								in.close();
						}
						
					}catch(Exception e){
					 	System.out.println(e+"Problem here"); //Thread Running error
				 	}
			 	}
		 	}.start(); //Start the thread
		}
		 
	 	}catch(Exception e){
	 		System.out.println(e); //Problem in creating Server Socket
		}
	}
	
	public synchronized void addToArray(ClientRequestAndResponseInformation clientRequestAndResponseInformation){
		clientsInformation.add(clientRequestAndResponseInformation);
		System.out.println(clientRequestAndResponseInformation.port);
	}
	
}
public class PeerToPeer{
	public static void main(String[] args){
		System.out.println("Hello from the Server");
		
		MainServer mainServer = new MainServer();
	
		mainServer.makeServerSocket(4000);
	}
	
	
}