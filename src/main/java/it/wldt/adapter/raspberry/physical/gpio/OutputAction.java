package it.wldt.adapter.raspberry.physical.gpio;

public class OutputAction {

    private final String name;
    private final DigitalPin pin;
    private final DigitalState state;

    public OutputAction(String name, DigitalPin pin, DigitalState state) {
        this.name = name;
        this.pin = pin;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public DigitalPin getPin() {
        return pin;
    }

    public DigitalState getState() {
        return state;
    }
}
