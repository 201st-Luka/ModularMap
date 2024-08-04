package luka.modularmap.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class LockableTexturedButtonWidget extends TexturedButtonWidget {
    private boolean locked = false;

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction) {
        super(x, y, width, height, textures, pressAction);
    }

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures, PressAction pressAction, Text text) {
        super(x, y, width, height, textures, pressAction, text);
    }

    public LockableTexturedButtonWidget(int width, int height, ButtonTextures textures, PressAction pressAction, Text text) {
        super(width, height, textures, pressAction, text);
    }

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures,
                                        PressAction pressAction, boolean locked) {
        this(x, y, width, height, textures, pressAction);

        locked = locked;
    }

    public LockableTexturedButtonWidget(int x, int y, int width, int height, ButtonTextures textures,
                                        PressAction pressAction, Text text, boolean locked) {
        this(x, y, width, height, textures, pressAction, text);

        locked = locked;
    }

    public LockableTexturedButtonWidget(int width, int height, ButtonTextures textures, PressAction pressAction,
                                        Text text, boolean locked) {
        this(width, height, textures, pressAction, text);

        locked = locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void toggleLocked() {
        locked = !locked;
    }

    public void lock() {
        locked = true;
    }

    public void unlock() {
        locked = false;
    }

    public boolean isLocked() {
        return locked;
    }

    @Override
	public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        Identifier identifier = textures.get(locked, isSelected());
		context.drawGuiTexture(identifier, getX(), getY(), width, height);
    }
}
