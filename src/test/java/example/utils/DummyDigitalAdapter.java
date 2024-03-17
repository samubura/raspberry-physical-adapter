package example.utils;

import it.wldt.adapter.digital.DigitalAdapter;
import it.wldt.core.state.DigitalTwinState;
import it.wldt.core.state.DigitalTwinStateEventNotification;

import java.util.ArrayList;

public class DummyDigitalAdapter extends DigitalAdapter<String> {

    public DummyDigitalAdapter(String id) {
        super(id);
    }

    @Override
    protected void onStateUpdate(DigitalTwinState digitalTwinState, DigitalTwinState digitalTwinState1, ArrayList arrayList) {
        System.out.println(digitalTwinState.toString());
    }

    @Override
    protected void onEventNotificationReceived(DigitalTwinStateEventNotification digitalTwinStateEventNotification) {

    }

    @Override
    public void onAdapterStart() {

    }

    @Override
    public void onAdapterStop() {

    }

    @Override
    public void onDigitalTwinSync(DigitalTwinState digitalTwinState) {

    }

    @Override
    public void onDigitalTwinUnSync(DigitalTwinState digitalTwinState) {

    }

    @Override
    public void onDigitalTwinCreate() {

    }

    @Override
    public void onDigitalTwinStart() {

    }

    @Override
    public void onDigitalTwinStop() {

    }

    @Override
    public void onDigitalTwinDestroy() {

    }
}
