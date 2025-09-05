package co.com.pragma.api.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class PageDTO<T> {
    private List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;
}
