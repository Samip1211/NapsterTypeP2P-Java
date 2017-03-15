import java.util.*;
import java.net.*;
import java.io.*;

class Foo implements Serializable{
	int port;
	int getOther; //0 means do not get other client address whereas 1 means to get other client address
	
	public Foo(int id){
		this.port= id;
	}
}

class Send{
	
	public Send(){
		
	}
	
	
	public synchronized void sendObject(int port,Foo foo){
		
		try{
			
			Socket client= new Socket("localhost",4000);
			OutputStream outToServer = client.getOutputStream();
	     	ObjectOutputStream out = new ObjectOutputStream(outToServer);
			foo.getOther=0;
		 	out.writeObject(foo);
			out.flush();
			out.close();
			
		}catch(Exception e){
			System.out.println(e);
			
		}
		
	}
	
	public int getOtherClients(Foo foo){
		try{
			Socket client= new Socket("localhost",4000);
			OutputStream outToServer = client.getOutputStream();
	     	ObjectOutputStream out = new ObjectOutputStream(outToServer);
			foo.getOther=1;
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


public class Client {
	
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
							
							Random rm= new Random();
							
							int port = rm.nextInt(50) + 1;
							if(port < 1000){
								port+=4000;
							}
							//Connect to the indexing server and send the data.
							//Socket client = new Socket("localhost",4000);
							Foo foo = new Foo(port);
							Send send= new Send();
							
							send.sendObject(port,foo);
							
							
							
							final int finalPort = port;
							
							new Thread(){
								public void run(){
									makeServer(finalPort);
								}
							}.start();
							
							System.out.println("After Making Server"); 
							sleep(1000);
							
							int clientPort = send.getOtherClients(foo);
							if(clientPort == 0){
								System.out.println("No client Available");
							}else{
								connectToClient(clientPort);
							}	
							
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
}