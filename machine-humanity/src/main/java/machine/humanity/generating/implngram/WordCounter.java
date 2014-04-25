package machine.humanity.generating.implngram;

import java.util.HashMap;
import java.util.Map;

/**
 * A map of strings to integers, use {@link #count(String)} to increment the value for a given key.
 *
 * Keeps track of the total counted.
 */
public class WordCounter {

    private Map<String, Integer> map;
    private Integer total;

    public WordCounter(Map<String, Integer> map, Integer total) {
        this.map = map;
        this.total = total;
    }

    public WordCounter() {
        this.map = new HashMap<String, Integer>();
        this.total = 0;
    }

    public void count(String key){
        if(!this.map.containsKey(key)){
            this.map.put(key, 0);
        }
        Integer current = this.map.get(key);
        this.map.put(key, current + 1);
    }

    public Integer get(String key) {
        return map.containsKey(key) ? map.get(key) : 0;
    }

    public Integer getTotal() {
        return total;
    }

    public String getAt(Integer offset){
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            offset -= entry.getValue();
            if(offset < 0){
                return entry.getKey();
            }
        }
        return null;
    }

}
