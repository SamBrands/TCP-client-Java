package TcpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class TCPClient {

	//Data received event
	public List<DataReceivedHandler> DataReceived;
	
	private Socket _clientSocket;
	private BufferedReader _serverInput;
	private DataOutputStream _serverWriter; 
	
	private Thread ListenThread;
	private boolean Listen;
	
	public TCPClient()
	{
		DataReceived = new ArrayList<DataReceivedHandler>(); 
		Listen = true;
	}
	
	public void Connect(InetAddress address, int port) throws IOException, Exception
	{
		_clientSocket = new Socket(address, port);
		
		_serverInput = new BufferedReader(new InputStreamReader(_clientSocket.getInputStream()));
		_serverWriter = new DataOutputStream(_clientSocket.getOutputStream());
				
		ListenThread = new Thread(new Runnable() {
			
			public void run()
			{
				Listen(); 
			}
		});
		ListenThread.start();
	}
	
	public void Disconnect(DisconnectHandler disconectHandel) throws IOException
	{
		Listen = false; 
		disconectHandel.SendDisconnectToServer();
		_clientSocket.close();
	}
	
	public void Send(byte[] data) throws IOException
	{
		_serverWriter.write(data);	
	}
	
    private void Listen(int bufferSize)
    {
		char[] buffer = new char[bufferSize];
		
		while(Listen)
		{
			try {
				int bytesReceived = _serverInput.read(buffer);
				char[] dataReceived = new char[bytesReceived];
				System.arraycopy(buffer, 0, dataReceived, 0, bytesReceived);
				
				for (int i = 0; i < DataReceived.size(); i++) {
					DataReceived.get(i).DataRecieved(dataReceived);
				}
			} 
			catch (IOException e) {
				return; 
			}
		}
    }
    
    
}
