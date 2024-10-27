package controller;

public enum Device {

  BACKLED("backled", "on", "off"),
  FRONTLED("frontled", "onoff", "onoff"),
  POTLED("potled", "on", "off");

  final String name;
  final String onCommand;
  final String offCommand;

  Device(String name, String onCommand, String offCommand) {
    this.name = name;
    this.onCommand = onCommand;
    this.offCommand = offCommand;
  }

}
