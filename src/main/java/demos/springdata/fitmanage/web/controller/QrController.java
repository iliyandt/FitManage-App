package demos.springdata.fitmanage.web.controller;

import com.google.zxing.WriterException;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.domain.dto.users.UserProfileDto;
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
@PreAuthorize("hasAnyAuthority('FACILITY_ADMIN', 'FACILITY_STAFF')")
public class QrController {
    private final UserService userService;
    private final MemberService memberService;
    private final QrService qrService;


    public QrController(UserService userService, MemberService memberService, QrService qrService) {
        this.userService = userService;
        this.memberService = memberService;
        this.qrService = qrService;
    }

    @GetMapping(value = "/{userId}", produces = MediaType.IMAGE_PNG_VALUE)
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

    @PostMapping("/qr")
    public ResponseEntity<ApiResponse<UserProfileDto>> checkInByQr(@RequestParam String qrToken) {
        User user = userService.findByQrToken(qrToken)
                .orElseThrow(() -> new RuntimeException("Invalid QR code"));

        UserProfileDto response = memberService.checkInMember(user.getId());

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
