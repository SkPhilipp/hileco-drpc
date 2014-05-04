package machine.management.services.utils;

import java.util.Random;
import java.util.UUID;

/**
 * Utility class for creating random content.
 */
public class Randoms {

    public static final int DEFAULT_RANDOM_BYTES_LENGTH = 100;
    private static final Random RANDOM = new Random();

    public static byte[] randomBytes(int length){
        byte[] bytes = new byte[length];
        Random random = new Random();
        random.nextBytes(bytes);
        return bytes;
    }

    public static byte[] randomBytes(){
        return Randoms.randomBytes(DEFAULT_RANDOM_BYTES_LENGTH);
    }

    public static UUID randomUUID(){
        return UUID.randomUUID();
    }

    public static String randomString(){
        return UUID.randomUUID().toString();
    }

    public static <T extends Enum<T>> T randomEnum(Class<T> type){
        T[] enumConstants = type.getEnumConstants();
        int index = RANDOM.nextInt(enumConstants.length);
        return enumConstants[index];
    }

    public static Random getRandom() {
        return RANDOM;
    }


}
