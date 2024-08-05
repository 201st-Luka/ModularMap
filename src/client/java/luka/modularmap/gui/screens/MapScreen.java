package luka.modularmap.gui.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import luka.modularmap.ModularMapClientEntryPoint;
import luka.modularmap.config.ConfigManager;
import luka.modularmap.event.KeyInputHandler;
import luka.modularmap.map.ChunkManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ButtonTextures;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TexturedButtonWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.*;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.ChunkPos;
import org.joml.Matrix4f;

import java.util.Set;

@Environment(EnvType.CLIENT)
public class MapScreen extends BaseScreen {
    private static final int GRID_COLOR = 0x40000000;
    private final Set<ChunkPos> loadedChunks, unloadedChunks;
    private final ClientPlayerEntity player;
    private double scale, deltaXCache, deltaZCache;
    private int centerX, centerZ;
    private boolean isInitialized = false;

    public MapScreen() {
        super("Map Screen");

        player = MinecraftClient.getInstance().player;

        ChunkManager chunkManager = modularMapClient.modularMap$getChunkManager();
        loadedChunks = chunkManager.getLoadedChunks();
        unloadedChunks = chunkManager.getUnloadedChunks();

        scale = 1;
    }

    @Override
    protected void init() {
        if (!isInitialized) {
            isInitialized = true;

            centerX = width / 2;
            centerZ = height / 2;
        }

        ButtonWidget configButton = new TexturedButtonWidget(
                width - FRAME_SPACING - BUTTON_SIZE, height - FRAME_SPACING - BUTTON_SIZE,
                BUTTON_SIZE, BUTTON_SIZE,
                new ButtonTextures(
                    Identifier.of(ModularMapClientEntryPoint.MOD_ID, "map/buttons/config"),
                    Identifier.of(ModularMapClientEntryPoint.MOD_ID, "map/buttons/config_highlighted")
                ),
                button -> client.setScreen(new ConfigScreen(this)),
                Text.literal("Configuration")
        ),
                waypointButton = new TexturedButtonWidget(
                        width - FRAME_SPACING - BUTTON_SIZE, height - FRAME_SPACING - BUTTON_SIZE * 2 - PADDING,
                        BUTTON_SIZE, BUTTON_SIZE,
                        new ButtonTextures(
                                Identifier.of(ModularMapClientEntryPoint.MOD_ID, "map/buttons/waypoints"),
                                Identifier.of(ModularMapClientEntryPoint.MOD_ID, "map/buttons/waypoints_highlighted")
                        ),
                        button -> client.setScreen(new WaypointScreen(this)),
                        Text.literal("Waypoints")
                );

        addDrawableChild(configButton);
        addDrawableChild(waypointButton);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (KeyInputHandler.openMapKey.matchesKey(keyCode, scanCode)) {
            player.closeHandledScreen();
            return true;
        } else if (KeyInputHandler.openWaypointsKey.matchesKey(keyCode, scanCode)) {
            client.setScreen(new WaypointScreen(this));
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private int calculatePlayerRelChunkX() {
        return player.getBlockX() >= 0 ? player.getBlockX() % 16 : 16 + player.getBlockX() % 16;
    }

    private int calculatePlayerRelChunkZ() {
        return player.getBlockZ() >= 0 ? player.getBlockZ() % 16 : 16 + player.getBlockZ() % 16;
    }

    private int calculateMapXStart(int chunkX, int playerChunkX, int playerRelX) {
        return (int) (((chunkX - playerChunkX) * 16 - playerRelX) * scale + centerX);
    }

    private int calculateMapZStart(int chunkZ, int playerChunkZ, int playerRelZ) {
        return (int) (((chunkZ - playerChunkZ) * 16 - playerRelZ) * scale + centerZ);
    }

    private int calculateMapXEnd(int chunkX, int playerChunkX, int playerRelX) {
        return (int) (((chunkX - playerChunkX) * 16 - playerRelX + 16) * scale + centerX);
    }

    private int calculateMapZEnd(int chunkZ, int playerChunkZ, int playerRelZ) {
        return (int) (((chunkZ - playerChunkZ) * 16 - playerRelZ + 16) * scale + centerZ);
    }

    private int calculateGridX(int x, int playerRelX) {
        return (int) ((x * 16 - playerRelX) * scale + centerX % (16 * scale));
    }

    private int calculateGridZ(int z, int playerRelZ) {
        return (int) ((z * 16 - playerRelZ) * scale + centerZ % (16 * scale));
    }

    private void drawChunk(DrawContext context,
                           ChunkPos chunk, ChunkPos playerChunk,
                           int playerRelX, int playerRelZ,
                           int color) {
        context.fill(
                calculateMapXStart(chunk.x, playerChunk.x, playerRelX),
                calculateMapZStart(chunk.z, playerChunk.z, playerRelZ),
                calculateMapXEnd(chunk.x, playerChunk.x, playerRelX),
                calculateMapZEnd(chunk.z, playerChunk.z, playerRelZ),
                color
        );
    }

    private void renderChunks(DrawContext context, int playerRelChunkX, int playerRelChunkZ) {
        // render loaded chunks
        for (ChunkPos chunk : loadedChunks)
            drawChunk(context, chunk, player.getChunkPos(), playerRelChunkX, playerRelChunkZ, 0x8000FF00);
        // render unloaded chunks
        for (ChunkPos chunk : unloadedChunks)
            drawChunk(context, chunk, player.getChunkPos(), playerRelChunkX, playerRelChunkZ, 0x80FF0000);
    }

    private void renderGrid(DrawContext context, int playerRelChunkX, int playerRelChunkZ) {
        if (scale >= 0.7) {
            Matrix4f transformationMatrix = context.getMatrices().peek().getPositionMatrix();
            Tessellator tessellator = Tessellator.getInstance();

            BufferBuilder buffer = tessellator.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            // vertical lines
            for (int i = 0, x = calculateGridX(i, playerRelChunkX);
                 x <= width;
                 x = calculateGridX(++i, playerRelChunkX)) {
                buffer.vertex(transformationMatrix, x, 0, 0).color(GRID_COLOR);
                buffer.vertex(transformationMatrix, x, height, 0).color(GRID_COLOR);
            }
            // horizontal lines
            for (int i = 0, z = calculateGridZ(i, playerRelChunkZ);
                 z <= height;
                 z = calculateGridZ(++i, playerRelChunkZ)) {
                buffer.vertex(transformationMatrix, 0, z, 0).color(GRID_COLOR);
                buffer.vertex(transformationMatrix, width, z, 0).color(GRID_COLOR);
            }

            RenderSystem.setShader(GameRenderer::getPositionColorProgram);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

            BufferRenderer.drawWithGlobalProgram(buffer.end());
        }
    }

    @SuppressWarnings("unused")
    private void renderMap(DrawContext context, int mouseX, int mouseY, float delta) {
        // calculate player relative chunk position
        int playerRelChunkX = calculatePlayerRelChunkX(),
                playerRelChunkZ = calculatePlayerRelChunkZ();

        // render chunks
        renderChunks(context, playerRelChunkX, playerRelChunkZ);

        // draw current player chunk
        drawChunk(context, player.getChunkPos(), player.getChunkPos(), playerRelChunkX, playerRelChunkZ, 0x800000FF);

        // render grid
        renderGrid(context, playerRelChunkX, playerRelChunkZ);

        // render player
        context.fill(centerX - 2, centerZ - 2, centerX + 2, centerZ + 2, 0xFF0000FF);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // background
        renderBackground(context, mouseX, mouseY, delta);
        context.fill(0, 0, width, height, ConfigManager.getConfig().backgroundColor.getValue());

        // map
        renderMap(context, mouseX, mouseY, delta);

        // widgets (buttons, ...)
        for (Drawable drawable : drawables)
            drawable.render(context, mouseX, mouseY, delta);
    }

    @SuppressWarnings("unused")
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        // update cache to get more precise dragging
        deltaXCache += deltaX;
        deltaZCache += deltaY;

        // calculate int ratio
        int dx = (int) deltaXCache,
                dy = (int) deltaZCache;

        // update center
        centerX += dx;
        centerZ += dy;

        // update cache
        deltaXCache -= dx;
        deltaZCache -= dy;
	}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double change = verticalAmount * 0.1;
        if (0.1 < scale + change && scale + change < 8)
            scale += change;
        return true;
    }

    @Override
	public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
		if (isLeftClickButton(button)) {
			onDrag(mouseX, mouseY, deltaX, deltaY);
			return true;
		} else {
			return false;
		}
	}
}
