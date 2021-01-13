package unl.core;

public class MainClass {
    public static void main(String[] args) {
        UnlCore unlCoreInstance = UnlCore.getInstance();

        String encodeResult = unlCoreInstance.encode(
                52.205, 0.119, 7, new Elevation(2, "floor")
        );



        System.out.println("Encode result: " + encodeResult);
    }
}
