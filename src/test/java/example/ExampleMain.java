package example;

import example.utils.MyButtonLedShadowingFunction;
import example.utils.DummyDigitalAdapter;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.raspberry.physical.RaspberryPhysicalAdapter;
import it.wldt.adapter.raspberry.physical.RaspberryPhysicalAdapterConfiguration;
import it.wldt.adapter.raspberry.physical.gpio.DigitalPin;
import it.wldt.adapter.raspberry.physical.gpio.DigitalState;
import it.wldt.adapter.raspberry.physical.gpio.InputEvent;
import it.wldt.adapter.raspberry.physical.gpio.OutputAction;
import it.wldt.core.engine.DigitalTwin;
import it.wldt.core.engine.DigitalTwinEngine;
import it.wldt.exception.EventBusException;

import java.util.List;

public class ExampleMain {

    public static void main(String[] args) throws Exception {

        //Defining Pins Actions and Events
        DigitalPin ledPin = new DigitalPin(10, DigitalState.LOW);
        DigitalPin buttonPin = new DigitalPin(12, DigitalState.LOW);


        List<OutputAction> physicalActions = List.of(
                new OutputAction("ledOn", ledPin, DigitalState.HIGH),
                new OutputAction("ledOff", ledPin, DigitalState.LOW)
        );

        List<InputEvent> physicalEvents = List.of(
                new InputEvent("buttonPressed",buttonPin, DigitalState.HIGH, () -> {
                    try {
                        return new PhysicalAssetEventWldtEvent<>("buttonPressed");
                    } catch (EventBusException e) {
                        throw new RuntimeException(e);
                    }
                } )
        );


        RaspberryPhysicalAdapterConfiguration configuration = new RaspberryPhysicalAdapterConfiguration(physicalActions, physicalEvents);

        DigitalTwin dt = new DigitalTwin("raspberryTest", new MyButtonLedShadowingFunction());
        dt.addPhysicalAdapter(new RaspberryPhysicalAdapter("raspberry-adapter", configuration));
        dt.addDigitalAdapter(new DummyDigitalAdapter("dummy-adapter"));

        DigitalTwinEngine engine = new DigitalTwinEngine();
        engine.addDigitalTwin(dt);
        engine.startAll();
    }
}
