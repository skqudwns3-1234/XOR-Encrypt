import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Optional;

public class LanNetwork {
    public Optional<String> findMyLanIp() {
        try {
            return Collections.list(NetworkInterface.getNetworkInterfaces()).stream()
                    .filter(ni -> {
                        try {
                            return ni.isUp() && !ni.isLoopback() && !ni.isVirtual();
                        } catch (SocketException e) {
                            return false;
                        }
                    })
                    .flatMap(ni -> Collections.list(ni.getInetAddresses()).stream())
                    .filter(addr -> addr instanceof Inet4Address)
                    .map(addr -> addr.getHostAddress())
                    .filter(ip -> ip.startsWith("192.168."))
                    .findFirst();
        } catch (SocketException e) {
            return Optional.empty();
        }
    }

    public String validateLanIp(String ip) {
        if (ip == null || ip.isBlank()) {
            throw new IllegalArgumentException("상대 IP가 필요합니다.");
        }

        String value = ip.trim();
        String[] parts = value.split("\\.");

        if (parts.length != 4) {
            throw new IllegalArgumentException(
                    "IP는 192.168.200.188 형식으로 입력해주세요."
            );
        }

        int first = parseOctet(parts[0], "첫 번째");
        int second = parseOctet(parts[1], "두 번째");
        int third = parseOctet(parts[2], "세 번째");
        int fourth = parseOctet(parts[3], "네 번째");

        if (first != 192 || second != 168) {
            throw new IllegalArgumentException("192.168.x.x LAN IP만 연결할 수 있습니다.");
        }

        return first + "." + second + "." + third + "." + fourth;
    }

    private int parseOctet(String value, String position) {
        try {
            int octet = Integer.parseInt(value);
            if (octet < 0 || octet > 255) {
                throw new IllegalArgumentException(
                        position + " IP 구역은 0부터 255 사이여야 합니다."
                );
            }
            return octet;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    position + " IP 구역은 숫자여야 합니다.", e
            );
        }
    }
}