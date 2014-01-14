package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractGBRomHandler.java - a base class for GB/GBA rom handlers		--*/
/*--                              which standardises common GB(A) functions.--*/
/*--  																		--*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew					--*/
/*--  Pokemon and any associated names and the like are						--*/
/*--  trademark and (C) Nintendo 1996-2012.									--*/
/*--  																		--*/
/*--  The custom code written here is licensed under the terms of the GPL:	--*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class AbstractGBRomHandler extends AbstractRomHandler {

	protected byte[] rom;

	public boolean detectRom(String filename) {
		if (new File(filename).length() > 32 * 1024 * 1024) {
			return false;
		}
		byte[] loaded = loadFile(filename);
		return detectRom(loaded);
	}

	@Override
	public boolean loadRom(String filename) {
		byte[] loaded = loadFile(filename);
		if (!detectRom(loaded)) {
			return false;
		}
		this.rom = loaded;
		loadedRom();
		return true;
	}

	@Override
	public boolean saveRom(String filename) {
		savingRom();
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			fos.write(rom);
			fos.close();
			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	@Override
	public boolean canChangeStaticPokemon() {
		return true;
	}

	public abstract boolean detectRom(byte[] rom);

	public abstract void loadedRom();

	public abstract void savingRom();

	private byte[] loadFile(String filename) {
		try {
			FileInputStream fis = new FileInputStream(filename);
			byte[] file = new byte[fis.available()];
			fis.read(file);
			fis.close();
			return file;
		} catch (IOException ex) {
			return new byte[0];
		}
	}

	protected void readByteIntoFlags(boolean[] flags, int offsetIntoFlags,
			int offsetIntoROM) {
		int thisByte = rom[offsetIntoROM] & 0xFF;
		for (int i = 0; i < 8 && (i + offsetIntoFlags) < flags.length; i++) {
			flags[offsetIntoFlags + i] = ((thisByte >> i) & 0x01) == 0x01;
		}
	}

	protected byte getByteFromFlags(boolean[] flags, int offsetIntoFlags) {
		int thisByte = 0;
		for (int i = 0; i < 8 && (i + offsetIntoFlags) < flags.length; i++) {
			thisByte |= (flags[offsetIntoFlags + i] ? 1 : 0) << i;
		}
		return (byte) thisByte;
	}

	protected int readWord(int offset) {
		return (rom[offset] & 0xFF) + ((rom[offset + 1] & 0xFF) << 8);
	}

	protected void writeWord(int offset, int value) {
		rom[offset] = (byte) (value % 0x100);
		rom[offset + 1] = (byte) ((value / 0x100) % 0x100);
	}

}
