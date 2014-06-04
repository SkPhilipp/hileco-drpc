package bot.demo.consumer.live.descriptors;

import java.util.Collection;
import java.util.UUID;

public class ProcessDescriptor {

    private UUID processId;
    private Integer slots;
    private Collection<String> usernames;

    public UUID getProcessId() {
        return processId;
    }

    public void setProcessId(UUID processId) {
        this.processId = processId;
    }

    public Integer getSlots() {
        return slots;
    }

    public void setSlots(Integer slots) {
        this.slots = slots;
    }

    public Collection<String> getUsernames() {
        return usernames;
    }

    public void setUsernames(Collection<String> usernames) {
        this.usernames = usernames;
    }

}
