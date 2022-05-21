import org.hashsnail.server.model.range.PasswordRange;

public class RRR {
    public static void main(String[] args) {
        int n = 8;
        float temp = 0;
        float delta = 1F / (float) n;
        PasswordRange passwordRange = new PasswordRange("%A%A%A".toCharArray());
        while (temp <= 1) {
            System.out.print("\n");
            System.out.print(passwordRange.subdivide(temp));
            temp += delta;
            System.out.print(" ");
            System.out.print(passwordRange.subdivide(temp));
        }
    }
}
