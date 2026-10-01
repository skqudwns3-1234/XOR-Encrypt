import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class MessageDTO {
    private static final MessageDTO instance = new MessageDTO();

    private final List<Message> messages = new ArrayList<>();

    private MessageDTO() {
        // 싱글톤 패턴: 메시지 저장소는 프로그램 전체에서 하나만 사용
    }

    public static MessageDTO getInstance() {
        return instance;
    }

    public void addMessage(Message message) {
        if (message == null) {
            throw new IllegalArgumentException("저장할 메시지가 없습니다.");
        }
        messages.add(message);
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public static class Message {
        private final String nickname;
        private final String content;
        private final long sentTime;

        public Message(String nickname, String content) {
            this(nickname, content, Instant.now().toEpochMilli());
        }

        private Message(String nickname, String content, long sentTime) {
            if (nickname == null || nickname.isBlank()) {
                throw new IllegalArgumentException("메시지 작성자 정보가 필요합니다.");
            }
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("메시지 내용은 비워둘 수 없습니다.");
            }
            if (sentTime < 0) {
                throw new IllegalArgumentException("메시지 전송 시간이 올바르지 않습니다.");
            }

            this.nickname = nickname;
            this.content = content;
            this.sentTime = sentTime;
        }

        public String getNickname() {
            return nickname;
        }

        public String getContent() {
            return content;
        }

        public long getSentTime() {
            return sentTime;
        }

        public String toPacket() {
            return sentTime + "\t" + nickname + "\t" + content;
        }

        public static Message fromPacket(String packet) {
            if (packet == null || packet.isBlank()) {
                throw new IllegalArgumentException("메시지 패킷이 비어 있습니다.");
            }

            String[] parts = packet.split("\\t", 3);
            if (parts.length != 3) {
                throw new IllegalArgumentException("메시지 패킷 형식이 올바르지 않습니다.");
            }

            long sentTime = Long.parseLong(parts[0]);
            return new Message(parts[1], parts[2], sentTime);
        }

        @Override
        public String toString() {
            return nickname + ": " + content;
        }
    }
}