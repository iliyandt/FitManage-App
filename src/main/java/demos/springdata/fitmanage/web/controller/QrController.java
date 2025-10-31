package demos.springdata.fitmanage.web.controller;

import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.member.response.MemberResponseDto;
import demos.springdata.fitmanage.domain.entity.User;
import demos.springdata.fitmanage.service.MemberService;
import demos.springdata.fitmanage.service.QrService;
import demos.springdata.fitmanage.service.UserService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
@RequestMapping("/api/v1/qr")
public class QrController {
    private final UserService userService;
    private final QrService qrService;


    public QrController(UserService userService, QrService qrService) {
        this.userService = userService;
        this.qrService = qrService;
    }

    @GetMapping(value = "/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
    @PreAuthorize("hasAuthority('MEMBER')")
    public ResponseEntity<byte[]> getQr(@PathVariable Long userId) throws Exception {
        User user = userService.findUserById(userId);
        String qrToken = qrService.generateQrToken(user);

        BufferedImage qrImage = qrService.generateQrCodeImage(qrToken, 250, 250);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
