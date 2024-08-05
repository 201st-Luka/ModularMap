package luka.modularmap.gui.widgets;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.ChunkPos;

import java.util.Set;

public class ChunksWidget extends ClickableWidget {
    private final Set<ChunkPos> loadedChunks, unloadedChunks;
    private final ClientPlayerEntity player;
    private double scale, deltaXcache, deltaZchache;
    private int centerX, centerZ;
    private int screenWidth;

    public ChunksWidget(ClientPlayerEntity player, Set<ChunkPos> loadedChunks, Set<ChunkPos> unloadedChunks,
                        int width, int screenWidth, int screenHeight) {
        super(0, 0, width, screenHeight, null);

        this.player = player;
        this.loadedChunks = loadedChunks;
        this.unloadedChunks = unloadedChunks;
        this.scale = 1;

        this.centerX = width / 2;
        this.centerZ = height / 2;

        this.screenWidth = screenWidth;
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

    @Override
    protected void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
        int playerRelChunkX = player.getBlockX() >= 0 ? player.getBlockX() % 16 : 16 + player.getBlockX() % 16,
                playerRelChunkZ = player.getBlockZ() >= 0 ? player.getBlockZ() % 16 : 16 + player.getBlockZ() % 16;

        // render chunks
        for (ChunkPos chunk : loadedChunks)
            drawChunk(context, chunk, player.getChunkPos(), playerRelChunkX, playerRelChunkZ, 0x8000FF00);
        for (ChunkPos chunk : unloadedChunks)
            drawChunk(context, chunk, player.getChunkPos(), playerRelChunkX, playerRelChunkZ, 0x80FF0000);

        drawChunk(context, player.getChunkPos(), player.getChunkPos(), playerRelChunkX, playerRelChunkZ, 0x800000FF);

        // render grid
        if (scale >= 0.7) {
            for (int i = 0, x = calculateGridX(i, playerRelChunkX);
                 x <= screenWidth;
                 x = calculateGridX(++i, playerRelChunkX)) {
                context.drawVerticalLine(
                        x,
                        -1, height,
                        0x40000000
                );
            }
            for (int i = 0, z = calculateGridZ(i, playerRelChunkZ);
                 z <= height;
                 z = calculateGridZ(++i, playerRelChunkZ)) {
                for (int j = 0, x = calculateGridX(j, playerRelChunkX);
                     x <= screenWidth;
                     x = calculateGridX(++j, playerRelChunkX)) {
                    context.drawHorizontalLine(
                            x + 1, (int) (x + 16 * scale),
                            z,
                            0x40000000
                    );
                }
            }

        }

        // render player
        context.fill(centerX - 2, centerZ - 2, centerX + 2, centerZ + 2, 0xFF0000FF);
    }

    @Override
	protected void onDrag(double mouseX, double mouseY, double deltaX, double deltaY) {
        deltaXcache += deltaX;
        deltaZchache += deltaY;
        int dx = (int) deltaXcache,
                dy = (int) deltaZchache;
        centerX += dx;
        centerZ += dy;
        deltaXcache -= dx;
        deltaZchache -= dy;
	}

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        double change = verticalAmount * 0.1;
        if (0.1 < scale + change && scale + change < 8)
            scale += change;
        return true;
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {
    }
}
