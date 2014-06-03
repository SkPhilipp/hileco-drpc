package bot.demo.consumer.api;

import java.util.List;

public interface LiveUser {

    public void chat(String receiver, String message);

    public void farm(String what);

    public void inventorize(List<String> tradeables);

    public void locate();

    public void trade(String to, List<String> tradeables);

}
