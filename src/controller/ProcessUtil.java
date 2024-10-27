package controller;

import controller.ui.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProcessUtil {

  public static void execute(String... args) {
    try {
      new ProcessBuilder(args).start();
    } catch (IOException e) {
      UI.showError(e.getMessage());
      Controller.exit(new IllegalStateException("Command execution failed"));
    }
  }

  public static List<String> executeAndReturnOutput(String... args) {
    ProcessBuilder ps = new ProcessBuilder(args);
    List<String> output = new ArrayList<>();
    try {
      ps.redirectErrorStream(true);
      Process pr = ps.start();
      try (BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()))) {
        String line;
        while ((line = in.readLine()) != null) {
          output.add(line);
        }
        pr.waitFor();
        if (pr.exitValue() > 0) {
          UI.showError(String.join("\n", output));
          return null;
        }
      }
    } catch (IOException | InterruptedException e) {
      UI.showError(e.getMessage());
      Controller.exit(new IllegalStateException("Process output parsing failed"));
    }
    return output;
  }

  public static boolean isRunning(final String processName) {
    return ProcessHandle
        .allProcesses()
        .filter(ProcessHandle::isAlive)
        .anyMatch(p -> Paths.get(p.info().command().orElse("")).endsWith(processName));
  }

}
