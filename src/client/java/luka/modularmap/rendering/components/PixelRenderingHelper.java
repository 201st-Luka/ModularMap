/*
 * ModularMap
 * Copyright (c) 2024 201st-Luka
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package luka.modularmap.rendering.components;

import com.google.common.base.Supplier;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class PixelRenderingHelper extends AbstractRenderingHelper {


    public PixelRenderingHelper(DrawContext drawContext,
                                @NotNull Vector3d translation,
                                @NotNull Vector3f scale,
                                @NotNull Supplier<ShaderProgram> shaderProgram,
                                @NotNull Vector4f shaderColor) {
        super(drawContext, translation, scale, null, VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR,
                shaderProgram, shaderColor);
    }

    public void drawPixel(float posX, float posY, float posZ, int color) {
        _bufferBuilder.vertex(_matrixStackPeek, posX, posY, posZ).color(color);
        _bufferBuilder.vertex(_matrixStackPeek, posX, posY + 1, posZ).color(color);
        _bufferBuilder.vertex(_matrixStackPeek, posX + 1, posY + 1, posZ).color(color);
        _bufferBuilder.vertex(_matrixStackPeek, posX + 1, posY, posZ).color(color);
    }

    public void drawPixel(float posX, float posY, int color) {
        drawPixel(posX, posY, 0, color);
    }
}
