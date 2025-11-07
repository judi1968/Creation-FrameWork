package jframework.slistener;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import jframework.annotation.Controller;
import jframework.annotation.Url;
import jframework.qutils.Rooter;
import jframework.servlet.RooterServlet;

@WebListener
public class JFrameworkStartupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println(">>>>>>> Application servlet demarree sous JFramework <<<<<");

        ServletContext servletContext = sce.getServletContext();
        String appPath = servletContext.getRealPath("/WEB-INF/classes");


        try {
            List<Class<?>> controllers = findClassesWithAnnotation(new File(appPath), "", Controller.class);
            Map<String, Rooter> rootersMap = getAllRootes(controllers);
            // RooterServlet.rooters = rootersMap;
            servletContext.setAttribute("rooters", rootersMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        System.out.println(">>>>> Application du JFramework arretee  <<<<<<");
    }

    private List<Class<?>> findClassesWithAnnotation(File folder, String packageName, Class<?> annotationClass)
            throws Exception {
        List<Class<?>> result = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null) return result;

        for (File file : files) {
            if (file.isDirectory()) {
                String subPackage = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                result.addAll(findClassesWithAnnotation(file, subPackage, annotationClass));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> cls = Class.forName(className);
                    if (cls.isAnnotationPresent(annotationClass.asSubclass(java.lang.annotation.Annotation.class))) {
                        result.add(cls);
                    }
                } catch (Throwable t) {
                }
            }
        }
        return result;
    }
    
    private Map<String, Rooter> getAllRootes(List<Class<?>> controllers) {
    Map<String, Rooter> routes = new HashMap<>();

    for (Class<?> controllerClass : controllers) {
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(Url.class)) {
                Url urlAnnotation = method.getAnnotation(Url.class);
                String urlValue = urlAnnotation.value();
                Rooter info = new Rooter();
                info.classe = controllerClass.getName();
                info.method = method.getName();

                routes.put(urlValue, info);
            }
        }
    }

    return routes;
}

}
