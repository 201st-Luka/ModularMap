package luka.modularmap.gui.screens;

import luka.modularmap.event.KeyInputHandler;
import net.minecraft.client.gui.screen.Screen;

public class WaypointScreen extends MenuScreen {
    public WaypointScreen() {
        super("Waypoints");
    }

    public WaypointScreen(Screen parent) {
        super("Waypoints", parent);
    }

    @Override
    protected void init() {

    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyInputHandler.openWaypointsKey.matchesKey(keyCode, scanCode)) {
            close();
            return true;
        }

        if (KeyInputHandler.openMapKey.matchesKey(keyCode, scanCode) && parent instanceof MapScreen) {
            client.player.closeHandledScreen();
            return true;
        }

        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
