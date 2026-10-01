import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MessageHandler {
    private final Socket socket;
    private final Scanner in;
    private final ConsoleUI ui;
    private final UserProfile profile;
    private final MessageDTO messageDTO = MessageDTO.getInstance();
    private final BufferedReader reader;
    private final PrintWriter writer;

    public MessageHandler(Socket socket, Scanner in, ConsoleUI ui, UserProfile profile) throws IOException {
        if (socket == null || in == null || ui == null || profile == null) {
            throw new IllegalArgumentException("채팅 실행에 필요한 값이 없습니다.");
        }

        this.socket = socket;
        this.in = in;
        this.ui = ui;
        this.profile = profile;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
        this.writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
    }

    public void start() throws IOException {
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        try {
            executor.submit(new ReceiveTask());
            sendLoop();
        } finally {
            socket.close();
            executor.shutdownNow();
        }
    }

    private class ReceiveTask implements Runnable {
        @Override
        public void run() {
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String packet = XorCipher.decrypt(XorCipher.fromHex(line));
                    MessageDTO.Message message = MessageDTO.Message.fromPacket(packet);
                    messageDTO.addMessage(message);

                    ui.showMessage(message, false);
                    ui.showPrompt();
                }

                if (!socket.isClosed()) {
                    ui.showNotice("상대방이 채팅을 종료했습니다.");
                    socket.close();
                }
            } catch (IOException e) {
                if (!socket.isClosed()) {
                    ui.showNotice("상대방과의 연결이 종료되었습니다.");
                }
            } catch (IllegalArgumentException e) {
                ui.showError("메시지 변환 오류: " + e.getMessage());
            }
        }
    }

    private void sendLoop() {
        while (!socket.isClosed()) {
            ui.showPrompt();
            String content = in.nextLine().trim();

            if (socket.isClosed()) {
                break;
            }
            if (content.isEmpty()) {
                continue;
            }
            if (handleCommand(content)) {
                if ("/quit".equals(content)) {
                    break;
                }
                continue;
            }

            MessageDTO.Message message = new MessageDTO.Message(profile.getNickname(), content);
            messageDTO.addMessage(message);

            String enc = XorCipher.toHex(XorCipher.encrypt(message.toPacket()));
            writer.println(enc);
            ui.showMessage(message, true);
        }
    }

    private boolean handleCommand(String content) {
        switch (content) {
            case "/help" -> ui.showHelp();
            case "/history" -> ui.showHistory(messageDTO.getMessages(), profile.getNickname());
            case "/info" -> ui.showConnectionInfo(socket, profile.getNickname());
            case "/clear" -> {
                ui.clear();
                ui.showConnected(socket, profile.getNickname());
            }
            case "/quit" -> ui.showNotice("채팅을 종료합니다.");
            default -> {
                return false;
            }
        }
        return true;
    }
}
