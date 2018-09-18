package no.kristiania.prg200.http;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.BeforeClass;
import org.junit.Test;

public class HttpServerTest {

    private static HttpEchoServer server;
    private String path;
   

    @BeforeClass
    public static void startServer() throws IOException {
        server = new HttpEchoServer(10084);
        server.start();
    }
    

    @Test
    public void shouldWriteStatusCode() throws IOException {
    	setPath("/echo?status=200");
        HttpRequest request = new HttpRequest("localhost", server.getPort(), path);
        HttpResponse response = request.execute();
        
        System.out.println(server.getPort());

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
    
    @Test
    public void shouldReadOtherStatusCodes() throws IOException {
    	setPath("/echo?status=404");
        HttpRequest request = new HttpRequest("localhost", server.getPort(), path);
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(404);
    }

    @Test
    public void shouldReadResponseHeaders() throws IOException {
    	setPath("/echo?status=307&Location=http%3A%2F%2Fwww.kristiania.no");
        HttpRequest request = new HttpRequest("localhost", server.getPort(), path);
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(307);
        assertThat(response.getHeader("Location")).isEqualTo("http://www.kristiania.no");
    }

    @Test
    public void shouldReadResponseBody() throws IOException {
        HttpRequest request = new HttpRequest("localhost", server.getPort(),
                "/echo?body=Hello+world!");
        HttpResponse response = request.execute();

        assertThat(response.getStatusCode()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("Hello world!");
    }
    
    public void setPath(String path) {
    	this.path = path;
    }

    public static void main(String[] args) throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(10080)) {
            Socket socket = serverSocket.accept();
            socket.getOutputStream().write(("HTTP/1.1 200 OK\r\n").getBytes());
            socket.getOutputStream().write("Content-Type: text/html; charset=utf-8\r\n".getBytes());
            socket.getOutputStream().write(("Location: http://www.google.com\r\n").getBytes());
            socket.getOutputStream().write("Server: Kristiania Java Server!!\r\n".getBytes());
            socket.getOutputStream().write(("Content-Length: 13\r\n").getBytes());
            socket.getOutputStream().write("\r\n".getBytes());
            socket.getOutputStream().write("Hello world!\r\n".getBytes());
        }
    }

}
