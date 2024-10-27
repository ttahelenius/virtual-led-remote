package controller;

import controller.ui.UI;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static controller.Controller.exit;

/**
 * Uses a file to store inputs and on/off statuses. This implementation fails fast at unexpected situations.
 * File format: "(device 1 input) ... (device n input) (device 1 status 0|1) ... (device n status 0|1)"
 * Where devices are listed in the order specified in {@link #DEVICE_ORDER_IN_SAVE_FILE}.
 */
public class SaveFile implements InputStore {

  private static final Device[] DEVICE_ORDER_IN_SAVE_FILE = {Device.BACKLED, Device.FRONTLED, Device.POTLED};

  private final Path savefilePath;
  private final Map<Device, Integer> inputIndexInSaveFile;
  private final Map<Device, Integer> statusIndexInSaveFile;

  SaveFile(Path savefilePath) {
    this.savefilePath = savefilePath;
    this.inputIndexInSaveFile = new HashMap<>();
    this.statusIndexInSaveFile = new HashMap<>();
    if (Device.values().length != DEVICE_ORDER_IN_SAVE_FILE.length) {
      exit(new IllegalStateException("Unsupported devices registered!"));
    }
    int i = 0;
    for (Device device : DEVICE_ORDER_IN_SAVE_FILE) {
      inputIndexInSaveFile.put(device, i);
      statusIndexInSaveFile.put(device, i + DEVICE_ORDER_IN_SAVE_FILE.length);
      i++;
    }
  }

  @Override
  public void saveInput(Device device, String value) {
    writeValue(inputIndexInSaveFile.get(device), value);
  }

  @Override
  public void saveOnStatus(Device device) {
    writeValue(statusIndexInSaveFile.get(device), "1");
  }

  @Override
  public void saveOffStatus(Device device) {
    writeValue(statusIndexInSaveFile.get(device), "0");
  }

  @Override
  public String loadInput(Device device) {
    return readValues()[inputIndexInSaveFile.get(device)];
  }

  @Override
  public boolean loadStatus(Device device) {
    return "1".equals(readValues()[statusIndexInSaveFile.get(device)]);
  }

  private void writeValue(Integer index, String value) {
    String[] originalValues = readValues();
    originalValues[index] = value;
    writeValues(originalValues);
  }

  private void writeValues(String[] originalValues) {
    try {
      Files.writeString(savefilePath, String.join(" ", originalValues));
    } catch (IOException e) {
      UI.showError(e.getMessage());
      exit(new IllegalStateException("Save file write failed"));
    }
  }

  private String[] readValues() {
    String originalContents = "";
    try {
      originalContents = Files.readString(savefilePath, StandardCharsets.US_ASCII);
    } catch (IOException e) {
      UI.showError(e.getMessage());
      exit(new IllegalStateException("Save file read failed"));
    }

    int size = inputIndexInSaveFile.size() + statusIndexInSaveFile.size();
    String[] originalValues = originalContents.split(" ", size);

    if (originalValues.length != size) {
      UI.showError("Unexpected save file format, operation failed");
      exit(new IllegalStateException("Unsupported save file format"));
    }

    return originalValues;
  }

}
