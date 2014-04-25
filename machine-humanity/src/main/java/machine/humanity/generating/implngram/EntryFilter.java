package machine.humanity.generating.implngram;

public interface EntryFilter {

    /**
     * Decides whether a key value pair is allowed.
     *
     * @param key key
     * @param value value
     * @return true to allow
     */
    public boolean allow(String key, String value);

    /**
     * A filter which always returns true.
     */
    public static final EntryFilter ALLOW_ALL = new EntryFilter() {

        @Override
        public boolean allow(String key, String value) {
            return true;
        }

    };

}
