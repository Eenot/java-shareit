package shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestItemDto {

    private Long id;
    private String name;
    private Long ownerId;
    private String description;
    private Boolean available;
    private Long requestId;
}
