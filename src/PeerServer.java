import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Optional;

public class PeerServer {
    private final int port;

    private PeerServer(int port) {
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("포트 번호는 1부터 65535 사이여야 합니다.");
        }
        this.port = port;
    }

    public Optional<Socket> waitClient() {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("[서버 대기] PORT: " + port);
            Socket socket = serverSocket.accept();
            System.out.println("[연결됨] " + socket.getInetAddress().getHostAddress());
            return Optional.of(socket);
        } catch (IOException e) {
            System.out.println("서버 오류: " + e.getMessage());
            return Optional.empty();
        }
    }

    public static PeerServer defaultPeerServer() {
        return new PeerServer(Config.getInstance().getPort());
    }

    public static PeerServer changePortPeerServer(int port) {
        return new PeerServer(port);
    }
}