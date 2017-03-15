import java.util.*;
import java.net.*;
import java.io.*;
//The Main Server Class
class Foo implements Serializable{
	int port;
	int getOther;
	
	public Foo(int id){
		this.port= id;
	}
}
class MainServer {
	List<Integer> clientsAddresses = new ArrayList<Integer>();
	
	public void makeServerSocket(int port){
		try{
			ServerSocket server = new ServerSocket(port); // Start the Server at port
			while(true){
				//Listen to client request till true "indefinetly"	
			Socket client = server.accept();
			//On accepting client request spawn a new Thread
			new Thread(){
				public void run(){
				 	try{
						 //System.out.println("Client Connected");
						 
						 ObjectInputStream in = new ObjectInputStream(client.getInputStream());
            			 
						 Foo foo =(Foo) in.readObject();
						 
						 //clientsAddresses.add(foo.port);
						 
						 //System.out.println(foo.port);	
						 
						 if(foo.getOther==1){
							 
							 for(int clientAddress:clientsAddresses){
								 if(clientAddress!=foo.port){
									 try{
										 ObjectOutputStream outStream = new ObjectOutputStream(client.getOutputStream());
										 outStream.writeObject(clientAddress);
										 //
										 outStream.flush();
										 
										 System.out.println("Giving client"+ clientAddress);
										 outStream.close();
										 in.close();
										 break;
									 }catch(Exception e){
										 System.out.println(e+ "Problem");
									 }
									 
								 }
							 }
						 }else{
								clientsAddresses.add(foo.port);
								System.out.println(foo.port);
								in.close();
						 }
						 
						 	
					 	
					}catch(Exception e){
					 	System.out.println(e); //Thread Running error
				 	}
			 	}
		 	}.start(); //Start the thread
		}
		 
	 	}catch(Exception e){
	 		System.out.println(e); //Problem in creating Server Socket
		}
	}
}
public class PeerToPeer{
	public static void main(String[] args){
		System.out.println("Hello from the Server");
		
		MainServer mainServer = new MainServer();
	
		mainServer.makeServerSocket(4000);
	}
	
	
}