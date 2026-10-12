package forge.game.staticability;

import forge.game.Game;
import forge.game.card.Card;
import forge.game.player.Player;
import forge.game.zone.ZoneType;

public class StaticAbilitySurveilNum {

    public static int surveilNumMod(Player p) {
        return surveilNumMod(p, (Integer) null);
    }

    public static int surveilNumMod(Player p, int baseNum) {
        return surveilNumMod(p, Integer.valueOf(baseNum));
    }

    private static int surveilNumMod(Player p, Integer baseNum) {
        final Game game = p.getGame();
        int mod = 0;
        for (final Card ca : game.getCardsIn(ZoneType.STATIC_ABILITIES_SOURCE_ZONES)) {
            for (final StaticAbility stAb : ca.getStaticAbilities()) {
                if (!stAb.checkConditions(StaticAbilityMode.SurveilNum)) {
                    continue;
                }
                // The current count includes every previously accepted static modifier.
                mod += getSurveilMod(stAb, p, baseNum == null ? null : baseNum + mod);
            }
        }
        return mod;
    }

    public static int getSurveilMod(final StaticAbility stAb, final Player p) {
        return getSurveilMod(stAb, p, (Integer) null);
    }

    public static int getSurveilMod(final StaticAbility stAb, final Player p, final int currentNum) {
        return getSurveilMod(stAb, p, Integer.valueOf(currentNum));
    }

    private static int getSurveilMod(final StaticAbility stAb, final Player p, final Integer currentNum) {
        if (!stAb.matchesValidParam("ValidPlayer", p)) {
            return 0;
        }
        final int modifier = Integer.parseInt(stAb.getParam("Num"));
        if (stAb.hasParam("Optional")) {
            // Legacy callers have no base count; retain their description-only prompt.
            final String confirmation = currentNum == null
                    ? stAb + "?"
                    : "Surveil " + (currentNum + modifier) + " instead of " + currentNum + "? " + stAb;
            if (!p.getController().confirmStaticApplication(stAb.getHostCard(), null, confirmation, null)) {
                return 0;
            }
        }
        return modifier;
    }

}
