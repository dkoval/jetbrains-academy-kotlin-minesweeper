import minesweeper.MainKt;
import org.hyperskill.hstest.v6.dynamic.output.SystemOutHandler;
import org.hyperskill.hstest.v6.stage.BaseStageTest;
import org.hyperskill.hstest.v6.testcase.CheckResult;
import org.hyperskill.hstest.v6.testcase.TestCase;

import java.util.*;
import java.util.stream.Collectors;

public class MinesweeperTest extends BaseStageTest<Integer> {

    public MinesweeperTest() {
        super(MainKt.class);
    }

    @Override
    public List<TestCase<Integer>> generate() {
        List<TestCase<Integer>> tests = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            TestCase<Integer> test = new TestCase<Integer>()
                .addInput("" + i)
                .setAttach(i);
            tests.add(test);
            tests.add(test);
        }
        return tests;
    }

    @Override
    public CheckResult check(String reply, Integer attach) {

        String outputSinceLastInput = SystemOutHandler.getDynamicOutput().trim();

        List<String> lines =
            Arrays.stream(outputSinceLastInput.split("\n"))
                .map(String::trim)
                .collect(Collectors.toList());

        if (lines.isEmpty()) {
            return CheckResult.FALSE(
                "Looks like you didn't output a single line!"
            );
        }

        if (lines.size() != 9) {
            return CheckResult.FALSE(
                "You should output exactly 9 lines of the field. Found: " + lines.size() + "."
            );
        }

        int mines = 0;

        for (String line : lines) {
            if (line.length() != 9) {
                return CheckResult.FALSE(
                    "One of the lines of the field doesn't have 9 symbols, " +
                        "but has " + line.length() + ".\n" +
                        "This line is \"" + line + "\""
                );
            }

            for (char c : line.toCharArray()) {
                if (c != 'X' && c != '.') {
                    return CheckResult.FALSE(
                        "One of the characters is not equal to either 'X' or '.'.\n" +
                            "In this line: \"" + line + "\"."
                    );
                }
                if (c == 'X') {
                    mines++;
                }
            }
        }

        if (attach != mines) {
            return CheckResult.FALSE(
                "Expected to see " + attach + " mines, found " + mines
            );
        }

        return CheckResult.TRUE;
    }
}
