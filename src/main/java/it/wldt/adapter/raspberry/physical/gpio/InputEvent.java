package it.wldt.adapter.raspberry.physical.gpio;

import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetWldtEvent;

import java.util.function.Supplier;

public class InputEvent {

    private final String name;
    private final DigitalPin pin;
    private final DigitalState trigger;

    private Supplier<PhysicalAssetEventWldtEvent<String>> supplier;

    public InputEvent(String name, DigitalPin pin, DigitalState trigger, Supplier<PhysicalAssetEventWldtEvent<String>> supplier) {
        this.name = name;
        this.pin = pin;
        this.trigger = trigger;
    }

    public String getName() {
        return name;
    }

    public DigitalPin getPin() {
        return pin;
    }

    public DigitalState getTrigger() {
        return trigger;
    }

    public PhysicalAssetEventWldtEvent<?> getEvent(){
        return supplier.get();
    }
}
