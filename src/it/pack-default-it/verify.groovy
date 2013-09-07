/*
 * Copyright (c) 2013 Monkey By Monkey.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

texture = new File(basedir, 'target/texturepack/pack.png');
atlas = new File(basedir, 'target/texturepack/pack.atlas');

assert texture.exists();
assert atlas.exists();

assert texture.length() > 0

atlasContent = atlas.text
assert atlasContent.contains('KoopaTroopaShellRed')
assert atlasContent.contains('MagicMushroom')
assert atlasContent.contains('MushroomRetainer')
assert atlasContent.contains('Princess')