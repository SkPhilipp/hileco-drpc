package bot.demo.messages.user;

import java.io.Serializable;
import java.util.List;

public class InventorizeAction implements Serializable {

    private List<String> tradeables;

    public List<String> getTradeables() {
        return tradeables;
    }

    public void setTradeables(List<String> tradeables) {
        this.tradeables = tradeables;
    }

}
