package controller.ui;

import controller.Device;

import java.awt.*;
import java.util.List;

import static controller.Device.*;

class CompundButton extends Button {

  final List<DeviceCommand> deviceCommands;

  CompundButton(Color potLedColor, String potLedCommand, Color backLedColor, String backLedCommand, Color frontLedColor, String frontLedCommand) {
    this(new Gradient(potLedColor, backLedColor, frontLedColor).discrete(), potLedCommand, backLedCommand, frontLedCommand);
  }

  CompundButton(Gradient gradient, String potLedCommand, String backLedCommand, String frontLedCommand) {
    super(null, gradient, null);
    this.deviceCommands = List.of(
        new DeviceCommand(BACKLED, backLedCommand),
        new DeviceCommand(FRONTLED, frontLedCommand),
        new DeviceCommand(POTLED, potLedCommand)
    );
  }

  static class DeviceCommand {
    final Device device;
    final String remoteCommand;

    private DeviceCommand(Device device, String remoteCommand) {
      this.device = device;
      this.remoteCommand = remoteCommand;
    }
  }

}
