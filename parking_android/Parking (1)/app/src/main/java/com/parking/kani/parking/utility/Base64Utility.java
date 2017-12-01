package com.parking.kani.parking.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by Kani on 2017-01-06.
 */

public class Base64Utility
{
    public Bitmap decode(String encodedImage)
    {
        byte[] decodedString = Base64.decode(encodedImage, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        return decodedByte;
    }

    public String encodeJPEG(String imagePath)
    {
        return encode(Bitmap.CompressFormat.JPEG, imagePath);
    }

    public String encodePNG(String imagePath)
    {
        return encode(Bitmap.CompressFormat.PNG, imagePath);
    }

    private String encode(Bitmap.CompressFormat format, String imagePath)
    {
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(format, 100, baos);
        byte[] byteArrayImage = baos.toByteArray();
        String encodedImaged = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        return encodedImaged;
    }
}


