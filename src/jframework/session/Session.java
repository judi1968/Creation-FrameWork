package jframework.session;

import jakarta.servlet.http.HttpSession;

public class Session {

    private HttpSession session;

    
    public  void initializer(HttpSession httpSession) {
        session = httpSession;
    }

    
    public void add(String key, Object value) {
        if (session != null) {
            session.setAttribute(key, value);
        }
    }

    
    public void update(String key, Object value) {
        if (session != null && session.getAttribute(key) != null) {
            session.setAttribute(key, value);
        }
    }

    
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        if (session != null) {
            return (T) session.getAttribute(key);
        }
        return null;
    }

    
    public void remove(String key) {
        if (session != null) {
            session.removeAttribute(key);
        }
    }

    
    public boolean exists(String key) {
        return session != null && session.getAttribute(key) != null;
    }

    
    public void invalidate() {
        if (session != null) {
            session.invalidate();
            session = null;
        }
    }

    
    public HttpSession getHttpSession() {
        return session;
    }

    
}
