import java.io.IOException;
import java.net.Socket;
import java.util.Optional;

public class PeerClient {
    public Optional<Socket> connect(String ip, int port) {
        try {
            Socket socket = new Socket(ip, port);
            System.out.println("[접속 성공] " + ip + ":" + port);
            return Optional.of(socket);
        } catch (Exception e) { // IOException -> Exception 수정
            System.out.println("접속 실패: " + e.getMessage());
            return Optional.empty();
        }
    }
}
