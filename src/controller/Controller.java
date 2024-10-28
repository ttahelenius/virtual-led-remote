package controller;

import controller.StateHistory.DeviceState;
import controller.ui.UI;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static controller.InputStore.readAccess;

public class Controller {

  private static final String PROPERTIES_PATH = "config.properties";
  private static final String TRANSMIT_DAEMON_PATH;
  private static final String TRANSMIT_PATH;
  private static final String SOLVER_PATH;
  private static final String SAVEFILE_PATH;

  private static final InputStore SAVE_FILE;
  private static final CommandTransmitManager TRANSMIT_MANAGER;

  private static final StateHistory STATE_HISTORY = new StateHistory();
  private static final List<StateHistory.ChangeListener> STATE_CHANGE_LISTENERS = new ArrayList<>();

  static {
    Properties properties = new Properties();
    try (FileInputStream in = new FileInputStream(PROPERTIES_PATH)) {
      properties.load(in);
    } catch (IOException e) {
      UI.confirmOrExit("Couldn't read " + PROPERTIES_PATH + " file!\n"
          + "Only the GUI can be expected to function without it. Proceed?");
    }

    TRANSMIT_DAEMON_PATH = properties.getProperty("TransmitDaemon");
    TRANSMIT_PATH = properties.getProperty("Transmit");
    SOLVER_PATH = properties.getProperty("Solver");
    SAVEFILE_PATH = properties.getProperty("Savefile");
    checkPaths(TRANSMIT_DAEMON_PATH, TRANSMIT_PATH, SOLVER_PATH, SAVEFILE_PATH);

    if (SAVEFILE_PATH != null) {
      SAVE_FILE = new SaveFile(Paths.get(SAVEFILE_PATH));
    } else {
      UI.confirmOrExit("Missing configuration for \"Savefile\" in " + PROPERTIES_PATH + "!\n"
          + "Each device will be assumed to be on and set to red initially and the settings won't be preserved. Proceed?");
      SAVE_FILE = InputStore.DUMMY;
    }

    if (SOLVER_PATH != null) {
      TRANSMIT_MANAGER = new CommandOverlapManager(SOLVER_PATH, Controller::sendCode, readAccess(SAVE_FILE));
    } else {
      UI.confirmOrExit("Missing configuration for \"Solver\" in " + PROPERTIES_PATH + "!\n"
          + "The IR command will be sent without resolving potential conflicts. Proceed?");
      TRANSMIT_MANAGER = CommandTransmitManager.direct(Controller::sendCode);
    }

    saveStateToHistory();
  }

  private static void checkPaths(String... paths) {
    for (String path : paths) {
      if (path == null)
        continue;
      if (!Files.exists(Paths.get(path))) {
        UI.showError("Invalid path " + path);
        exit();
      }
    }
  }

  public static void main(String[] args) {
    launchTransmitDaemonIfNotRunning();
    UI.show();
  }

  public static void exit(Throwable e) {
    e.printStackTrace(System.err);
    exit();
  }

  public static void exit() {
    System.exit(1);
  }

  private static void launchTransmitDaemonIfNotRunning() {
    if (TRANSMIT_DAEMON_PATH != null && !isTransmitDaemonRunning())
      executeCommand(TRANSMIT_DAEMON_PATH);
  }

  private static boolean isTransmitDaemonRunning() {
    return ProcessUtil.isRunning(Paths.get(TRANSMIT_DAEMON_PATH).getFileName().toString());
  }

  public static void pressButton(Device device, String remoteCommand, boolean saveInput, boolean awaitRepeats) {
    TRANSMIT_MANAGER.performCommandActions(device, remoteCommand, awaitRepeats);

    if (saveInput)
      SAVE_FILE.saveInput(device, remoteCommand);

    if (device.onCommand.equals(remoteCommand) || device.offCommand.equals(remoteCommand)) {
      boolean newStatus;
      if (device.onCommand.equals(device.offCommand))
        newStatus = !SAVE_FILE.loadStatus(device);
      else
        newStatus = device.onCommand.equals(remoteCommand);

      if (newStatus)
        SAVE_FILE.saveOnStatus(device);
      else
        SAVE_FILE.saveOffStatus(device);
    }
  }

  private static void sendCode(String command) {
    if (TRANSMIT_PATH == null) {
      UI.showError("Missing configuration for \"Transmit\" in " + PROPERTIES_PATH + "! IR command not sent.");
      return;
    }
    String[] split = command.split(" ", 2);
    String device = split[0];
    String code = split[1];
    executeCommand(TRANSMIT_PATH, device, code);
  }

  private static void executeCommand(String... command) {
    ProcessUtil.execute(command);
  }

  public static void undo() {
    try {
      Collection<DeviceState> newStates = STATE_HISTORY.loadPrevious();
      for (DeviceState deviceState : newStates) {
        pressButton(deviceState.device, deviceState.state, true, false);
      }
      stateHistoryChanged();
    } catch (IllegalStateException e) {
      exit(e);
    }
  }

  public static void redo() {
    try {
      Collection<DeviceState> newStates = STATE_HISTORY.loadNext();
      for (DeviceState deviceState : newStates) {
        pressButton(deviceState.device, deviceState.state, true, false);
      }
      stateHistoryChanged();
    } catch (IllegalStateException e) {
      exit(e);
    }
  }

  public static void saveStateToHistory() {
    List<DeviceState> deviceStates = Arrays.stream(Device.values())
        .map(d -> new DeviceState(d, SAVE_FILE.loadInput(d)))
        .collect(Collectors.toList());
    STATE_HISTORY.save(deviceStates);
    stateHistoryChanged();
  }

  public static boolean canUndo() {
    return STATE_HISTORY.hasPrevious();
  }

  public static boolean canRedo() {
    return STATE_HISTORY.hasNext();
  }

  public static void addStateChangeListener(StateHistory.ChangeListener listener) {
    STATE_CHANGE_LISTENERS.add(listener);
  }

  private static void stateHistoryChanged() {
    STATE_CHANGE_LISTENERS.forEach(StateHistory.ChangeListener::stateChanged);
  }

}
