## Supported Annotations

- @Async
- @AutoWire
- @EventListner
- @Profile
- @Qualifier
- @Scheduled
- @Service
- @Value

## Setup Application

```java

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

```