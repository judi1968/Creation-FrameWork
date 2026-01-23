package jframework.session;

public interface UserAuthSession{
    String[] getRoles();
    void setRole(String role);
    boolean isAuthentified();
    boolean isAuthentifiedRole(String role);
}