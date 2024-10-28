package controller;

import java.util.*;

public class StateHistory {

  private List<State> history = new ArrayList<>();
  private int head = -1;

  public void save(Collection<DeviceState> deviceStates) {
    State newState = new State(deviceStates);
    if (!changed(newState))
      return;
    head++;
    history = history.subList(0, head);
    history.add(newState);
  }

  private boolean changed(State state) {
    if (head == -1)
      return true;
    return !state.deviceStates.equals(history.get(head).deviceStates);
  }

  public boolean hasPrevious() {
    return head > 0;
  }

  public boolean hasNext() {
    return head < history.size() - 1;
  }

  public Collection<DeviceState> loadPrevious() throws IllegalStateException {
    if (!hasPrevious())
      throw new IllegalStateException();
    head--;
    return history.get(head).deviceStates;
  }

  public Collection<DeviceState> loadNext() throws IllegalStateException {
    if (!hasNext())
      throw new IllegalStateException();
    head++;
    return history.get(head).deviceStates;
  }

  public interface ChangeListener {
    void stateChanged();
  }

  public static class DeviceState {
    public final Device device;
    public final String state;

    public DeviceState(Device device, String state) {
      this.device = device;
      this.state = state;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o)
        return true;
      if (o == null || getClass() != o.getClass())
        return false;
      DeviceState that = (DeviceState)o;
      return device == that.device && state.equals(that.state);
    }

    @Override
    public int hashCode() {
      return Objects.hash(device, state);
    }
  }

  private static class State {
    private final Set<DeviceState> deviceStates;

    private State(Collection<DeviceState> deviceStates) {
      this.deviceStates = new HashSet<>(deviceStates);
    }
  }

}
