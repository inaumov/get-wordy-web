package get.wordy.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor()
@NoArgsConstructor
public class ClassInfoEditRequest extends ClassInfoRequest {

    private String classId;
}
