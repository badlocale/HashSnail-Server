import org.hashsnail.server.model.mods.AttackByMask;
import org.hashsnail.server.model.mods.AttackMode;
import org.hashsnail.server.model.range.PasswordRange;
import org.hashsnail.server.net.PocketWriter;
import org.hashsnail.server.net.ServerSession;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Enumeration;

public class RRR {
    public static void main(String[] args) throws IOException {
//        int n = 8;
//        float temp = 0;
//        float delta = 1F / (float) n;
//        PasswordRange passwordRange = new PasswordRange("%A%A%A".toCharArray());
//        while (temp <= 1) {
//            System.out.print("\n");
//            System.out.print(passwordRange.subdivide(temp));
//            temp += delta;
//            System.out.print(" ");
//            System.out.print(passwordRange.subdivide(temp));
//        }


        PasswordRange passwordRange = new PasswordRange("%L%L%L%L%L%L".toCharArray());
        AttackByMask attackByMask = new AttackByMask("%L%L%L%L%L%L");
        System.out.println(attackByMask.nextPasswordRange(0.5f));
        System.out.println(attackByMask.nextPasswordRange(0.5f));
    }
}
