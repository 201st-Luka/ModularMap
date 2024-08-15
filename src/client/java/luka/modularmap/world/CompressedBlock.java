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

package luka.modularmap.world;

import net.minecraft.block.Block;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

public class CompressedBlock {
    private final Identifier _blockId;
    private final int _x, _y, _z;

    public CompressedBlock(Block block, int posX, int posY, int posZ) {
        _x = posX;
        _y = posY;
        _z = posZ;
        _blockId = Registries.BLOCK.getId(block);
    }

    public BlockPos getBlockPos() {
        return new BlockPos(_x, _y, _z);
    }

    public int getBlockX() {
        return _x;
    }

    public int getBlockY() {
        return _y;
    }

    public int getBlockZ() {
        return _z;
    }

    public Identifier getBlockId() {
        return _blockId;
    }

    public int getColor() {
        return Registries.BLOCK.get(_blockId).getDefaultMapColor().color | 0xFF000000;
    }
}