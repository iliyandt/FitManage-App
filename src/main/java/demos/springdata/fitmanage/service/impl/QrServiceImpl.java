package demos.springdata.fitmanage.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.service.QrService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.util.UUID;

@Service
public class QrServiceImpl implements QrService {
    private final UserService userService;

    @Autowired
    public QrServiceImpl(UserService userService) {
        this.userService = userService;
    }


    @Override
    public String generateQrToken(User user) {
        if (user.getQrToken() == null) {
            String token = UUID.randomUUID().toString();
            user.setQrToken(token);
            userService.save(user);
        }
        return user.getQrToken();
    }

    @Override
    public BufferedImage generateQrCodeImage(String text, int width, int height) throws WriterException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height);
        return MatrixToImageWriter.toBufferedImage(bitMatrix);
    }
}
