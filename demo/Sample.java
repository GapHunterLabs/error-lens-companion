import java.util.List;
import java.util.ArrayList;
import java.util.Map;

public class Sample {

    public void brokenSyntax() {
        int total = 1 + 2
        System.out.println(total);
    }

    public void unresolvedSymbol() {
        int result = totallyUndefinedMethodCall(42);
        System.out.println(result);
    }

    public void unusedLocal() {
        int neverRead = 99;
        System.out.println("done");
    }

    public void twoProblemsOneLine() {
        int x = undefinedVariable + anotherUndefinedThing;
    }
}
