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
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class TriangleFanRenderingHelper extends AbstractRenderingHelper {
    private boolean _firstTriangle = false;

    public TriangleFanRenderingHelper(@NotNull DrawContext drawContext,
                                      Vector3d translation,
                                      Quaternionf rotation,
                                      @NotNull Supplier<ShaderProgram> shaderProgram,
                                      @NotNull Vector4f shaderColor) {
        super(drawContext, translation, null, rotation, VertexFormat.DrawMode.TRIANGLE_FAN,
                VertexFormats.POSITION_COLOR, shaderProgram, shaderColor);
    }

    public void drawFirstTriangleFan(@NotNull Vector3f pos1, int color1,
                                     @NotNull Vector3f pos2, int color2,
                                     @NotNull Vector3f pos3, int color3) {
        if (_firstTriangle)
            throw new IllegalStateException("First triangle already drawn");

        _bufferBuilder.vertex(_matrixStackPeek, pos1.x, pos1.y, pos1.z).color(color1);
        _bufferBuilder.vertex(_matrixStackPeek, pos2.x, pos2.y, pos2.z).color(color2);
        _bufferBuilder.vertex(_matrixStackPeek, pos3.x, pos3.y, pos3.z).color(color3);

        _firstTriangle = true;
    }

    public void drawTriangleFan(@NotNull Vector3f pos, int color) {
        if (!_firstTriangle)
            throw new IllegalStateException("First triangle not drawn");

        _bufferBuilder.vertex(_matrixStackPeek, pos.x, pos.y, pos.z).color(color);
    }
}
