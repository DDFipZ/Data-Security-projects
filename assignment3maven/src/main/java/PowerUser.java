public class PowerUser extends PrinterManager{
    public PowerUser(String name, String password, String usertype) {
        super(name, password, usertype);
        this.usertype = "power_user";
    }
}
