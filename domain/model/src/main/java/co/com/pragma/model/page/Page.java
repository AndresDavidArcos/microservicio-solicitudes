package co.com.pragma.model.page;


import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class Page<T> {
    private List<T> content;
    private int currentPage;
    private long totalElements;
    private int totalPages;
}
