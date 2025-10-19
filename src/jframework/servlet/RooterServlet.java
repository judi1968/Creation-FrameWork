package jframework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RooterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RequestDispatcher dispatcher;

    @Override
    public void init() throws ServletException{
        dispatcher = getServletContext().getNamedDispatcher("default");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        ServletContext context = request.getServletContext();
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        String relativePath = path.substring(req.getContextPath().length());

        if (fileExists(context, relativePath)) {
            System.out.println("yesssssss ********** n " + relativePath); 
            dispatcher.forward(request, response);
        } else {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                String url = request.getRequestURL().toString();
                out.print("<h1>RooterServlet</h1>");
                out.print("<p>URL appelée : " + url + "</p>");
            }
        }
    }

     private boolean fileExists(ServletContext context, String relativePath) {
        try {
            URL resource = context.getResource(relativePath);
            return (resource != null);
        } catch (Exception e) {
            return false;
        }
    
    }
}

