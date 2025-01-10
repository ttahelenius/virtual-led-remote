# **Virtual LED Remote**

A virtual set of remote controls to replace physical remote controls for LED devices. Essentially this is a GUI for sending the commands to a IR transmitter.

Paired with [IR command overlap solver](https://github.com/ttahelenius/ir-command-overlap-solver), this will counter-act all the side-effects of overlapping commands among the devices.

## **Getting Started**

### **Installation**

```bash
git clone https://github.com/ttahelenius/virtual-led-remote.git
```

### **Building**

```bash
cd virtual-led-remote
javac -sourcepath src src/controller/Controller.java -d out -encoding utf-8
```

Note: Java 9+ required.

### **Running**

```bash
java -cp out controller.Controller
```

Now the GUI should be functional, although it'll throw warnings until configured further (see [Configuration](#configuration)).

![image](https://github.com/user-attachments/assets/b914f56b-bfee-4a47-a387-8124363e66ef)

### **Configuration**

The GUI by itself is not of much use without appropriate configuration, namely one should create ```config.properties``` file in the project root. This is documented
in [doc/config.properties.example](https://github.com/ttahelenius/virtual-led-remote/tree/main/doc).

The config.properties file can contain the following optional paths:
* ```TransmitDaemon```: Path to a daemon program that's required to be running for transmitting the commands. In practice this should probably refer to [WinLIRC](https://github.com/leg0/WinLIRC) configured with commands matching the ones hard coded for the buttons in [controller.ui.Buttons.java](https://github.com/ttahelenius/virtual-led-remote/blob/main/src/controller/ui/Buttons.java).
* ```Transmit```: Path to a program that is called for each command as follows: ```<Transmit> <device> <command>``` <br />
  This corresponds to [Transmit.exe](https://github.com/leg0/WinLIRC/tree/master/Tools/Transmit) in [WinLIRC](https://github.com/leg0/WinLIRC).
* ```Solver```: Path to [a Python script that solves for command overlap](https://github.com/ttahelenius/ir-command-overlap-solver). This program supports all the features outlined in the script [commented here](https://github.com/ttahelenius/ir-command-overlap-solver/blob/main/main.py).
* ```Savefile```: Path to a file that will be overwritten with the last used setting for each device. In practice this is only needed because the solver needs to know the currently selected modes for each device.

## Adaptability

As probably evident already this project is catered quite specifically to my particular configuration of devices. However, with some effort the virtual remote control part (without the solver nonsense) could be adapted to
pretty much any device that's configured to be controlled programmatically.

As of writing this document the most relevant code for customization:
* Controller configuration [controller.ui.UI.createControllers()](https://github.com/ttahelenius/virtual-led-remote/blob/main/src/controller/ui/UI.java#L114)
* Device configuration [controller.Device](https://github.com/ttahelenius/virtual-led-remote/blob/main/src/controller/Device.java)
* Button configuration [controller.ui.Buttons](https://github.com/ttahelenius/virtual-led-remote/blob/main/src/controller/ui/Buttons.java)
