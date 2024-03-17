package it.wldt.adapter.raspberry.physical.gpio;

public class DigitalPin {

    private final int number;
    private final DigitalState shutdownState;

    public DigitalPin(int number, DigitalState shutdownState) {
        this.number = number;
        this.shutdownState = shutdownState;
    }

    public int getNumber() {
        return number;
    }

    public DigitalState getShutdownState() {
        return shutdownState;
    }
}
