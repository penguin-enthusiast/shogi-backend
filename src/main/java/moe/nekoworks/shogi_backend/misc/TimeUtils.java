package moe.nekoworks.shogi_backend.misc;

public class TimeUtils {

    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }

    public static String convertTimeHumanReadable(long t1, long t2) {
        if (t1 > t2) {
            throw new IllegalArgumentException();
        }
        long diff = (t2 - t1);
        int milis = Math.toIntExact(diff % 1000) / 10;
        diff /= 1000;
        int seconds = Math.toIntExact(diff % 60);
        diff /= 60;
        int minutes = (int) diff;
        String time = "";
        time += minutes < 10 ? '0' : "";
        time += minutes;
        time += ':';
        time += seconds < 10 ? '0' : "";
        time += seconds;
        time += '.';
        time += milis < 10 ? '0' : "";
        time += milis;
        return time;
    }
}
