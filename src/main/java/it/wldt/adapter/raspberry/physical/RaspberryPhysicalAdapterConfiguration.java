package it.wldt.adapter.raspberry.physical;

import it.wldt.adapter.raspberry.physical.gpio.DigitalPin;
import it.wldt.adapter.raspberry.physical.gpio.InputEvent;
import it.wldt.adapter.raspberry.physical.gpio.OutputAction;


import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class RaspberryPhysicalAdapterConfiguration {

    private final List<OutputAction> outputActions;

    private final List<InputEvent> inputEvents;

    public RaspberryPhysicalAdapterConfiguration(List<OutputAction> actions, List<InputEvent> events){
        this.outputActions = actions;
        this.inputEvents = events;
    }

    public List<DigitalPin> getOutputPins(){
        return outputActions.stream().map(OutputAction::getPin).collect(Collectors.toList());
    }

    public Map<String, OutputAction> getOutputActions(){
        return outputActions.stream().collect(Collectors.toMap(OutputAction::getName, Function.identity()));
    }

    public List<InputEvent> getInputEvents(){
        return inputEvents;
    }

}

