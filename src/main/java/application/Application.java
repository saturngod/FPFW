package application;

import framework.annotations.AutoWire;
import framework.annotations.FPFWApplication;
import framework.FPFWContext;
import framework.annotations.Runnable;

@FPFWApplication
public class Application implements Runnable {

    @AutoWire
    ProductService productService;

    public static void main(String[] args) {
        FPFWContext.run(Application.class);
    }

    @Override
    public void run() {
        System.out.println("HELLO WORKING FINE");
        productService.show();
    }
}
