package demos.springdata.fitmanage.service.impl;

import com.google.zxing.WriterException;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.service.UserService;
import io.jsonwebtoken.lang.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.awt.image.BufferedImage;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class QrServiceImplUTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private QrServiceImpl qrService;

    @Test
    void generateQrToken_ShouldReturnExistingToken_WhenTokenIsNotNull() {

        User user = new User();
        String existingToken = "existing-uuid-token-123";
        user.setQrToken(existingToken);

        String result = qrService.generateQrToken(user);

        Assertions.assertEquals(existingToken, result, "Should return the existing token without changing it");

        Mockito.verify(userService, never()).save(any(User.class));
    }

    @Test
    void generateQrToken_ShouldGenerateAndSaveNewToken_WhenTokenIsNull() {

        User user = new User();
        user.setQrToken(null);

        String result = qrService.generateQrToken(user);

        Assertions.assertNotNull(result, "Should generate a non-null token");
        Assertions.assertFalse(result.isEmpty(), "Token should not be empty");

        Assertions.assertEquals(result, user.getQrToken());

        Mockito.verify(userService, times(1)).save(user);
    }

    @Test
    void generateQrCodeImage_ShouldReturnBufferedImage_WhenInputIsValid() throws WriterException {

        String textToEncode = "https://damilsoft.com/check-in/user-123";
        int width = 200;
        int height = 200;

        BufferedImage image = qrService.generateQrCodeImage(textToEncode, width, height);

        Assertions.assertNotNull(image, "Generated image should not be null");
        Assertions.assertEquals(width, image.getWidth());
        Assertions.assertEquals(height, image.getHeight());
    }
}
