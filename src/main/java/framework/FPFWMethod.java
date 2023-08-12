package framework;

import framework.annotations.Scheduled;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

class FPFWMethod {
    private List<Object> serviceObjectList;

    FPFWMethod(List<Object> serviceObjectList) {
        this.serviceObjectList = serviceObjectList;
    }

    void scanSchedule() {
        for (Object serviceObject : serviceObjectList) {
            for (Method method : serviceObject.getClass().getDeclaredMethods()) {
                try {
                    if (method.isAnnotationPresent(Scheduled.class)) {

                        FPFWAnnotationScanner scanner = new FPFWAnnotationScanner();
                        Annotation annotation = scanner.findAnnotation(method,Scheduled.class);
                        if(annotation != null) {
                            Scheduled scheduled = (Scheduled)annotation;

                            if(!scheduled.cron().isEmpty()) {
                                String[] timeData = scheduled.cron().split(" ");
                                if(timeData.length == 2) {
                                    try {
                                        int second = Integer.parseInt(timeData[0]);
                                        int minute = Integer.parseInt(timeData[1]);

                                        if(second < 60 && minute < 60) {
                                            int totalSecond = second + (minute * 60);
                                            Timer timer = new Timer();
                                            TimerTask task = getTimerTask(serviceObject, method);
                                            timer.schedule(task,totalSecond * 1000);
                                        }
                                    }
                                    catch (Exception e) {
                                        throw e;
                                    }
                                }
                            }
                            else {
                                workingAsTimer(serviceObject, method, scheduled);
                            }

                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void workingAsTimer(Object serviceObject, Method method, Scheduled scheduled) {
        Timer timer = new Timer();
        TimerTask task = getTimerTask(serviceObject, method);
        timer.scheduleAtFixedRate(task, scheduled.delay(), scheduled.fixedRate());
    }

    private static TimerTask getTimerTask(Object serviceObject, Method method) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    method.invoke(serviceObject);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        return task;
    }

}
