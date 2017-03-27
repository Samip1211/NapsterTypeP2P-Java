import java.util.*;
import java.net.*;
import java.io.*;
//The Main Server Class

//Object describing the client which is stored in server
class ClientRequestAndResponseInformation implements Serializable{
	int port; // Port of the client
	int getOtherClient; //0 means do not get other client address whereas 1 means to get other client address
	List<String> filesPresent;
	String getFile;
	public ClientRequestAndResponseInformation(int id){
		this.port= id;
		filesPresent = new ArrayList<String>();
		File folder = new File(System.getProperty("user.dir"));
		File[] listOfFiles = folder.listFiles();
		
		//Get the files present in the directory
		for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
				  filesPresent.add(listOfFiles[i].getName());
		        //System.out.println("File " + listOfFiles[i].getName());
		      } else if (listOfFiles[i].isDirectory()) {
		        //System.out.println("Directory " + listOfFiles[i].getName());
		      }
		    }
	}
	
}

class MainServer {
	List<ClientRequestAndResponseInformation> clientsInformation = new ArrayList<ClientRequestAndResponseInformation>();
	
	//Method called when one client asks for the information of other client
	public synchronized int returnAddressForFile(ClientRequestAndResponseInformation client){
		//Use the simple search algorithm to search for the neccessary file
		for(int i=0;i<clientsInformation.size();i++){
			
			if(clientsInformation.get(i).port != client.port){
				
				for(String fileName : clientsInformation.get(i).filesPresent){
					if(fileName.equals(client.getFile)){
						return clientsInformation.get(i).port;
					}
				}
			}
		}
		return 4000;
	}
	
	//Method called so as to call the other client
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
							 System.out.println("Got from client " + clientRequestAndResponseInformation.getFile );
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
		System.out.println(clientRequestAndResponseInformation.filesPresent);
	}
	
}
public class PeerToPeer{
	public static void main(String[] args){
		System.out.println("Hello from the Server");
		
		MainServer mainServer = new MainServer();
	
		mainServer.makeServerSocket(4000);
	}
	
	
}