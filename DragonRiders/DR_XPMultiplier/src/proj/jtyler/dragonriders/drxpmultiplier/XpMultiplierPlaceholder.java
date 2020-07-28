package proj.jtyler.dragonriders.drxpmultiplier;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class XpMultiplierPlaceholder extends PlaceholderExpansion {

	@Override
	public String getAuthor() {
		return "JTyler_";
	}

	@Override
	public String getIdentifier() {
		return "xpm";
	}

	@Override
	public String getVersion() {
		return "1.0";
	}
	
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {

        if (identifier.equals("level")) {
            return "" + XpMultiplier.instance.getMultiplierLevel(p);
        }

        return null;
    }

}
