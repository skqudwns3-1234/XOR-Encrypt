public class UserProfile {
    private final String nickname;
    private final String ip;

    public UserProfile(String nickname, String ip) {
        if (nickname == null || nickname.isBlank()) {
            throw new IllegalArgumentException("닉네임은 비워둘 수 없습니다.");
        }
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("IP 정보가 필요합니다.");
        }

        this.nickname = nickname;
        this.ip = ip;
    }

    public String getNickname() {
        return nickname;
    }

    public String getIp() {
        return ip;
    }
}