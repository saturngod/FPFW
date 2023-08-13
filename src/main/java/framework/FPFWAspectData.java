package framework;

enum FPFWAspectDataType {
    AFTER,BEFORE,AROUND
}
class FPFWAspectData {
    String className;
    String functionName;
    FPFWAspectDataType type;

    public FPFWAspectData(String className, String functionName, FPFWAspectDataType type) {
        this.className = className;
        this.functionName = functionName;
        this.type = type;
    }

}
