package bot.demo.messages.user;

import java.io.Serializable;

public class FarmAction implements Serializable {

    private String what;

    public String getWhat() {
        return what;
    }

    public void setWhat(String what) {
        this.what = what;
    }

}
