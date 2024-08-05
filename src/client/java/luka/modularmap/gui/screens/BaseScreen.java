package luka.modularmap.gui.screens;

import com.google.common.collect.Lists;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

import java.util.List;

public class BaseScreen extends Screen {
    protected final Screen parent;
    protected final List<Drawable> drawables = Lists.<Drawable>newArrayList();

    public static final int FRAME_SPACING = 6,
            PADDING = 6,
            BUTTON_SIZE = 24,
            TEXT_WIDTH = 120,
            TEXT_HEIGHT = 24;

    public BaseScreen(String title, Screen parent) {
        super(Text.literal(title));
        this.parent = parent;
    }

    @Override
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
		drawables.add(drawableElement);
		return addSelectableChild(drawableElement);
	}

    @Override
    protected <T extends Drawable> T addDrawable(T drawable) {
		drawables.add(drawable);
		return drawable;
	}


    public BaseScreen(String title) {
        this(title, null);
    }

    @Override
    public void close() {
        client.setScreen(parent);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
