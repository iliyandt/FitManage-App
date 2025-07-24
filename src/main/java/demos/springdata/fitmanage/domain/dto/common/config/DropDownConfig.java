package demos.springdata.fitmanage.domain.dto.common.config;

public class DropDownConfig {
    private String url;
    private final boolean fromAnnotation;

    public DropDownConfig(String url, boolean fromAnnotation) {
        this.url = url;
        this.fromAnnotation = fromAnnotation;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isFromAnnotation() {
        return fromAnnotation;
    }
}
