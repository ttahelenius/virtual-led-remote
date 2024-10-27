package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static controller.RepeatInputHandler.Result.UNHANDLED;

/**
 * Manages command transmits taking into account possible command overlap among all devices.
 * These conflics may be avoided or undone by instead executing a series of specific commands.
 * Such a command series is solved for on a case by case basis by the Python solver program
 * specified in {@link #solverPath}. Possible delays to avoid device overwhelm as well as
 * situations to await repeated inputs, will be suggested by the program output, which this
 * manager attempts to comply accordingly.
 */
public class CommandOverlapManager implements CommandTransmitManager {

  private static final String AWAIT_REPEATS_COMMAND = "*Await repeats*";
  private static final String DELAY_COMMAND = "*Delay*";
  private static final long DELAY_AMOUNT_MS = 300L;
  private static final boolean PRINT_OUT_COMMANDS = true;

  private final String solverPath;
  private final Consumer<String> commandSender;
  private final InputStore inputStore;

  CommandOverlapManager(String solverPath, Consumer<String> commandSender, InputStore inputStore) {
    this.solverPath = solverPath;
    this.commandSender = commandSender;
    this.inputStore = inputStore;
  }

  @Override
  public void performCommandActions(Device device, String remoteCommand, boolean awaitRepeats) {
    String givenCommand = device.name + " " + remoteCommand;

    if (awaitRepeats) {
      RepeatInputHandler.Result result;
      result = RepeatInputHandler.get().processIfRepeat(givenCommand);
      if (result != UNHANDLED)
        return;
    } else {
      RepeatInputHandler.get().waitUntilReady();
    }

    if (PRINT_OUT_COMMANDS)
      System.out.print("\n");

    final List<String> commandSeries = getCommandSeries(device, remoteCommand);
    if (commandSeries == null)
      return;

    for (int i = 0; i < commandSeries.size(); i++) {
      String command = commandSeries.get(i);
      if (command.equals(AWAIT_REPEATS_COMMAND) && awaitRepeats) {
        RepeatInputHandler.get().awaitRepeats(
            givenCommand,
            commandSeries.get(i - 1),
            commandSeries.subList(i + 1, commandSeries.size()),
            this::performCommand
        );
        break; // RepeatInputHandler handles remaining commands
      }
      performCommand(command);
    }
  }

  private void performCommand(String command) {
    if (PRINT_OUT_COMMANDS)
      System.out.println(command);

    if (command.equals(DELAY_COMMAND)) {
      try {
        Thread.sleep(DELAY_AMOUNT_MS);
      } catch (InterruptedException e) {
        e.printStackTrace(System.err);
      }
    } else {
      commandSender.accept(command);
    }
  }

  private List<String> getCommandSeries(Device device, String remoteCommand) {
    List<String> currentStates = new ArrayList<>();
    for (Device dev : Device.values()) {
      currentStates.add(dev.name + " " + inputStore.loadInput(dev));
    }
    for (Device dev : Device.values()) {
      currentStates.add(dev.name + " " + (inputStore.loadStatus(dev) ? "on" : "off"));
    }
    String desiredState = getDesiredState(device, remoteCommand);
    String[] args = {
        "python", solverPath, String.join(", ", currentStates), desiredState,
        "--machine-readable", "--use-cache", "--avoid-overwhelm", "--await-repeats"
    };
    return ProcessUtil.executeAndReturnOutput(args);
  }

  private String getDesiredState(Device device, String remoteCommand) {
    String command = remoteCommand;
    if (device.onCommand.equals(remoteCommand) || device.offCommand.equals(remoteCommand)) {
      boolean onOffToggle = device.onCommand.equals(device.offCommand);
      if (onOffToggle) {
        boolean previousStatus = inputStore.loadStatus(device);
        command = previousStatus ? "off" : "on";
      } else {
        command = device.onCommand.equals(remoteCommand) ? "on" : "off";
      }
    }
    return device.name + " " + command;
  }
}
