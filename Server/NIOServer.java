import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
 
public class NIOServer {
 
	public static void main(String[] args) throws IOException {
 
		Selector selector = Selector.open(); // selector is open here
 
		ServerSocketChannel serverSocket = ServerSocketChannel.open();
		InetSocketAddress crunchifyAddr = new InetSocketAddress("localhost", 1111);
 
		serverSocket.bind(crunchifyAddr);
 
		serverSocket.configureBlocking(false);
 
		int ops = serverSocket.validOps();
		SelectionKey selectKy = serverSocket.register(selector, ops, null);
 
		while (true) {
 
			log("i'm a server and i'm waiting for new connection and buffer select...");
			selector.select();
 
			Set<SelectionKey> keys = selector.selectedKeys();
			Iterator<SelectionKey> iterator = keys.iterator();
 
			while (iterator.hasNext()) {
				SelectionKey myKey = iterator.next();
 
				if (myKey.isAcceptable()) {
					SocketChannel client = serverSocket.accept();
 
					client.configureBlocking(false);
 
					client.register(selector, SelectionKey.OP_READ);
					log("Connection Accepted: " + client.getLocalAddress() + "\n");
 
				} else if (myKey.isReadable()) {
					
					SocketChannel client = (SocketChannel) myKey.channel();
					ByteBuffer crunchifyBuffer = ByteBuffer.allocate(256);
					client.read(crunchifyBuffer);
					String result = new String(crunchifyBuffer.array()).trim();
 
					log("Message received: " + result);
 
					if (result.equals("Regaltusk")) {
						client.close();
						log("\nTime to close, we got last company name 'Regaltusk'");
						log("\nServer will keep running. Try running client again to establish new connection");
					}
				}
				iterator.remove();
			}
		}
	}
 
	private static void log(String str) {
		System.out.println(str);
	}
}

