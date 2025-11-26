package coscostan.model;

public class Admin {
    private int idAdmin;
    private String adminName;
    private String password;
    private String noTelepon;
    private String email;
    
    // Constructors
    public Admin() {}
    
    public Admin(String adminName, String password, String noTelepon, String email) {
        this.adminName = adminName;
        this.password = password;
        this.noTelepon = noTelepon;
        this.email = email;
    }
    
    // Getters and Setters
    public int getIdAdmin() { return idAdmin; }
    public void setIdAdmin(int idAdmin) { this.idAdmin = idAdmin; }
    
    public String getAdminName() { return adminName; }
    public void setAdminName(String adminName) { this.adminName = adminName; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNoTelepon() { return noTelepon; }
    public void setNoTelepon(String noTelepon) { this.noTelepon = noTelepon; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    @Override
    public String toString() {
        return "Admin{" + "idAdmin=" + idAdmin + ", adminName=" + adminName + 
               ", noTelepon=" + noTelepon + ", email=" + email + '}';
    }
}