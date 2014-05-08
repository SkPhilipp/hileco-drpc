package machine.backbone.services;

import com.google.common.io.ByteStreams;
import machine.management.api.entities.Command;
import machine.management.api.exceptions.CommandExecutionException;
import machine.management.api.services.RemoteCommandService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link RemoteCommandService}.
 */
public class RemoteCommandServiceImpl implements RemoteCommandService {

    @Override
    public String command(Command command) throws CommandExecutionException {
        try {
            List<String> cmdList = new ArrayList<>();
            cmdList.add(command.getName());
            cmdList.addAll(command.getArgs());
            Process process = Runtime.getRuntime().exec((String[])cmdList.toArray());
            process.waitFor();
            InputStream prcIn = process.getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ByteStreams.copy(prcIn, out);
            return out.toString();
        } catch (IOException e) {
            throw new CommandExecutionException("Erred while executing the given command.", e);
        } catch (InterruptedException e) {
            throw new CommandExecutionException("Executing the given command timed out.", e);
        }
    }

}
