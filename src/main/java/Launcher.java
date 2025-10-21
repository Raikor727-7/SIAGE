import java.io.File;
import java.lang.ProcessBuilder;

public class Launcher {
    public static void main(String[] args) throws Exception {
        String javaHome = System.getProperty("java.home");
        String javaCmd = javaHome + File.separator + "bin" + File.separator + "java";

        ProcessBuilder pb = new ProcessBuilder(
                javaCmd,
                "--module-path", "javafx-sdk-21/lib",
                "--add-modules", "javafx.controls,javafx.fxml,javafx.graphics,javafx.base",
                "-jar", "SIAGE.jar"
        );

        pb.directory(new File("."));
        pb.inheritIO();
        pb.start().waitFor();
    }
}