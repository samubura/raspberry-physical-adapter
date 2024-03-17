package it.wldt.adapter.raspberry.physical;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;
import com.pi4j.io.gpio.digital.*;
import it.wldt.adapter.physical.ConfigurablePhysicalAdapter;
import it.wldt.adapter.physical.PhysicalAssetAction;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.PhysicalAssetEvent;
import it.wldt.adapter.physical.event.PhysicalAssetActionWldtEvent;
import it.wldt.adapter.raspberry.physical.gpio.OutputAction;
import it.wldt.exception.EventBusException;
import it.wldt.exception.PhysicalAdapterException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class RaspberryPhysicalAdapter extends ConfigurablePhysicalAdapter<RaspberryPhysicalAdapterConfiguration> {

    private final Context pi4j;

    private final RaspberryPhysicalAdapterConfiguration configuration;

    public RaspberryPhysicalAdapter(String id, RaspberryPhysicalAdapterConfiguration configuration) {
        super(id, configuration);
        this.configuration = configuration;
        this.pi4j = Pi4J.newAutoContext();
    }


    @Override
    public void onIncomingPhysicalAction(PhysicalAssetActionWldtEvent<?> physicalActionEvent) {
        OutputAction action = configuration.getOutputActions().getOrDefault(physicalActionEvent.getActionKey(), null);
        if (action != null && pi4j.hasIO(String.valueOf(action.getPin()))){
            DigitalOutput output = pi4j.io(String.valueOf(action.getPin()));
            output.state(convertToPi4JState(action.getState()));
        }
    }

    @Override
    public void onAdapterStart() {

        List<PhysicalAssetAction> actions = configuration.getOutputActions().keySet()
                .stream().map(x -> new PhysicalAssetAction(x, "digital-output", "string")).collect(Collectors.toList());

        List<PhysicalAssetEvent> events = configuration.getInputEvents()
                .stream().map(x -> new PhysicalAssetEvent(x.getName(), "digital-input")).collect(Collectors.toList());

        configuration.getOutputPins().forEach(pin -> {
            if(!pi4j.hasIO(String.valueOf(pin.getNumber()))){
                DigitalOutput output = pi4j.digitalOutput().create(pin.getNumber(), String.valueOf(pin.getNumber()));
                output.config().shutdownState(convertToPi4JState(pin.getShutdownState()));
            }
        });

        try {
        configuration.getInputEvents().forEach(event -> {
            DigitalInputConfig config = DigitalInput.newConfigBuilder(pi4j)
                    .address(event.getPin().getNumber())
                    .pull(convertTriggerToPi4JPull(event.getTrigger()))
                    .build();

            pi4j.digitalInput()
                    .create(config)
                    .addListener(x -> {
                        try {
                            publishPhysicalAssetEventWldtEvent(event.getEvent());
                        } catch (EventBusException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
            this.notifyPhysicalAdapterBound(new PhysicalAssetDescription(actions, new ArrayList<>(), events));

        } catch (PhysicalAdapterException | EventBusException e) {
            throw new RuntimeException(e);
        }
    }

    private PullResistance convertTriggerToPi4JPull(it.wldt.adapter.raspberry.physical.gpio.DigitalState trigger) {
        switch(trigger){
            case LOW: return PullResistance.PULL_UP;
            case HIGH: return PullResistance.PULL_DOWN;
            default: return PullResistance.OFF;
        }
    }

    @Override
    public void onAdapterStop() {
        pi4j.shutdown();
    }

    private DigitalState convertToPi4JState(it.wldt.adapter.raspberry.physical.gpio.DigitalState shutdownState) {
        switch(shutdownState){
            case LOW: return DigitalState.LOW;
            case HIGH: return DigitalState.HIGH;
            default: return DigitalState.UNKNOWN;
        }
    }
}
