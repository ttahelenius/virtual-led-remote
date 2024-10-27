package controller;

import java.util.function.Consumer;

public interface CommandTransmitManager {
  void performCommandActions(Device device, String remoteCommand, boolean awaitRepeats);

  static CommandTransmitManager direct(Consumer<String> commandSender) {
    return (device, command, awaitRepeats) -> commandSender.accept(device.name + " " + command);
  }
}
