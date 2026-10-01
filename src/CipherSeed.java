import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CipherSeed {
    private static final Path DEFAULT_PATH = Path.of("config", "cipher_seed.txt");

    private static final long A = 1993;
    private static final long C = 1205;
    private static final long M = 256;

    private final List<LanguageSeed> seeds;

    private CipherSeed(List<LanguageSeed> seeds) {
        if (seeds == null || seeds.isEmpty()) {
            throw new IllegalArgumentException("암호화 시드가 하나 이상 필요합니다.");
        }
        this.seeds = List.copyOf(seeds);
    }

    public static CipherSeed loadDefault() {
        return load(DEFAULT_PATH);
    }

    public static CipherSeed load(Path path) {
        if (path == null) {
            throw new IllegalArgumentException("시드 파일 경로가 필요합니다.");
        }

        List<LanguageSeed> seeds = new ArrayList<>();
        Set<String> languageNames = new HashSet<>();

        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) {
                    continue;
                }

                String[] parts = line.split("=", 2);
                if (parts.length != 2) {
                    throw new IllegalArgumentException(
                            lineNumber + "번째 줄의 형식이 올바르지 않습니다."
                    );
                }

                String language = parts[0].trim().toUpperCase();
                LocalDate releaseDate = LocalDate.parse(parts[1].trim());

                if (!languageNames.add(language)) {
                    throw new IllegalArgumentException("중복된 언어 시드입니다: " + language);
                }

                seeds.add(new LanguageSeed(language, releaseDate));
            }
        } catch (IOException e) {
            throw new IllegalStateException("시드 파일을 읽을 수 없습니다: " + path, e);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("날짜는 YYYY-MM-DD 형식으로 작성해야 합니다.", e);
        }

        return new CipherSeed(seeds);
    }

    public byte[] createKey(int length) {
        if (length < 1) {
            throw new IllegalArgumentException("키 길이는 1 이상이어야 합니다.");
        }

        byte[] key = new byte[length];
        long value = createInitialSeed();

        for (int i = 0; i < key.length; i++) {
            value = (value * A + C) % M;
            key[i] = (byte) value;
        }

        return key;
    }

    private long createInitialSeed() {
        long initialSeed = 0;

        for (int i = 0; i < seeds.size(); i++) {
            long dateNumber = seeds.get(i).toDateNumber();
            initialSeed += dateNumber * (i + 1L);
        }

        return initialSeed;
    }

    public List<LanguageSeed> getSeeds() {
        return seeds;
    }

    public static class LanguageSeed {
        private final String language;
        private final LocalDate releaseDate;

        private LanguageSeed(String language, LocalDate releaseDate) {
            if (language == null || language.isBlank()) {
                throw new IllegalArgumentException("프로그래밍 언어 이름이 필요합니다.");
            }
            if (releaseDate == null) {
                throw new IllegalArgumentException("프로그래밍 언어의 기준 날짜가 필요합니다.");
            }

            this.language = language;
            this.releaseDate = releaseDate;
        }

        public String getLanguage() {
            return language;
        }

        public LocalDate getReleaseDate() {
            return releaseDate;
        }

        private long toDateNumber() {
            return releaseDate.getYear() * 10000L
                    + releaseDate.getMonthValue() * 100L
                    + releaseDate.getDayOfMonth();
        }
    }
}