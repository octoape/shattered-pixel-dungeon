/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
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
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.actors.hero.spells;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.ShieldBuff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.cleric.AscendedForm;
import com.shatteredpixel.shatteredpixeldungeon.items.artifacts.HolyTome;
import com.shatteredpixel.shatteredpixeldungeon.sprites.CharSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.HeroIcon;

public class DivineIntervention extends ClericSpell {

	public static DivineIntervention INSTANCE = new DivineIntervention();

	@Override
	public int icon() {
		return HeroIcon.DIVINE_INTERVENTION;
	}

	@Override
	public float chargeUse(Hero hero) {
		return 2; //TODO
	}

	@Override
	public boolean canCast(Hero hero) {
		return super.canCast(hero) && hero.buff(AscendedForm.AscendBuff.class) != null;
	}

	@Override
	public void onCast(HolyTome tome, Hero hero) {

		//TODO vfx

		for (Char ch : Actor.chars()){
			if (ch.alignment == Char.Alignment.ALLY && ch != hero){
				Buff.affect(ch, DivineShield.class).setShield(150 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));
			}
		}

		hero.spendAndNext( 1f );
		onSpellCast(tome, hero);

		//we apply buffs here so that the 6 charge cost and shield boost do not stack
		hero.buff(AscendedForm.AscendBuff.class).setShield(150 + 50*hero.pointsInTalent(Talent.DIVINE_INTERVENTION));

	}

	public static class DivineShield extends ShieldBuff{

		@Override
		public boolean act() {

			if (Dungeon.hero == null || Dungeon.hero.buff(AscendedForm.AscendBuff.class) == null){
				detach();
			}

			spend(TICK);
			return true;
		}

		@Override
		public int shielding() {
			if (Dungeon.hero == null || Dungeon.hero.buff(AscendedForm.AscendBuff.class) == null){
				return 0;
			}
			return super.shielding();
		}

		@Override
		public void fx(boolean on) {
			if (on) target.sprite.add(CharSprite.State.SHIELDED);
			else    target.sprite.remove(CharSprite.State.SHIELDED);
		}
	}
}
