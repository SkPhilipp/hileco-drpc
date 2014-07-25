package com.hileco.drpc.http;

import com.hileco.drpc.core.spec.IncomingMessageConsumer;
import com.hileco.drpc.core.spec.Metadata;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Allows a {@link com.hileco.drpc.core.spec.IncomingMessageConsumer} to be accessed as an {@link HttpServlet}.
 *
 * @author Philipp Gayret
 */
public class MessageConsumerServletAdapter extends HttpServlet {

    private HeaderUtils headerUtils;
    private IncomingMessageConsumer incomingMessageConsumer;

    public MessageConsumerServletAdapter(IncomingMessageConsumer incomingMessageConsumer) {
        this.incomingMessageConsumer = incomingMessageConsumer;
        this.headerUtils = new HeaderUtils();
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Metadata metadata = this.headerUtils.fromHeaders(req::getHeader);
        this.incomingMessageConsumer.accept(metadata, req.getInputStream());
        resp.setStatus(HttpServletResponse.SC_ACCEPTED);
    }

}
