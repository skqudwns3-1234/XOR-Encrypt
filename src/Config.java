public class Config {
    private static final Config instance = new Config();

    private final int port = 5000;

    private Config() {
        // 싱글톤 패턴: 외부에서 Config 객체를 새로 만들지 못하게 막음
    }

    public static Config getInstance() {
        return instance;
    }

    public int getPort() {
        return port;
    }
}