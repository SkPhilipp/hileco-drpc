package bot.demo.messages.user;

import java.io.Serializable;
import java.util.List;

public class TradeAction implements Serializable {

    private String from;
    private String to;
    private List<String> tradeables;

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<String> getTradeables() {
        return tradeables;
    }

    public void setTradeables(List<String> tradeables) {
        this.tradeables = tradeables;
    }

}
