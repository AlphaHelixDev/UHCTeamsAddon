package de.alphahelix.teamsaddon.instances;

import de.alphahelix.alphalibary.file.SimpleFile;

public class TeamOptions {

    private String guiName, toggleSymbol;
    private boolean teamVictory;
    private SimpleFile.InventoryItem icon;

    public TeamOptions(String guiName, String toggleSymbol, boolean teamVictory, SimpleFile.InventoryItem icon) {
        this.guiName = guiName;
        this.toggleSymbol = toggleSymbol;
        this.teamVictory = teamVictory;
        this.icon = icon;
    }

    public String getGuiName() {
        return guiName;
    }

    public SimpleFile.InventoryItem getIcon() {
        return icon;
    }

    public String getToggleSymbol() {
        return toggleSymbol;
    }

    public boolean isTeamVictory() {
        return teamVictory;
    }
}
