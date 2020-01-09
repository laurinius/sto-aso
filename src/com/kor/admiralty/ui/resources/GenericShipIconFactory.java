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
package com.kor.admiralty.ui.resources;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.EnumMap;

import javax.swing.ImageIcon;

import com.kor.admiralty.enums.Rarity;
import com.kor.admiralty.enums.Role;
import com.kor.admiralty.enums.ShipFaction;
import com.kor.admiralty.io.Datastore;

public class GenericShipIconFactory implements ShipIconFactory {
	
	protected static final String URL_WEBICONS = "https://github.com/intrinsical/sto-aso/raw/master/icons/%s.png";
	protected static final EnumMap<ShipFaction, EnumMap<Role, EnumMap<Rarity, ImageIcon>>> CACHE = new EnumMap<ShipFaction, EnumMap<Role, EnumMap<Rarity, ImageIcon>>>(ShipFaction.class);
	
	protected static final int SPAN_IMAGE = Images.SPAN_IMAGE;
	protected static final Image IMG_LOBI = getImage("lobi.png");
	protected static final Image IMG_FED_BKG = getImage("fed_bkg.png");
	protected static final Image IMG_KDF_BKG = getImage("kdf_bkg.png");
	protected static final Image IMG_ROM_BKG = getImage("rom_bkg.png");
	protected static final Image IMG_JH_BKG = getImage("jh_bkg.png");
	protected static final Image IMG_FED_ENG = getImage("fed_eng.png");
	protected static final Image IMG_FED_TAC = getImage("fed_tac.png");
	protected static final Image IMG_FED_SCI = getImage("fed_sci.png");
	protected static final Image IMG_FED_SMC = getImage("fed_smc.png");
	protected static final Image IMG_KDF_ENG = getImage("kdf_eng.png");
	protected static final Image IMG_KDF_TAC = getImage("kdf_tac.png");
	protected static final Image IMG_KDF_SCI = getImage("kdf_sci.png");
	protected static final Image IMG_KDF_SMC = getImage("kdf_smc.png");
	protected static final Image IMG_ROM_ENG = getImage("rom_eng.png");
	protected static final Image IMG_ROM_TAC = getImage("rom_tac.png");
	protected static final Image IMG_ROM_SCI = getImage("rom_sci.png");
	protected static final Image IMG_ROM_SMC = getImage("rom_smc.png");
	protected static final Image IMG_JH_ENG = getImage("jh_eng.png");
	protected static final Image IMG_JH_TAC = getImage("jh_tac.png");
	protected static final Image IMG_JH_SCI = getImage("jh_sci.png");
	protected static final Image IMG_JH_SMC = getImage("jh_smc.png");
	protected static final Image IMG_EPIC_ENG = getImage("eng.png");
	protected static final Image IMG_EPIC_TAC = getImage("tac.png");
	protected static final Image IMG_EPIC_SCI = getImage("sci.png");
//	protected static final Image IMG_EPIC_SMC = getImage("tac.png");
	//protected static final Image IMG_COMMON = getImage("common.png");
	//protected static final Image IMG_UNCOMMON = getImage("uncommon.png");
	//protected static final Image IMG_RARE = getImage("rare.png");
	//protected static final Image IMG_VERYRARE = getImage("veryrare.png");
	//protected static final Image IMG_ULTRARARE = getImage("ultrarare.png");
	//protected static final Image IMG_EPIC = getImage("epic.png");
	
	
	static {
		for (ShipFaction faction : ShipFaction.values()) {
			CACHE.put(faction, new EnumMap<Role, EnumMap<Rarity, ImageIcon>>(Role.class));
			for (Role role : Role.values()) {
				CACHE.get(faction).put(role, new EnumMap<Rarity, ImageIcon>(Rarity.class));
			}
		}
	};
	
	public ImageIcon getIcon(String iconName, ShipFaction faction, Role role, Rarity rarity, boolean owned) {
		if (CACHE.get(faction).get(role).containsKey(rarity)) {
			return CACHE.get(faction).get(role).get(rarity);
		}
		
		BufferedImage image = new BufferedImage(SPAN_IMAGE, SPAN_IMAGE, BufferedImage.TYPE_INT_ARGB);
		Graphics g = image.getGraphics();
		switch (faction) {
		case Universal:
		case None:
			g.drawImage(IMG_FED_BKG, 0, 0, null);
			g.drawImage(IMG_LOBI, 0, 0, null);
			switch (role) {
			case Eng:
				g.drawImage(IMG_EPIC_ENG, 1, 1, null);
				break;
			case Tac:
				g.drawImage(IMG_EPIC_TAC, 1, 1, null);
				break;
			case Sci:
				g.drawImage(IMG_EPIC_SCI, 1, 1, null);
				break;
			case Smc:
			case None:
			default:
			}
			break;

		case Federation: 
			g.drawImage(IMG_FED_BKG, 0, 0, null);
			switch (role) {
			case Eng:
				g.drawImage(IMG_FED_ENG, 0, 0, null);
				break;
			case Tac:
				g.drawImage(IMG_FED_TAC, 0, 0, null);
				break;
			case Sci:
				g.drawImage(IMG_FED_SCI, 0, 0, null);
				break;
			case Smc:
				g.drawImage(IMG_FED_SMC, 0, 0, null);
				break;
			case None:
			default:
			}
			break;
			
		case Klingon:
			g.drawImage(IMG_KDF_BKG, 0, 0, null);
			switch (role) {
			case Eng:
				g.drawImage(IMG_KDF_ENG, 0, 0, null);
				break;
			case Tac:
				g.drawImage(IMG_KDF_TAC, 0, 0, null);
				break;
			case Sci:
				g.drawImage(IMG_KDF_SCI, 0, 0, null);
				break;
			case Smc:
				g.drawImage(IMG_KDF_SMC, 0, 0, null);
				break;
			case None:
			default:
			}
			break;
			
		case Romulan:
			g.drawImage(IMG_ROM_BKG, 0, 0, null);
			switch (role) {
			case Eng:
				g.drawImage(IMG_ROM_ENG, 0, 0, null);
				break;
			case Tac:
				g.drawImage(IMG_ROM_TAC, 0, 0, null);
				break;
			case Sci:
				g.drawImage(IMG_ROM_SCI, 0, 0, null);
				break;
			case Smc:
				g.drawImage(IMG_ROM_SMC, 0, 0, null);
				break;
			case None:
			default:
			}
			break;
			
		case JemHadar:
			g.drawImage(IMG_JH_BKG, 0, 0, null);
			switch (role) {
			case Eng:
				g.drawImage(IMG_JH_ENG, 0, 0, null);
				break;
			case Tac:
				g.drawImage(IMG_JH_TAC, 0, 0, null);
				break;
			case Sci:
				g.drawImage(IMG_JH_SCI, 0, 0, null);
				break;
			case Smc:
				g.drawImage(IMG_JH_SMC, 0, 0, null);
				break;
			case None:
			default:
			}
			break;
		}
		/*/
		switch (rarity) {
		case Common: 
			g.drawImage(IMG_COMMON,  0,  0,  null);
			break;
		case Uncommon: 
			g.drawImage(IMG_UNCOMMON,  0,  0,  null);
			break;
		case Rare: 
			g.drawImage(IMG_RARE,  0,  0,  null);
			break;
		case VeryRare: 
			g.drawImage(IMG_VERYRARE,  0,  0,  null);
			break;
		case UltraRare: 
			g.drawImage(IMG_ULTRARARE,  0,  0,  null);
			break;
		case Epic: 
			g.drawImage(IMG_EPIC,  0,  0,  null);
			break;
		case None:
		}
		//*/
		
		
		g.dispose();
		ImageIcon imageIcon = new ImageIcon(image);
		CACHE.get(faction).get(role).put(rarity, imageIcon);
		return imageIcon;
	}
	
	protected static BufferedImage getImage(String name) {
		return Datastore.getIcons().get(name);
	}
	
}
