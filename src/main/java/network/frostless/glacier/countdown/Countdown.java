package network.frostless.glacier.countdown;

public interface Countdown {

    /**
     * Called when the countdown is cancelled.
     */
    void onCancel();

    /**
     * Called every time the countdown is ticked.
     * @param curr The current time that has passed.
     */
    void onCount(int curr);

    /**
     * Called when the countdown is finished.
     */
    void start();
}
