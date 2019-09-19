package me.uniodex.uniofactions.utils.packages.menubuilder;

import org.bukkit.entity.HumanEntity;

public abstract class MenuBuilder<T> {

    public MenuBuilder() {
    }

    /**
     * Shows the Menu to the viewers
     */
    public abstract MenuBuilder show(HumanEntity... viewers);

    /**
     * Refreshes the content of the menu
     */
    public abstract MenuBuilder refreshContent();

    /**
     * Builds the menu
     */
    @SuppressWarnings({"TypeParameterHidesVisibleType"})
    public abstract <T> T build();

    public abstract void dispose();

}
