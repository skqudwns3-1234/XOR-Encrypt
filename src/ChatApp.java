import java.io.IOException;
import java.net.Socket;
import java.util.Optional;
import java.util.Scanner;

public class ChatApp {
    private static final Scanner in = new Scanner(System.in);
    private static final LanNetwork lanNetwork = new LanNetwork();
    private static final Config config = Config.getInstance();
    private static final ConsoleUI ui = ConsoleUI.getInstance();

    static void run() {
        ui.showTitle();

        String nickname = readNickname();
        Optional<String> myIp = lanNetwork.findMyLanIp();
        String ip = myIp.orElse("LAN IP 감지 실패");
        UserProfile profile = new UserProfile(nickname, ip);

        ui.showMyInfo(profile.getNickname(), profile.getIp());

        int menu = readMenu();
        switch (menu) {
            case 1 -> serverMode(profile);
            case 2 -> clientMode(profile);
            case 0 -> ui.showNotice("프로그램을 종료합니다.");
        }
    }

    private static String readNickname() {
        while (true) {
            System.out.print("닉네임을 입력해주세요 > ");
            String nickname = in.nextLine().trim();

            if (!nickname.isEmpty()) {
                return nickname;
            }
            ui.showError("닉네임은 비워둘 수 없습니다.");
        }
    }

    private static int readMenu() {
        while (true) {
            ui.showMenu();
            System.out.print("메뉴 선택 > ");
            String input = in.nextLine().trim();

            try {
                int menu = Integer.parseInt(input);
                if (menu >= 0 && menu <= 2) {
                    return menu;
                }
            } catch (NumberFormatException e) {
                // 아래에서 같은 오류 메시지를 출력함
            }
            ui.showError("0, 1, 2 중 하나를 입력해주세요.");
        }
    }

    private static void serverMode(UserProfile profile) {
        ui.showWaiting(config.getPort());
        PeerServer server = PeerServer.defaultPeerServer();
        server.waitClient().ifPresentOrElse(
                socket -> startChat(socket, profile),
                () -> ui.showError("상대방과 연결하지 못했습니다.")
        );
    }

    private static void clientMode(UserProfile profile) {
        System.out.print("상대 IP 입력(예: 192.168.200.188) > ");
        String inputIp = in.nextLine();

        try {
            String ip = lanNetwork.validateLanIp(inputIp);
            PeerClient client = new PeerClient();
            client.connect(ip, config.getPort()).ifPresentOrElse(
                    socket -> startChat(socket, profile),
                    () -> ui.showError("상대방에게 연결하지 못했습니다.")
            );
        } catch (IllegalArgumentException e) {
            ui.showError(e.getMessage());
        }
    }

    private static void startChat(Socket socket, UserProfile profile) {
        ui.showConnected(socket, profile.getNickname());

        try {
            MessageHandler handler = new MessageHandler(socket, in, ui, profile);
            handler.start();
        } catch (IOException e) {
            ui.showError("채팅 오류: " + e.getMessage());
        }
    }
}