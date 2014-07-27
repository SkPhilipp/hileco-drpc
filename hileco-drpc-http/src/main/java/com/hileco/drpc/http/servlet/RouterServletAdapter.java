package com.hileco.drpc.http.servlet;

import com.hileco.drpc.http.routing.HttpRouter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

// TODO: implement as HttpServlet delegating to router
public class RouterServletAdapter extends HttpServlet {

    private HttpRouter httpRouter;

    // TODO: accept+save, (extend?)

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPost(req, resp);
    }

}
