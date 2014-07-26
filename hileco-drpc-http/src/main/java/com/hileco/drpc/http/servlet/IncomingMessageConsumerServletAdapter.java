package com.hileco.drpc.http.servlet;

import com.hileco.drpc.core.spec.MessageReceiver;
import com.hileco.drpc.core.spec.Metadata;
import com.hileco.drpc.http.HeaderUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows a {@link com.hileco.drpc.core.spec.MessageReceiver} to be accessed as an {@link HttpServlet}.
 *
 * @author Philipp Gayret
 */
public class IncomingMessageConsumerServletAdapter extends HttpServlet {

    private MessageReceiver messageReceiver;

    public IncomingMessageConsumerServletAdapter(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Metadata metadata = HeaderUtils.fromHeaders(req::getHeader);
        this.messageReceiver.accept(metadata, req.getInputStream());
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

}
