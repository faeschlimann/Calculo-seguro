package inacap.cl.appinacap1;

        import java.io.BufferedInputStream;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.URL;
//Es una clase genérica para all
/**
 * Created by fganga on 29-09-17.
 */

class HTTPDataHandler {
    static String stream = null;

    public HTTPDataHandler(){
    }

    public String GetHTTPData(String urlString){
        try{
            URL url = new URL(urlString); //Creamos el objeto de la clase url
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            // Chequeamos el estado de la conexión
            if(urlConnection.getResponseCode() == 200)
            {
                // Si el código de respuesta es 200
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                // Leemos el  BufferedInputStream
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = r.readLine()) != null) {
                    sb.append(line);
                }
                stream = sb.toString();
                // Fin de la lectura

                // Desconexión de HttpURLConnection
                urlConnection.disconnect();
            }
            else
            {
                // Efectuar alguna otra operación
            }
        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch(IOException e){
            e.printStackTrace();
        }finally {

        }
        // Retorna los datos desde la URL especificada
        return stream;
    }
}