package demos.springdata.fitmanage.web.controller;


import demos.springdata.fitmanage.domain.dto.common.response.EnumOption;
import demos.springdata.fitmanage.domain.dto.auth.response.ApiResponse;
import demos.springdata.fitmanage.service.EnumService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("api/v1")
@PreAuthorize("hasAuthority('ROLE_GYM_ADMIN')")
public class EnumValuesController {

    private final EnumService enumService;

    public EnumValuesController(EnumService enumService) {
        this.enumService = enumService;
    }

    @GetMapping("/{enumName}/values")
    public ResponseEntity<ApiResponse<List<EnumOption>>> getEnumValues(@PathVariable String enumName) {
        List<EnumOption> options = enumService.getEnumOptions(enumName);
        return ResponseEntity.ok(ApiResponse.success(options));
    }
}
