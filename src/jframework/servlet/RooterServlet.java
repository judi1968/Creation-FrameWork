package jframework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jframework.qutils.ModelView;
import jframework.qutils.Rooter;

public class RooterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RequestDispatcher dispatcher;

    // public static Map<String, Rooter> rooters;
    @Override
    public void init() throws ServletException{
        dispatcher = getServletContext().getNamedDispatcher("default");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
    
            processRequest(request, response);
        } catch (Exception e) {
            new ServletException(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception e) {
            new ServletException(e.getMessage());
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        ServletContext context = request.getServletContext();
        HttpServletRequest req = (HttpServletRequest) request;
        String path = req.getRequestURI();

        String relativePath = path.substring(req.getContextPath().length());

        if (fileExists(context, relativePath)) {
            dispatcher.forward(request, response);
        } else {
            response.setContentType("text/html;charset=UTF-8");
            try (PrintWriter out = response.getWriter()) {
                String url = request.getRequestURL().toString();
                
                Map<String, Rooter> rooters = (Map<String, Rooter>) context.getAttribute("rooters");
                Rooter rooter = rooters.get(relativePath);

               

                if (rooter == null) 
                    out.println("<h1> 404 Not Found</h1>");
                else{
                    String className = rooter.classe;
                    String methodName = rooter.method;

                    // Charger la classe dynamiquement
                    Class<?> clazz = Class.forName(className);

                    for (Method m : clazz.getDeclaredMethods()) {
                        if (m.getName().equals(methodName)) {

                            // maka type de retour
                            String returnType = m.getReturnType().getSimpleName();
                            System.out.println("Type de retour : " + returnType);

                            // mi executer methode
                            Object instance = clazz.getDeclaredConstructor().newInstance();
                            Object result = m.invoke(instance);
                            if (result.getClass().getName().compareToIgnoreCase("java.lang.String") == 0) {
                                out.println(result);
                            } else if (result.getClass().getName().compareToIgnoreCase("jframework.qutils.ModelView") == 0) {
                                ModelView modelView = (ModelView) result;
                                RequestDispatcher dispat =
                                req.getRequestDispatcher(modelView.getView());
                                dispat.forward(request,response);
                            } else {
                                out.println("<h1> Erreur 500 </h1>");
                                out.println("<p> Type de retour de " + rooter.classe+ " : "+rooter.method+" est invalide");
                            }
                        }
                    }
                }
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

