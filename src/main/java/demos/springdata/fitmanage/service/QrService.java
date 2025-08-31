package demos.springdata.fitmanage.service;


import com.google.zxing.WriterException;
import demos.springdata.fitmanage.domain.entity.User;

import java.awt.image.BufferedImage;

public interface QrService {
    String generateQrToken(User user);
    BufferedImage generateQrCodeImage(String text, int width, int height) throws WriterException;
}
