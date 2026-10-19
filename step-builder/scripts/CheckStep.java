import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Validates that a plan step was closed out correctly.
 *
 * <p>Usage (no compilation needed, JDK 17+): {@code java CheckStep.java <plan-root> <step-id>}
 *
 * <ul>
 *   <li>report docs/steps/STEP-&lt;id&gt;-*.md exists, has all 12 numbered sections, no template placeholders left,
 *       at least 3 interview questions
 *   <li>PROGRESS.md row has status ✅ or 🟡, a date when ✅, and a link to the report
 *   <li>docs/interview-qa.md mentions the step
 * </ul>
 *
 * Exit code 0 when everything passes, 1 when problems were found, 2 on wrong usage.
 */
public class CheckStep {

    private static final List<String> REQUIRED_SECTIONS = List.of(
            "Mục tiêu",
            "Đã làm những gì",
            "File đã tạo / thay đổi",
            "Luồng hoạt động",
            "Code then chốt",
            "Kiến thức trọng tâm",
            "Trade-off & quyết định",
            "Vấn đề gặp phải & cách xử lý",
            "Kiểm chứng",
            "Câu hỏi phỏng vấn",
            "Tự luyện thêm",
            "Bước tiếp theo");

    private static final List<String> PLACEHOLDERS = List.of(
            "<Tên step>",
            "STEP <id>",
            "STEP <next>",
            "STEP <...>",
            "YYYY-MM-DD",
            "<module>",
            "<Khái niệm>",
            "`backend/.../X.java`",
            "// đoạn code ngắn",
            "# lệnh đã chạy thật");

    private static final Pattern DATE = Pattern.compile("\\d{4}-\\d{2}-\\d{2}");

    public static void main(String[] args) throws IOException {
        PrintStream out = new PrintStream(new FileOutputStream(FileDescriptor.out), true, StandardCharsets.UTF_8);
        if (args.length != 2) {
            out.println("Usage: java CheckStep.java <plan-root> <step-id>");
            System.exit(2);
        }
        Path root = Path.of(args[0]);
        String stepId = args[1];

        List<String> errors = new ArrayList<>();
        checkReport(root, stepId, errors);
        checkProgress(root, stepId, errors);
        checkInterviewQa(root, stepId, errors);

        if (errors.isEmpty()) {
            out.println("STEP " + stepId + ": OK");
            return;
        }
        out.println("STEP " + stepId + ": " + errors.size() + " vấn đề");
        errors.forEach(e -> out.println("  - " + e));
        System.exit(1);
    }

    private static void checkReport(Path root, String stepId, List<String> errors) throws IOException {
        Path stepsDir = root.resolve("docs").resolve("steps");
        List<Path> matches = new ArrayList<>();
        if (Files.isDirectory(stepsDir)) {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(stepsDir, "STEP-" + stepId + "-*.md")) {
                stream.forEach(matches::add);
            }
        }
        if (matches.isEmpty()) {
            errors.add("Thiếu báo cáo: " + stepsDir + "/STEP-" + stepId + "-<slug>.md");
            return;
        }
        if (matches.size() > 1) {
            errors.add("Có nhiều báo cáo cho cùng step: " + matches);
        }

        Path report = matches.get(0);
        String name = report.getFileName().toString();
        String text = Files.readString(report).replaceAll("(?s)<!--.*?-->", "");

        if (!Pattern.compile("^# STEP " + Pattern.quote(stepId) + "\\b", Pattern.MULTILINE).matcher(text).find()) {
            errors.add(name + ": tiêu đề phải bắt đầu bằng '# STEP " + stepId + "'");
        }
        for (int i = 0; i < REQUIRED_SECTIONS.size(); i++) {
            String heading = "## " + (i + 1) + ". " + REQUIRED_SECTIONS.get(i);
            if (!Pattern.compile("^" + Pattern.quote(heading), Pattern.MULTILINE).matcher(text).find()) {
                errors.add(name + ": thiếu mục '" + heading + "'");
            }
        }
        for (String placeholder : PLACEHOLDERS) {
            if (text.contains(placeholder)) {
                errors.add(name + ": còn placeholder của template: '" + placeholder + "'");
            }
        }

        Matcher qa = Pattern.compile("(?ms)^## 10\\..*?(?=^## 11\\.|\\z)").matcher(text);
        if (qa.find()) {
            long questions = Pattern.compile("\\*\\*Hỏi:\\*\\*").matcher(qa.group()).results().count();
            if (questions < 3) {
                errors.add(name + ": mục 10 cần ít nhất 3 câu hỏi phỏng vấn (đang có " + questions + ")");
            }
        }
    }

    private static void checkProgress(Path root, String stepId, List<String> errors) throws IOException {
        Path progress = root.resolve("PROGRESS.md");
        if (!Files.isRegularFile(progress)) {
            errors.add("Không tìm thấy " + progress);
            return;
        }
        List<String> lines = Files.readAllLines(progress);

        String header = lines.stream()
                .filter(l -> l.startsWith("|") && l.contains("Trạng thái"))
                .findFirst()
                .orElse(null);
        if (header == null) {
            errors.add("PROGRESS.md: không tìm thấy dòng tiêu đề bảng có cột 'Trạng thái'");
            return;
        }
        List<String> columns = cells(header);

        String row = lines.stream()
                .filter(l -> l.startsWith("|") && !cells(l).isEmpty() && cells(l).get(0).equals(stepId))
                .findFirst()
                .orElse(null);
        if (row == null) {
            errors.add("PROGRESS.md: không có dòng cho step " + stepId);
            return;
        }

        List<String> values = cells(row);
        Map<String, String> byColumn = new HashMap<>();
        for (int i = 0; i < columns.size() && i < values.size(); i++) {
            byColumn.put(columns.get(i), values.get(i));
        }

        String status = byColumn.getOrDefault("Trạng thái", "");
        boolean done = status.startsWith("✅");
        if (!done && !status.startsWith("🟡")) {
            errors.add("PROGRESS.md: trạng thái step " + stepId + " là '" + status + "', cần ✅ hoặc 🟡");
        }
        if (done && !DATE.matcher(byColumn.getOrDefault("Ngày xong", "")).matches()) {
            errors.add("PROGRESS.md: step " + stepId + " đã ✅ nhưng thiếu 'Ngày xong' dạng YYYY-MM-DD");
        }
        if (!byColumn.getOrDefault("Báo cáo", "").contains("STEP-" + stepId + "-")) {
            errors.add("PROGRESS.md: cột 'Báo cáo' của step " + stepId + " chưa link tới báo cáo");
        }
    }

    private static void checkInterviewQa(Path root, String stepId, List<String> errors) throws IOException {
        Path qa = root.resolve("docs").resolve("interview-qa.md");
        if (!Files.isRegularFile(qa)) {
            errors.add("Không tìm thấy " + qa);
            return;
        }
        if (!Pattern.compile("STEP " + Pattern.quote(stepId) + "\\b").matcher(Files.readString(qa)).find()) {
            errors.add("docs/interview-qa.md: chưa có mục cho STEP " + stepId);
        }
    }

    private static List<String> cells(String tableRow) {
        String trimmed = tableRow.strip();
        if (trimmed.startsWith("|")) trimmed = trimmed.substring(1);
        if (trimmed.endsWith("|")) trimmed = trimmed.substring(0, trimmed.length() - 1);
        List<String> result = new ArrayList<>();
        for (String cell : trimmed.split("\\|", -1)) {
            result.add(cell.strip());
        }
        return result;
    }
}
