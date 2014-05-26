package bot.demo.consumer.handlers;

import bot.demo.messages.user.*;

public interface UserActionHandler {

    public void chat(ChatAction chatAction);

    public void farm(FarmAction farmAction);

    public void inventorize(InventorizeAction inventorizeAction);

    public void locate(LocateAction locateAction);

    public void trade(TradeAction tradeAction);

}
