public class PrinterManager extends User {
    public PrinterManager(String name, String password, String usertype){
        super(name, password, usertype);
        PrinterManager.super.usertype = "printer_manager";


    }
}
