/**
Copyright 2015 Fabian Bock, Fabian Bruckner, Christine Dahn, Amin Nirazi, Matthäus Poloczek, Kai Sauerwald, Michael Schultz, Shabnam Tabatabaian, Tim Tegeler und Marvin Wepner

This file is part of pg-infoscreen.

pg-infoscreen is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

pg-infoscreen is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with pg-infoscreen.  If not, see <http://www.gnu.org/licenses/>.
*/
package sample.hello;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.types.Binary;

public class ImgConverter {
	
	/**
	 * Holt Bild unter der URL 'imgURL' und gibt das Bild im base64 Format zurück.
	 * 
	 * @param imgURL		URL des zu holenden Bildes
	 * @return				Bild im base64 Format
	 * @throws IOException
	 */
	public static Binary storeImage(String imgURL) throws IOException {
		Binary binary = null;
		try {
			URL url = new URL(imgURL);
	        URLConnection connection = url.openConnection();
	        InputStream is = new BufferedInputStream(connection.getInputStream());
	        binary = new Binary(readInputStream(is));
		} catch(FileNotFoundException e){
			return null;
		}
        return binary;
    }
	
	/**
	 * Liest InputStream 'is' ein und gibt den gelesenen Inhalt als byte array zurück.
	 * 
	 * @param is			InputStream
	 * @return				gelesener Inhalt als byte array
	 * @throws IOException
	 */
	public static byte[] readInputStream(InputStream is) throws IOException{
		byte[] buffer = new byte[1024];
        List<byte[]> bytes = new ArrayList<>();
        int size = 0;
        int read;
        while ((read = is.read(buffer, 0, buffer.length)) != -1) {
            byte[] chunk = Arrays.copyOf(buffer, read);
            bytes.add(chunk);
            size += chunk.length;
        }
        byte[] file = new byte[size];
        int offset = 0;
        for (int i = 0; i < bytes.size(); ++i) {
            byte[] bs = bytes.get(i);
            System.arraycopy(bs, 0, file, offset, bs.length);
            offset += bs.length;
        }
        return file;
	}
}
