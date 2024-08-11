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

package luka.modularmap.config;

import org.jetbrains.annotations.NotNull;

public class Color implements Cloneable {
    private int _value;

    public Color(int value) {
        _value = value;
    }

    public Color(byte r, byte g, byte b, byte a) {
        this(a << 24 | r << 16 | g << 8 | b);
    }

    public Color(@NotNull String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        else if (hex.startsWith("0x"))
            hex = hex.substring(2);

        _value = Integer.parseUnsignedInt(hex, 16);
    }

    public int getValue() {
        return _value;
    }

    public byte getRed() {
        return (byte) (_value >> 16 & 0xFF);
    }

    public byte getGreen() {
        return (byte) (_value >> 8 & 0xFF);
    }

    public byte getBlue() {
        return (byte) (_value & 0xFF);
    }

    public byte getAlpha() {
        return (byte) (_value >> 24 & 0xFF);
    }

    public void setValue(int value) {
        this._value = value;
    }

    public void setRed(byte r) {
        _value = (_value & 0xFF00FFFF) | (r << 16);
    }

    public void setGreen(byte g) {
        _value = (_value & 0xFFFF00FF) | (g << 8);
    }

    public void setBlue(byte b) {
        _value = (_value & 0xFFFFFF00) | b;
    }

    public void setAlpha(byte a) {
        _value = (_value & 0x00FFFFFF) | (a << 24);
    }

    public void setHex(@NotNull String hex) {
        if (hex.startsWith("#"))
            hex = hex.substring(1);
        else if (hex.startsWith("0x"))
            hex = hex.substring(2);

        _value = Integer.parseUnsignedInt(hex, 16);
    }

    public String toHex() {
        return Integer.toHexString(_value);
    }

    @Override
    public String toString() {
        return toHex();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;

        if (obj == null || getClass() != obj.getClass())
            return false;

        var color = (Color) obj;
        return _value == color._value;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(_value);
    }

    @Override
    public Color clone() {
        try {
            return (Color) super.clone();

        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
