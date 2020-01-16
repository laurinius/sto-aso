/*******************************************************************************
 * Copyright (C) 2015, 2019 Dave Kor
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.kor.admiralty.enums;

import static com.kor.admiralty.ui.resources.Strings.Shared.*;

public enum ShipPriority {
	Active("true"),
	OneTime("false"),
	OnlyActive("onlyActive");

	private String configValue;

	ShipPriority(String configValue) {
		this.configValue = configValue;
	}

	public String getConfigValue() {
		return this.configValue;
	}

	public static ShipPriority fromConfigValue(String configValue) {
		for (ShipPriority sp : values()) {
			if (sp.configValue.equals(configValue)) {
				return sp;
			}
		}
		throw new IllegalArgumentException(configValue);
	}

	@Override
	public String toString() {
		return toString(this);
	}
	
	protected static String toString(ShipPriority faction) {
		switch (faction) {
			case OneTime: return PriorityOneTime;
			case OnlyActive: return PriorityOnlyActive;
			case Active:
			default: return PriorityActive;
		}
	}
	
	public static ShipPriority fromString(String string) {
		if (string == null) {
	        throw new IllegalArgumentException();
		}
        for (ShipPriority faction : values()) {
            if (faction.toString().equalsIgnoreCase(string)) {
            	return faction;
            }
        }
        throw new IllegalArgumentException();
    }
	
}
