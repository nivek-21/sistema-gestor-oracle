package Helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Clase encargada de leer un archivo y retornar su contenido en forma de un
 * String.
 */
public class Reader {

    /**
     * Método que recibe un String con el path del sistema donde se encuentra el
     * archivo que queremos leer; lo lee y lo retorna en forma de un String.
     *
     * @param path String
     * @return String
     */
    public static String readLog(String path) {

        try {
            FileInputStream fstream = new FileInputStream(path);
            BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

            String strLine;
            String result = "";

            while ((strLine = br.readLine()) != null) {
                result += strLine + "\n";
            }

            fstream.close();
            return result;

        } catch (FileNotFoundException ex) {
            Emergent.showConsole("Error - RD: " + ex.getMessage());
        } catch (IOException ex) {
            Emergent.showConsole("Error - RD: " + ex.getMessage());
        }

        return null;
    }

    /**
     * Método que divide un path en dos partes, la carpeta donde está localizado
     * y el nombre del archivo. El mismo retorna un HashMap con ambas partes.
     *
     * @param path String
     * @return HashMap<String, String>
     */
    public static HashMap<String, String> divider(String path) {

        String[] values = path.split(Pattern.quote(File.separator));
        int last = values.length - 1;

        String realPath = "";
        for (int i = 0; i < last; i++) {
            realPath += values[i];
            if (i != last - 1) {
                realPath += "\\";
            }
        }

        HashMap<String, String> response = new HashMap<>();
        response.put("path", realPath);
        response.put("name", values[last]);

        return response;
    }

}
