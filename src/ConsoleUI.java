import java.net.Socket;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ConsoleUI {
    private static final ConsoleUI instance = new ConsoleUI();
    private static final String LINE = "====================================================================";
    private static final String SUB_LINE = "--------------------------------------------------------------------";
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private ConsoleUI() {
    }

    public static ConsoleUI getInstance() {
        return instance;
    }

    public void showTitle() {
        System.out.println(LINE);
        System.out.println("                        XOR SECURE CHAT");
        System.out.println(LINE);
    }

    public void showMyInfo(String nickname, String ip) {
        System.out.println();
        System.out.println("[내 정보]");
        System.out.println("닉네임 : " + nickname);
        System.out.println("IP     : " + ip);
        System.out.println(SUB_LINE);
    }

    public void showMenu() {
        System.out.println("1. 상대방의 연결 기다리기");
        System.out.println("2. 상대방에게 연결하기");
        System.out.println("0. 프로그램 종료");
        System.out.println(SUB_LINE);
    }

    public void showWaiting(int port) {
        System.out.println();
        System.out.println("[연결 대기 중] PORT: " + port);
        System.out.println("상대방의 연결을 기다리고 있습니다...");
    }

    public void showConnected(Socket socket, String nickname) {
        clear();
        System.out.println(LINE);
        System.out.println("                    " + nickname + "의 XOR SECURE CHAT");
        System.out.println("상태: " + socket.getInetAddress().getHostAddress() + "와 연결됨");
        System.out.println(LINE);
        showCommandLine();
    }

    public void showMessage(MessageDTO.Message message, boolean mine) {
        String time = TIME_FORMAT.format(Instant.ofEpochMilli(message.getSentTime()));
        String sender = mine ? "나" : message.getNickname();
        System.out.println();
        System.out.println("[" + time + "] " + sender + " > " + message.getContent());
    }

    public void showHistory(List<MessageDTO.Message> messages, String nickname) {
        System.out.println();
        System.out.println(SUB_LINE);
        System.out.println("[채팅 기록]");

        if (messages.isEmpty()) {
            System.out.println("저장된 메시지가 없습니다.");
        } else {
            messages.forEach(message -> {
                String time = TIME_FORMAT.format(Instant.ofEpochMilli(message.getSentTime()));
                String sender = message.getNickname().equals(nickname) ? "나" : message.getNickname();
                System.out.println("[" + time + "] " + sender + " > " + message.getContent());
            });
        }
        System.out.println(SUB_LINE);
    }

    public void showHelp() {
        System.out.println();
        System.out.println(SUB_LINE);
        System.out.println("/help    사용 가능한 명령어 출력");
        System.out.println("/history 저장된 채팅 기록 출력");
        System.out.println("/info    현재 연결 정보 출력");
        System.out.println("/clear   터미널 화면 정리");
        System.out.println("/quit    채팅 종료");
        System.out.println(SUB_LINE);
    }

    public void showConnectionInfo(Socket socket, String nickname) {
        System.out.println();
        System.out.println(SUB_LINE);
        System.out.println("[현재 연결 정보]");
        System.out.println("닉네임   : " + nickname);
        System.out.println("내 주소   : " + socket.getLocalAddress().getHostAddress());
        System.out.println("상대 주소 : " + socket.getInetAddress().getHostAddress());
        System.out.println("포트      : " + socket.getPort());
        System.out.println(SUB_LINE);
    }

    public void showPrompt() {
        System.out.print("메시지 입력 > ");
    }

    public void showError(String message) {
        System.out.println("[오류] " + message);
    }

    public void showNotice(String message) {
        System.out.println("[알림] " + message);
    }

    public void showCommandLine() {
        System.out.println("/help | /history | /info | /clear | /quit");
        System.out.println(SUB_LINE);
    }

    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}