package overwatch.getPlugin.utils;

import org.bukkit.World;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import java.util.Iterator;
import java.util.List;
import java.text.DecimalFormat;
import java.math.RoundingMode;
import java.math.BigDecimal;
import java.util.Random;

public class UtilMath
{
    public static Random random;

    static {
        UtilMath.random = new Random();
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static double trim(final int degree, final double d) {
        String format = "#.#";
        for (int i = 1; i < degree; ++i) {
            format = String.valueOf(format) + "#";
        }
        final DecimalFormat twoDForm = new DecimalFormat(format);
        return Double.valueOf(twoDForm.format(d));
    }

    public static int r(final int i) {
        return UtilMath.random.nextInt(i);
    }

    public static double abs(final double a) {
        return (a <= 0.0) ? (0.0 - a) : a;
    }

    public static String ArrayToString(final String[] list) {
        String string = "";
        for (final String key : list) {
            string = String.valueOf(string) + key + ",";
        }
        if (string.length() != 0) {
            return string.substring(0, string.length() - 1);
        }
        return null;
    }

    public static String ArrayToString(final List<String> list) {
        String string = "";
        for (final String key : list) {
            string = String.valueOf(string) + key + ",";
        }
        if (string.length() != 0) {
            return string.substring(0, string.length() - 1);
        }
        return null;
    }

    public static String[] StringToArray(final String string, final String split) {
        return string.split(split);
    }

    public static double offset2d(final Entity a, final Entity b) {
        return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset2d(final Location a, final Location b) {
        return offset2d(a.toVector(), b.toVector());
    }

    public static double offset2d(final Vector a, final Vector b) {
        a.setY(0);
        b.setY(0);
        return a.subtract(b).length();
    }

    public static double offset(final Entity a, final Entity b) {
        return offset(a.getLocation().toVector(), b.getLocation().toVector());
    }

    public static double offset(final Location a, final Location b) {
        return offset(a.toVector(), b.toVector());
    }

    public static double offset(final Vector a, final Vector b) {
        return a.subtract(b).length();
    }

    public static Vector getHorizontalVector(final Vector v) {
        v.setY(0);
        return v;
    }

    public static Vector getVerticalVector(final Vector v) {
        v.setX(0);
        v.setZ(0);
        return v;
    }

    public static String serializeLocation(final Location location) {
        final int X = (int)location.getX();
        final int Y = (int)location.getY();
        final int Z = (int)location.getZ();
        final int P = (int)location.getPitch();
        final int Yaw = (int)location.getYaw();
        return new String(String.valueOf(location.getWorld().getName()) + "," + X + "," + Y + "," + Z + "," + P + "," + Yaw);
    }

    public static Location deserializeLocation(final String string) {
        final String[] parts = string.split(",");
        final World world = Bukkit.getServer().getWorld(parts[0]);
        final Double LX = Double.parseDouble(parts[1]);
        final Double LY = Double.parseDouble(parts[2]);
        final Double LZ = Double.parseDouble(parts[3]);
        final Float P = Float.parseFloat(parts[4]);
        final Float Y = Float.parseFloat(parts[5]);
        final Location result = new Location(world, (double)LX, (double)LY, (double)LZ);
        result.setPitch((float)P);
        result.setYaw((float)Y);
        return result;
    }

    public static long averageLong(final List<Long> list) {
        long add = 0L;
        for (final Long listlist : list) {
            add += listlist;
        }
        return add / list.size();
    }

    public static double averageDouble(final List<Double> list) {
        Double add = 0.0;
        for (final Double listlist : list) {
            add += listlist;
        }
        return add / list.size();
    }
}