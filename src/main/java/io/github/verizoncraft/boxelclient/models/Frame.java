package io.github.verizoncraft.boxelclient.models;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.verizoncraft.boxelclient.palettes.DefaultPalette;
import io.github.verizoncraft.boxelclient.palettes.Palette;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class Frame {
    private static transient GsonBuilder builder = new GsonBuilder();
    public static transient Logger LOG = Bukkit.getLogger();

    public int rows;
    public int columns;
    public List<Integer> colors;

    private transient Palette palette;

    public Frame() {
        setPalette(new DefaultPalette());
    }

    public Frame(int columns, int rows, List<Integer> colors) {
        setPalette(new DefaultPalette());
        this.rows = rows;
        this.columns = columns;
        this.colors = colors;
    }

    public static Frame fromJSON(String json) {
        Gson gson = builder.create();
        return gson.fromJson(json, Frame.class);
    }

    public void drawAtLocation(Location location) {
        drawAtLocation(location, rows, columns);
    }

    public void drawAtLocation(Location location, int maxWidth, int maxHeight) {
        Iterator<Integer> pixel_iter = colors.iterator();

        // 30 cols x 40 rows
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                Location newLocation = this.nextLocation(location, i, j);
                if (i <= maxWidth && j <= maxHeight) {
                    drawBoxel(newLocation, pixel_iter.next());
                }
            }
        }
    }

    public void drawAtLocation(Location location, Integer yOffset, int maxWidth, int maxHeight) {
        int xCounter = 0;
        int yCounter = 0;

        if (yOffset < rows) {
            for (int i = yOffset; i < rows; i++) {
                for (int j = 0; j < columns; j++) {
                    if (yCounter <= maxWidth) {
                        Location newLocation = this.nextLocation(location, yCounter, xCounter);
                        int idx = (i * columns) + j;
                        Integer color = colors.get(idx);
                        drawBoxel(newLocation, color);
                        if (xCounter == maxHeight - 1) {
                            xCounter = 0;
                            ++yCounter;
                        } else {
                            ++xCounter;
                        }
                    }
                }
            }
        }
    }

    public void drawAtLocation(Location location, Integer yOffset) {
        drawAtLocation(location, yOffset, rows, columns);
    }

    public Location nextLocation(Location location, int row, int column) {
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        return new Location(Bukkit.getWorld("world"), (x + column), (y - row), z);
    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    @SuppressWarnings("deprecation")
    private void drawBoxel(Location location, int pixel) {
        if (pixel >= 0) {
            Block block = location.getBlock();

            Pair<Material, DyeColor> data = palette.getColor(pixel);

            Material material = data.getLeft();
            DyeColor dye = data.getRight();
            block.setType(material);
            if (dye != null) {
                block.setData(dye.getData());
            }

        }
    }
}
