package demos.springdata.fitmanage.domain.dto.auth;

public class RefreshTokenRequestDto {
    private String token;

    public RefreshTokenRequestDto() {
    }

    public RefreshTokenRequestDto(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
