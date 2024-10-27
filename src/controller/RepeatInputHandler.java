package controller;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Waits for repeats of the given input command until {@link #WAIT_AMOUNT_MS} has elapsed from last such input,
 * before proceeding with the remainder of the command series. Other commands will be rejected during this wait time.
 */
public class RepeatInputHandler {

  private static final RepeatInputHandler INSTANCE = new RepeatInputHandler();

  private static final long WAIT_AMOUNT_MS = 300L;
  private static final boolean PRINT_DEBUG = true;

  private volatile String commandToRepeat;
  private String commandAwaitingRepeats;
  private Timer repeatInputTimer;
  private Consumer<String> consumer;
  private List<String> remainingCommands;

  static RepeatInputHandler get() {
    return INSTANCE;
  }

  private RepeatInputHandler() {
  }

  void waitUntilReady() {
    int i = 0;
    while (commandToRepeat != null) {
      try {
        Thread.sleep(WAIT_AMOUNT_MS / 4);
      } catch (InterruptedException e) {
        e.printStackTrace(System.err);
      }
      // Don't block forever in an unexpected case:
      if (i++ > 10)
        break;
    }
  }

  enum Result {
    UNHANDLED,
    ACCEPTED,
    REJECTED;
  }

  synchronized Result processIfRepeat(String command) {
    if (commandToRepeat != null) {
      assert commandAwaitingRepeats != null;
      assert repeatInputTimer != null;
      assert consumer != null;
      if (commandAwaitingRepeats.equals(command)) {
        repeatInputTimer.cancel();
        repeatInputTimer = new Timer();
        repeatInputTimer.schedule(new TimerTask() {
          @Override
          public void run() {
            processRemaining();
          }
        }, WAIT_AMOUNT_MS);

        consumer.accept(commandToRepeat);
        return Result.ACCEPTED;
      } else {
        if (PRINT_DEBUG)
          System.out.println("Input rejected: " + command);
        return Result.REJECTED;
      }
    }
    return Result.UNHANDLED;
  }

  synchronized void awaitRepeats(String commandAwaitingRepeats, String commandToRepeat, List<String> remainingCommands, Consumer<String> consumer) {
    this.commandAwaitingRepeats = commandAwaitingRepeats;
    this.commandToRepeat = commandToRepeat;
    this.consumer = consumer;
    this.remainingCommands = remainingCommands;

    repeatInputTimer = new Timer();
    repeatInputTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        processRemaining();
      }
    }, WAIT_AMOUNT_MS);

    if (PRINT_DEBUG)
      System.out.println("*Waiting for repeated inputs*");
  }

  private synchronized void processRemaining() {
    if (PRINT_DEBUG)
      System.out.println("*Stopped waiting for repeated inputs*");

    for (String command : remainingCommands) {
      consumer.accept(command);
    }

    remainingCommands = null;
    consumer = null;
    commandToRepeat = null;
    commandAwaitingRepeats = null;
    repeatInputTimer.cancel();
  }

}
