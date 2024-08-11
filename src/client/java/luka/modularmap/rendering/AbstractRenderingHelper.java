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

package luka.modularmap.rendering;

import com.google.common.base.Supplier;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.ShaderProgram;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BuiltBuffer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.util.math.MatrixStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.function.Consumer;

/**
 * Abstract class for rendering helper classes.
 * <p>
 * Do not create 2 instances of this class or its subclasses at the same time. Unexpected behavior may occur.
 */
public abstract class AbstractRenderingHelper {
    protected final Tessellator _tessellator;
    protected final DrawContext _drawContext;
    protected final MatrixStack _matrices;
    protected final Matrix4f _transformationMatrix;
    protected final BufferBuilder _bufferBuilder;

    public void render(Consumer<BuiltBuffer> program) {
        try {
            program.accept(_bufferBuilder.end());
        } catch (IllegalStateException ignored) {
            // buffer is empty, nothing to render
        }

        _matrices.pop();
    }

    public AbstractRenderingHelper(@NotNull DrawContext drawContext,
                                   @NotNull VertexFormat.DrawMode drawMode,
                                   @NotNull VertexFormat vertexFormat,
                                   @NotNull Supplier<ShaderProgram> shaderProgram) {
        _drawContext = drawContext;

        _matrices = _drawContext.getMatrices();
        _matrices.push();
        _transformationMatrix = _matrices.peek().getPositionMatrix();

        RenderSystem.setShader(shaderProgram);

        _tessellator = Tessellator.getInstance();

        _bufferBuilder = _tessellator.begin(drawMode, vertexFormat);
    }

    public AbstractRenderingHelper(DrawContext drawContext,
                                   @NotNull Vector3d translation,
                                   @NotNull VertexFormat.DrawMode drawMode,
                                   @NotNull VertexFormat vertexFormat,
                                   @NotNull Supplier<ShaderProgram> shaderProgram) {
        this(drawContext, drawMode, vertexFormat, shaderProgram);

        _matrices.translate(translation.x, translation.y, translation.z);
    }

    public AbstractRenderingHelper(DrawContext drawContext,
                                   @NotNull Vector3f scale,
                                   @NotNull VertexFormat.DrawMode drawMode,
                                   @NotNull VertexFormat vertexFormat,
                                   @NotNull Supplier<ShaderProgram> shaderProgram) {
        this(drawContext, drawMode, vertexFormat, shaderProgram);

        _matrices.scale(scale.x, scale.y, scale.z);
    }

    public AbstractRenderingHelper(DrawContext drawContext,
                                   @NotNull Vector3d translation,
                                   @NotNull Vector3f scale,
                                   @NotNull VertexFormat.DrawMode drawMode,
                                   @NotNull VertexFormat vertexFormat,
                                   @NotNull Supplier<ShaderProgram> shaderProgram) {
        this(drawContext, drawMode, vertexFormat, shaderProgram);

        _matrices.translate(translation.x, translation.y, translation.z);
        _matrices.scale(scale.x, scale.y, scale.z);
    }
}
