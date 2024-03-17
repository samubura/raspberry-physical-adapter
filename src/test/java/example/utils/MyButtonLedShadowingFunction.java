package example.utils;

import it.wldt.adapter.digital.event.DigitalActionWldtEvent;
import it.wldt.adapter.physical.PhysicalAssetDescription;
import it.wldt.adapter.physical.event.PhysicalAssetEventWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetPropertyWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceCreatedWldtEvent;
import it.wldt.adapter.physical.event.PhysicalAssetRelationshipInstanceDeletedWldtEvent;
import it.wldt.core.model.ShadowingFunction;
import it.wldt.core.state.DigitalTwinStateAction;
import it.wldt.core.state.DigitalTwinStateEvent;
import it.wldt.core.state.DigitalTwinStateProperty;
import it.wldt.exception.EventBusException;
import it.wldt.exception.WldtDigitalTwinStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class MyButtonLedShadowingFunction extends ShadowingFunction {

    private static final Logger logger = LoggerFactory.getLogger(MyButtonLedShadowingFunction.class);

    private boolean ledState;

    public MyButtonLedShadowingFunction() {
        super("my-button-led-shadowing-function");
    }

    @Override
    protected void onCreate() {
        ledState = false;
    }

    @Override
    protected void onStart() {

    }

    @Override
    protected void onStop() {

    }

    @Override
    protected void onDigitalTwinBound(Map<String, PhysicalAssetDescription> adaptersPhysicalAssetDescriptionMap) {

        // NEW from 0.3.0 -> Start DT State Change Transaction
        this.digitalTwinStateManager.startStateTransaction();

        //Create a new digital property for the led state
        try {
            digitalTwinStateManager.createProperty(new DigitalTwinStateProperty<>("ledState", ledState));
        } catch (WldtDigitalTwinStateException e) {
            throw new RuntimeException(e);
        }

        //Iterate over all the received PAD from connected Physical Adapters
        adaptersPhysicalAssetDescriptionMap.values().forEach(pad -> {
            pad.getProperties().forEach(property -> {
                try {

                    //Create and write the property on the DT's State
                    this.digitalTwinStateManager.createProperty(new DigitalTwinStateProperty<>(property.getKey(), (Double) property.getInitialValue()));

                    //Start observing the variation of the physical property in order to receive notifications
                    //Without this call the Shadowing Function will not receive any notifications or callback about
                    //incoming physical property of the target type and with the target key
                    this.observePhysicalAssetProperty(property);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            //Iterate over available declared Physical Events for the target Physical Adapter's PAD
            pad.getEvents().forEach(event -> {
                try {

                    //Instantiate a new DT State Event with the same key and type
                    DigitalTwinStateEvent dtStateEvent = new DigitalTwinStateEvent(event.getKey(), event.getType());

                    //Create and write the event on the DT's State
                    this.digitalTwinStateManager.registerEvent(dtStateEvent);

                    //Start observing the variation of the physical event in order to receive notifications
                    //Without this call the Shadowing Function will not receive any notifications or callback about
                    //incoming physical events of the target type and with the target key
                    this.observePhysicalAssetEvent(event);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            //Iterate over available declared Physical Actions for the target Physical Adapter's PAD
            pad.getActions().forEach(action -> {
                try {

                    //Instantiate a new DT State Action with the same key and type
                    DigitalTwinStateAction dtStateAction = new DigitalTwinStateAction(action.getKey(), action.getType(), action.getContentType());

                    //Enable the action on the DT's State
                    this.digitalTwinStateManager.enableAction(dtStateAction);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

        });

        try {
            digitalTwinStateManager.commitStateTransaction();
        } catch (WldtDigitalTwinStateException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected void onDigitalTwinUnBound(Map<String, PhysicalAssetDescription> map, String s) {

    }

    @Override
    protected void onPhysicalAdapterBidingUpdate(String s, PhysicalAssetDescription physicalAssetDescription) {

    }

    @Override
    protected void onPhysicalAssetPropertyVariation(PhysicalAssetPropertyWldtEvent<?> physicalAssetPropertyWldtEvent) {

    }

    @Override
    protected void onPhysicalAssetEventNotification(PhysicalAssetEventWldtEvent<?> physicalAssetEventWldtEvent) {
        if(physicalAssetEventWldtEvent.getPhysicalEventKey().equals("buttonPressed")) {

            //toggle behaviour
            String actionKey = ledState ? "ledOff" : "ledOn";
            ledState = !ledState;

            try {
                //send action to physical adapter
                publishPhysicalAssetActionWldtEvent(actionKey, "");

                //update DT state
                this.digitalTwinStateManager.startStateTransaction();
                this.digitalTwinStateManager.updateProperty(new DigitalTwinStateProperty<>("ledState", ledState));
                this.digitalTwinStateManager.commitStateTransaction();

            } catch (EventBusException | WldtDigitalTwinStateException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void onPhysicalAssetRelationshipEstablished(PhysicalAssetRelationshipInstanceCreatedWldtEvent<?> physicalAssetRelationshipInstanceCreatedWldtEvent) {

    }

    @Override
    protected void onPhysicalAssetRelationshipDeleted(PhysicalAssetRelationshipInstanceDeletedWldtEvent<?> physicalAssetRelationshipInstanceDeletedWldtEvent) {

    }

    @Override
    protected void onDigitalActionEvent(DigitalActionWldtEvent<?> digitalActionWldtEvent) {

    }
}