package controller;

import java.util.Arrays;

public interface InputStore {

  void saveInput(Device device, String value);
  void saveOnStatus(Device device);
  void saveOffStatus(Device device);
  String loadInput(Device device);
  boolean loadStatus(Device device);

  static InputStore readAccess(InputStore original) {
    return new InputStore() {
      @Override
      public void saveInput(Device device, String value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void saveOnStatus(Device device) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void saveOffStatus(Device device) {
        throw new UnsupportedOperationException();
      }

      @Override
      public String loadInput(Device device) {
        return original.loadInput(device);
      }

      @Override
      public boolean loadStatus(Device device) {
        return original.loadStatus(device);
      }
    };
  }

  InputStore DUMMY = new DummyInputStore();

  class DummyInputStore implements InputStore {
    private final String[] contents;

    private DummyInputStore() {
      contents = new String[2*Device.values().length];
      Arrays.stream(Device.values()).forEach(d -> {
        contents[d.ordinal()] = "r";
        contents[Device.values().length + d.ordinal()] = "1";
      });
    }

    @Override
    public void saveInput(Device device, String value) {
      contents[device.ordinal()] = value;
    }

    @Override
    public void saveOnStatus(Device device) {
      contents[Device.values().length + device.ordinal()] = "1";
    }

    @Override
    public void saveOffStatus(Device device) {
      contents[Device.values().length + device.ordinal()] = "0";
    }

    @Override
    public String loadInput(Device device) {
      return contents[device.ordinal()];
    }

    @Override
    public boolean loadStatus(Device device) {
      return contents[Device.values().length + device.ordinal()].equals("1");
    }

  }

}
