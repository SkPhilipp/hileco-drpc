package machine.management.api.entities;

import com.google.common.collect.Lists;

import java.util.List;

public class Command {

    String name;
    List<String> args;

    public Command(String name, String... args) {
        this.name = name;
        this.args = Lists.newArrayList(args);
    }

    public Command() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }
}
