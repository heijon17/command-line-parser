package no.kristiania.prg200.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;



public class HttpEchoServer {

	private int port;
	private int actualPort;

	public HttpEchoServer(int port) throws IOException {
		this.port = port;
	}

	public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		this.actualPort = serverSocket.getLocalPort();
		new Thread(() -> serverThread(serverSocket)).start();
	}
	
	public void serverThread(ServerSocket serverSocket) {
		while (true) {
			try {
				Socket clientSocket = serverSocket.accept();
				
				InputStream input = clientSocket.getInputStream();
				OutputStream output = clientSocket.getOutputStream();
				
				String line= readNextLine(input);
				while (!line.isEmpty()) {
					System.out.println(line);
					line = readNextLine(input);
				}
				
				String body = "Hello world\r\n";
				
				output.write(("HTTP/1.1 200 Ok\r\n").getBytes());
				output.write("X-Server-Name: Kristiania Web Server\r\n".getBytes());
				output.write("Connection: close\r\n".getBytes());
				output.write("Content-Type: text/plain\r\n".getBytes());
				output.write(("Content-Length: " + body.length() + "\r\n").getBytes());
				output.write("\r\n".getBytes());
				output.write(body.getBytes());
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static String readNextLine(InputStream input) throws IOException {
		StringBuilder nextLine = new StringBuilder();
		int c;
		while ((c = input.read()) != -1) {
			if (c == '\r') {
				input.read();
				break;
			}
			nextLine.append((char) c);
		}
		return nextLine.toString();
	}

	public int getPort() {
		return port;
	}

}
