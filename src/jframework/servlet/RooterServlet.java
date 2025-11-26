package jframework.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jframework.annotation.RequestParam;
import jframework.hutils.TypeCaster;
import jframework.qutils.ModelView;
import jframework.qutils.Rooter;

public class RooterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RequestDispatcher dispatcher;

    // public static Map<String, Rooter> rooters;
    @Override
    public void init() throws ServletException {
        dispatcher = getServletContext().getNamedDispatcher("default");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {

            processRequest(request, response, "get");
        } catch (Exception e) {
            e.printStackTrace();
            new ServletException(e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response, "post");
        } catch (Exception e) {
            e.printStackTrace();
            new ServletException(e.getMessage());
        }
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response, String typeMethod)
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
                // String url = request.getRequestURL().toString();
                Map<String, Rooter> rooters = null;
                if (typeMethod.compareToIgnoreCase("get") == 0) {
                    rooters = (Map<String, Rooter>) context.getAttribute("rootersGet");
                } else if (typeMethod.compareToIgnoreCase("post") == 0) {
                    rooters = (Map<String, Rooter>) context.getAttribute("rootersPost");
                }
                Rooter rooter = rooters.get(relativePath);

                

                if (rooter == null){
                    boolean isMatch = false;
                    for (Entry<String, Rooter> root : rooters.entrySet()) {
                        String regex = root.getKey().replaceAll("\\{[^/]+\\}", "([^/]+)");
                        regex = "^"+regex+"$";
                        if (relativePath.matches(regex)) {
                            isMatch = true;
                            String pathParameter = (String) root.getKey();
                            Rooter rooterParameter = (Rooter) rooters.get(pathParameter);
                            execRoote(request, response, rooterParameter , out, relativePath, root.getKey());
                        }else{
                            String[] urlInterogation = relativePath.split("\\?");
                            if (urlInterogation.length>1) {
                                if (urlInterogation[0].compareToIgnoreCase(root.getKey()) == 0) {
                                    rooter = rooters.get(urlInterogation[0]);
                                    if (rooter != null) {                                        
                                        execRoote(request, response, rooter , out, relativePath, root.getKey());
                                    }
                                }
                            }
                        }
                    }
                    if (isMatch) return;
                    if (!isMatch) out.println("<h1> 404 Not Found</h1>");
                } else {
                    execRoote(request, response, rooter, out, relativePath, relativePath);
                }
            }
        }
    }

    private boolean fileExists(ServletContext context, String relativePath) {
        if (relativePath.compareToIgnoreCase("/") == 0)
            return false;

        try {
            URL resource = context.getResource(relativePath);
            return (resource != null);
        } catch (Exception e) {
            return false;
        }

    }

    private void execRoote(HttpServletRequest request, HttpServletResponse response, Rooter rooter, PrintWriter out, String pathClient, String pathController)
            throws Exception {
        HttpServletRequest req = (HttpServletRequest) request;
        String className = rooter.classe;
        String methodName = rooter.method;
        // Charger la classe dynamiquement
        Class<?> clazz = Class.forName(className);

        for (Method m : clazz.getDeclaredMethods()) {
            if (m.getName().equals(methodName)) {

                // mi executer methode
                Object instance = clazz.getDeclaredConstructor().newInstance();

                Parameter[] parameters = m.getParameters();
                Object result;

                if (parameters.length == 0) {
                    // méthode sans paramètre
                    result = m.invoke(instance);
                } else {
                    String[] keyParam = pathController.split("/");
                    String[] valueParam = pathClient.split("/");
                    HashMap<String, String> paramUrl = new HashMap<>();
                    int j=0;
                    for (String parameter : keyParam) {
                        paramUrl.put(keyParam[j],valueParam[j]);
                        j++;
                    }
                    Object[] values = new Object[parameters.length];

                    for (int i = 0; i < parameters.length; i++) {
                        String paramName = parameters[i].getName();
                        String rawValue = request.getParameter(paramName);
                        if (rawValue == null) {
                            rawValue = paramUrl.get("{"+paramName+"}");
                        }
                        if (rawValue == null) {
                            if (parameters[i].isAnnotationPresent(RequestParam.class)) {
                                RequestParam requestParam = parameters[i].getAnnotation(RequestParam.class);
                                 rawValue = request.getParameter(requestParam.value());
                                 System.out.println(rawValue+" mety eeeeeh ");
                            }
                        }


                        Class<?> type = parameters[i].getType();

                        if (rawValue == null && type.isPrimitive()) {
                            throw new Exception("Le paramètre '" + paramName +
                                "' est manquant alors que la méthode attend un type primitif : " + type.getName());
                        }

                        values[i] = TypeCaster.cast(rawValue, type);
                    }

                    result = m.invoke(instance, values);
                }
                if (result.getClass().getName().compareToIgnoreCase("java.lang.String") == 0) {
                    out.println(result);
                } else if (result.getClass().getName().compareToIgnoreCase("jframework.qutils.ModelView") == 0) {
                    ModelView modelView = (ModelView) result;

                    for (Map.Entry<String, Object> data : modelView.getData().entrySet()) {
                        request.setAttribute(data.getKey(), data.getValue());
                    }
                    String pathDispatch = modelView.getView();
                    if (!pathDispatch.startsWith("/")) {
                        pathDispatch = "/" + pathDispatch;
                    }
                    RequestDispatcher dispat = req.getRequestDispatcher(pathDispatch);
                    dispat.forward(request, response);
                } else {
                    out.println("<h1> Erreur 500 </h1>");
                    out.println("<p> Type de retour de " + rooter.classe + " : " + rooter.method + " est invalide");
                }
            }
        }
    }

}
// efa vita 6 bis
// efa vita 6 ter