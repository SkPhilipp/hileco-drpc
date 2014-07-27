package com.hileco.drpc.http.core;

import com.hileco.drpc.core.spec.MessageReceiver;
import com.hileco.drpc.core.spec.Metadata;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows a {@link MessageReceiver} to be accessed through an {@link HttpServlet}.
 *
 * @author Philipp Gayret
 */
public class MessageReceiverServletAdapter extends HttpServlet {

    private MessageReceiver messageReceiver;

    public MessageReceiverServletAdapter(MessageReceiver messageReceiver) {
        this.messageReceiver = messageReceiver;
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Metadata metadata = HttpHeaderUtils.fromHeaders(req::getHeader);
        this.messageReceiver.accept(metadata, req.getInputStream());
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

}
