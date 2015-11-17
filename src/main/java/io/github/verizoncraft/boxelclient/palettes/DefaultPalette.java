package io.github.verizoncraft.boxelclient.palettes;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DefaultPalette implements Palette {
    private Logger logger = Bukkit.getLogger();
    private List<Pair <Material, DyeColor> > mColors;

    public DefaultPalette() {
        mColors = new ArrayList<Pair<Material, DyeColor>>();

        for (DyeColor dye : DyeColor.values()) {
            mColors.add(new ImmutablePair(Material.WOOL, dye));
        }
        for (DyeColor dye : DyeColor.values()) {
            mColors.add(new ImmutablePair(Material.STAINED_CLAY, dye));
        }
        mColors.add(new ImmutablePair(Material.SANDSTONE, null));
        mColors.add(new ImmutablePair(Material.IRON_BLOCK, null));
        mColors.add(new ImmutablePair(Material.GOLD_BLOCK, null));
        mColors.add(new ImmutablePair(Material.DIAMOND_BLOCK, null));
        mColors.add(new ImmutablePair(Material.SNOW_BLOCK, null));
        mColors.add(new ImmutablePair(Material.COAL_BLOCK, null));
        mColors.add(new ImmutablePair(Material.SLIME_BLOCK, null));
        mColors.add(new ImmutablePair(Material.PACKED_ICE, null));
        mColors.add(new ImmutablePair(Material.RED_SANDSTONE, null));
        mColors.add(new ImmutablePair(Material.QUARTZ_BLOCK, null));
        mColors.add(new ImmutablePair(Material.SPONGE, null));
        mColors.add(new ImmutablePair(Material.MELON_BLOCK, null));
        mColors.add(new ImmutablePair(Material.LAPIS_BLOCK, null));
        mColors.add(new ImmutablePair(Material.COAL_BLOCK, null));
    }

    public Pair<Material, DyeColor> getColor(int index) {
        try {
            return mColors.get(index);
        } catch (IndexOutOfBoundsException e) {
            // return black
            logger.log(Level.WARNING, "bad palette index "+index);
            return mColors.get(mColors.size() - 1);
        }
    }
}
