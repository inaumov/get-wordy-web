package get.wordy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClassInfoRequest {

    private String name;
    private String format;
    private String level;
    private String material;
    private String notes;
}
