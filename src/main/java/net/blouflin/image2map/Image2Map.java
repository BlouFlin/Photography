package net.blouflin.image2map;

//https://github.com/TheEssem/Image2Map

public class Image2Map {

    public enum DitherMode {
        NONE,
        FLOYD;

        public static DitherMode fromString(String string) {
            if (string.equalsIgnoreCase("NONE"))
                return DitherMode.NONE;
            else if (string.equalsIgnoreCase("DITHER") || string.equalsIgnoreCase("FLOYD"))
                return DitherMode.FLOYD;
            throw new IllegalArgumentException("invalid dither mode");
        }
    }




}
