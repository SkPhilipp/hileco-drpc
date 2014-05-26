package bot.demo.consumer.handlers;

import bot.demo.messages.process.LoginAction;
import bot.demo.messages.process.LogoutAction;
import bot.demo.messages.process.RegisterAction;

public interface ProcessActionHandler {

    public void login(LoginAction loginAction);

    public void logout(LogoutAction logoutAction);

    public void register(RegisterAction registerAction);

    public void scan();

}
